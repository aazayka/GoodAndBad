package com.aazayka.goodandbad.app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by andrey.zaytsev on 02.06.2014.
 */
public class Item {
    private static final String TAG = "Item";
    String tags;
    String comments;
    String isGood;
    Image image;
    Long id;

    DBAdapter db;

    public Item() {
        this.image = new Image(new String());
    }

    public Item(String tags, String comments, String isGood, String imageFilePath, Long id) {
        this.tags = tags;
        this.comments = comments;
        this.isGood = isGood;
        this.image = new Image((String) imageFilePath);
        this.id = id;
    }

    public Long Save() {
        db = DBAdapter.get();
        Long lastItemId = this.getId();
        if (lastItemId == null || lastItemId.equals(0)) {
            db.insertItem(isGood, comments, image.getImageFilePath());
            lastItemId = db.getLastInsertId("Items");
        } else {
            db.updateItem(lastItemId, isGood, comments, image.getImageFilePath());
            db.deleteItemTags(lastItemId);
        }
        tags = tags.trim();
        if (tags.equals("")) {
            tags = MyApp.getAppContext().getString(R.string.empty_tag);
        }

        String[] tagsArray = tags.split(",|;");
        for (int i = 0; i < tagsArray.length; i++) {
            String currentTag = tagsArray[i].trim();
            Log.d(TAG, "Process tag = " + currentTag);
            if (!currentTag.equals("")) {
                db.insertTag(currentTag);

                Long tagId = db.getTagId(currentTag);
                db.insertItemTag(lastItemId, tagId);
            }
        }

        return lastItemId;
    }

    public void deletePhoto() {
        this.image.deleteImage();
    }

    public void deleteItem() {
        db = DBAdapter.get();
        db.deleteItem(id);
    }
    public String getTags() {
        return tags;
    }

    public String getComments() {
        return comments;
    }

    public String getIsGood() {
        return isGood;
    }

    public Long getId() {
        return id;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setIsGood(String isGood) {
        this.isGood = isGood;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImage(String imageFilePath) {
        this.image = new Image(imageFilePath);
    }

}
