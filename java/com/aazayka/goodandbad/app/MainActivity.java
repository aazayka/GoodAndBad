package com.aazayka.goodandbad.app;

//TODO Сделать равномерные отступы

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final String DIALOG_IMAGE = "image";

    EditText tagsEditText;
    EditText commentsEditText;

    RadioGroup typeRadioGroup;
    RadioButton goodRadioButton;
    RadioButton badRadioButton;

    ImageView photoImageView;

    Button saveButton;

    Item item;

    File tempFile = null;

    static final int REQUEST_TAKE_PHOTO = 1;
    public static final String EXTRA_ITEM_ID = "com.aazayka.goodandbad.item_id";

    public void onButtonSaveClick(View v){
        String tags = tagsEditText.getText().toString();
        String comments = commentsEditText.getText().toString();

        String isGood = goodRadioButton.isChecked()?"Y":"N";

        item.setTags(tags);
        item.setComments(comments);
        item.setIsGood(isGood);

        Long item_id = item.Save();

        Intent intent = this.getIntent();
        intent.putExtra(EXTRA_ITEM_ID, item_id);
        this.setResult(RESULT_OK, intent);
        this.finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typeRadioGroup = (RadioGroup) findViewById(R.id.typeRadioGroup);
        goodRadioButton = (RadioButton) findViewById(R.id.goodRadioButton);
        badRadioButton = (RadioButton) findViewById(R.id.badRadioButton);

        tagsEditText = (EditText) findViewById(R.id.tagsEditText);
        commentsEditText = (EditText) findViewById(R.id.commentsEditText);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        saveButton = (Button) findViewById(R.id.saveButton);

        //Форма открывается для обновления
        Long item_id = this.getIntent().getLongExtra(EXTRA_ITEM_ID, 0);
        if (item_id != 0){
            item = DBAdapter.get().getItem(item_id);
            tagsEditText.setText(item.getTags());
            commentsEditText.setText(item.getComments());
            goodRadioButton.setChecked(item.getIsGood().equals("Y"));
            badRadioButton.setChecked(item.getIsGood().equals("N"));
            showPicture();
        } else {
            item = new Item();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if (item.getImage().isImageExists()) Image.cleanImageView(photoImageView);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                tempFile = Image.createTempFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Can't create temp file for picture", ex);
            }
            // Continue only if the File was successfully created
            if (tempFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(tempFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "Current image width=" + photoImageView.getWidth() + "; current image height = " + photoImageView.getHeight());
            if (item.image.isImageExists()) item.image.deleteImage();
            item.setImage(tempFile.getAbsolutePath());
            showPicture();
        }
    }

    private void showPicture() {
        if (item.getImage().isImageExists()) {
            photoImageView.setImageBitmap(item.getImage().resize(photoImageView.getMaxWidth(), photoImageView.getMaxHeight()));
        } else {
            photoImageView.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }

    public void onImageClick(View v){
        if (!item.image.isImageExists())
            onTakePhotoImageButtonClick(v);
        FragmentManager fm = getFragmentManager();
        String path = item.image.getImageFilePath();
        ImageFragment.newInstance(path)
                .show(fm, DIALOG_IMAGE);
    }
    public void onTakePhotoImageButtonClick(View v) {
        dispatchTakePictureIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = menuItem.getItemId();

        switch (id) {
//           case R.id.menu_delete_item:
//                item.deleteItem();
//                this.finish();
//                return true;
            case R.id.menu_delete_photo:
                item.deletePhoto();
                showPicture();
                return true;

        }
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(menuItem);
    }

}
