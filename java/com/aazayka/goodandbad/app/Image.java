package com.aazayka.goodandbad.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andrey.zaytsev on 03.06.2014.
 */
public class Image {
    public static final String TAG = "Image";

    String imageFilePath;

    public Image(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public Boolean isImageExists() {
        return !imageFilePath.equals("");
    }

    private static File getStorageDir(){
        return MyApp.getAppContext().getExternalFilesDir(null);
    }
    public static File createTempFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getStorageDir();
        File imageFile;
        Log.d(TAG, "Image: " + storageDir.getAbsolutePath() + "/" + imageFileName);
        imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return imageFile;
    }

    public Bitmap resize(int width, int height) {
        if (!isImageExists()){
            return null;
        }
        
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilePath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / width, photoH / height);
        Log.d(TAG, "Scale factor: " + scaleFactor);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(imageFilePath, bmOptions);

    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void deleteImage() {
        if (isImageExists()) {
            File file = new File(getStorageDir(), this.getImageFilePath());
            if (file.delete()) {
                Log.d(TAG, "File " + this.getImageFilePath() + " deleted");
            } else {
                Log.e(TAG, "File " + this.getImageFilePath() + " not deleted");
            }
            this.imageFilePath = "";
        }
    }

    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable))
            return;
// Стирание изображения для экономии памяти
        Bitmap b = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (b != null && !b.isRecycled()) {
            b.recycle();
            b = null;
        }
        imageView.setImageDrawable(null);
    }
}
