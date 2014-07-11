package com.aazayka.goodandbad.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by andrey.zaytsev on 29.05.2014.
 */
public class DBAdapter {
    private static final String TAG = "DBAdapter";
    public static final String DATABASE_NAME = "GoodAndBad2";

    private SQLiteDatabase db;
    private static DBAdapter sDBAdapter;

    private Context context;

    public DBAdapter(){
        context = MyApp.getAppContext();
        open();
    }

    public String getTags(Long id) {
        String tags = new String();
        Cursor tagsCursor = db.rawQuery("SELECT t.tag FROM Tags t, Items_Tags it WHERE it.item_id = ? AND it.tag_id = t.id", new String[]{id.toString()});
//        if (tagsCursor.moveToFirst()) {
//            for (int i = 0; i<tagsCursor.getCount();i++) {
        if (tagsCursor.moveToFirst()) {
            while (!tagsCursor.isAfterLast()) {
                tags = tags + tagsCursor.getString(tagsCursor.getColumnIndex("tag")) + ", ";
                tagsCursor.moveToNext();
            }
        }

        if (!tags.equals("")) {
            tags = tags.substring(0, tags.length() - 2);
        }
        tagsCursor.close();

        return tags;
    }

    public Item getItem(Long id){
        Item item;
        open();
        Cursor cursor = db.query(
                "Items",
                new String[]{"comments", "ifnull(image_path, '') image_path", "is_good", "id"},
                "id = ?",
                new String[]{id.toString()},
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            item = new Item(
                    null,
                    cursor.getString(cursor.getColumnIndex("comments")),
                    cursor.getString(cursor.getColumnIndex("is_good")),
                    cursor.getString(cursor.getColumnIndex("image_path")),
                    cursor.getLong(cursor.getColumnIndex("id"))
            );
            item.setTags(getTags(id));
            cursor.close();
            close();
            return item;
        } else {
            cursor.close();
            close();
            throw new NoSuchElementException();
        }
    }

    public ArrayList<Item> getFilteredItems(Long tag_id){
        ArrayList<Item> items = new ArrayList<Item>();
        String[] tag_filter = new String[]{};
        if (tag_id != null && tag_id != 0) tag_filter = new String[]{tag_id.toString()};
        open();
        Cursor cursor = db.rawQuery(
                "SELECT i.comments, ifnull(i.image_path, '') image_path, i.is_good, i.id FROM Items i, Items_Tags it WHERE i.id = it.item_id AND it.tag_id = ifnull(?, it.tag_id) ",
                tag_filter);
        cursor.moveToFirst();
        for (Integer i=0; i < cursor.getCount(); i++) {
            Item item = new Item(
                    getTags(cursor.getLong(cursor.getColumnIndex("id"))),
                    cursor.getString(cursor.getColumnIndex("comments")),
                    cursor.getString(cursor.getColumnIndex("is_good")),
                    cursor.getString(cursor.getColumnIndex("image_path")),
                    cursor.getLong(cursor.getColumnIndex("id"))
            );
            items.add(item);
            Log.d(TAG, "added item id=" + item.id + " comments=" + item.comments);
            cursor.moveToNext();
        }

        close();
        return items;
    }


    private void open(){
//        Boolean deleteResult = SQLiteDatabase.deleteDatabase(new File("/data/data/com.aazayka.goodandbad.app/databases/" + "GoodAndBad"));
        db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
//        db = SQLiteDatabase.openOrCreateDatabase(DATABASE_NAME, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Tags(id INTEGER PRIMARY KEY AUTOINCREMENT, tag VARCHAR COLLATE NOCASE UNIQUE);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Items(id INTEGER PRIMARY KEY AUTOINCREMENT, is_good VARCHAR, comments TEXT, image_path TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS Items_Tags(item_id INTEGER, tag_id INTEGER, FOREIGN KEY(item_id) REFERENCES Items(id), FOREIGN KEY(tag_id) REFERENCES Tags(id));");

    }

    public static DBAdapter get() {
        if (sDBAdapter == null) {
            sDBAdapter = new DBAdapter ();
        }

        return sDBAdapter;
    }

    public long getLastInsertId(String tableName) {
        open();
        long index = 0;
        Cursor cursor = db.query(
                "sqlite_sequence",
                new String[]{"seq"},
                "name = ?",
                new String[]{tableName},
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            index = cursor.getLong(cursor.getColumnIndex("seq"));
        }
        cursor.close();
        Log.d(TAG, "for table " + tableName + " last sequence is " + Long.toString(index));
        close();
        return index;
    }

    public long getTagId(String tag) {
        open();
        Cursor cursor = db.rawQuery("SELECT id FROM Tags WHERE tag = ? COLLATE NOCASE", new String[]{tag});
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        Log.d(TAG, "Найден тег " + tag + " c идентификатором = " + result);
        close();
        return result;
    }

    public void insertTag(String tag) {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues.put("tag", tag.trim());
        try {
            long result = db.insertOrThrow("Tags", null, initialValues);
            if (result == -1) {
                Log.d("TAG", "Ошибка вставки тега");
            }
        } catch (SQLiteConstraintException e) {
            Log.d(TAG, "Тег " + tag + " уже существует");
        }
        close();
    }

    public void insertItemTag(Long itemId, Long tagId) {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues = new ContentValues();
        initialValues.put("tag_id", tagId);
        initialValues.put("item_id", itemId);
        long insertResult = db.insert("Items_Tags", null, initialValues);
        if (insertResult == -1) {
            Log.d("TAG", "Ошибка вставки вещи-тега");
        }
        close();
    }

    public void insertItem(String isGood, String comments, String photoPath) {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues = new ContentValues();
        initialValues.put("is_good", isGood);
        initialValues.put("comments", comments);
        initialValues.put("image_path", photoPath);
        long insertResult = db.insert("Items", null, initialValues);
        if (insertResult == -1) {
            Log.d("TAG", "Error on insert item");
        }
        close();
    }

    public void close(){
        db.close();
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }


    public void updateItem(Long itemId, String isGood, String comments, String imageFilePath) {
        open();
        ContentValues initialValues = new ContentValues();
        initialValues = new ContentValues();
        initialValues.put("is_good", isGood);
        initialValues.put("comments", comments);
        initialValues.put("image_path", imageFilePath);
        db.update("Items", initialValues, "id=?", new String[]{itemId.toString()});
        db.close();
    }

    public void deleteItemTags(Long itemId) {
        open();
        db.delete("Items_Tags", "item_id=?", new String[]{itemId.toString()});
        db.close();
    }

    public void deleteItem(Long itemId) {
        this.deleteItemTags(itemId);
        open();
        db.delete("Items", "id=?", new String[]{itemId.toString()});
        db.close();
    }
    public void deleteTag(Long tag_id) {
        open();
        db.delete("Tags", "id=?", new String[]{tag_id.toString()});
        db.close();
    }

    public Cursor getTagsCursor() {
        return rawQuery("SELECT id _id, tag FROM Tags ORDER BY tag", null);
    }

    public boolean isEmptyTag(Long tag_id) {
        int cnt = 0;
        open();
        Cursor cursor = db.query(
                "Items_Tags",
                new String[]{"count(*) cnt"},
                "tag_id = ?",
                new String[]{tag_id.toString()},
                null,
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            cnt = cursor.getInt(cursor.getColumnIndex("cnt"));
        }
        Log.d(TAG, "For tag_id=" + tag_id + " count = " + cnt);
        db.close();
        return (cnt == 0);

    }

}