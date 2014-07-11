package com.aazayka.goodandbad.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by andrey.zaytsev on 02.06.2014.
 */
public class ItemsArrayAdapter extends ArrayAdapter<Item> {
    private static final String DIALOG_IMAGE = "list_image";
    public static final String TAG = "ItemsArrayAdapter";
    private ArrayList<Item> items;
    private SparseBooleanArray mSelectedItemsIds;
    private FragmentManager fragment_manager;

    public ItemsArrayAdapter(ArrayList<Item> items, FragmentManager fragment_manager) {
        super(MyApp.getAppContext(), R.layout.item_layout_good, items);
        this.items = items;
        this.fragment_manager = fragment_manager;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Item item = getItem(position);
//        if (convertView == null) {
            convertView =  ((LayoutInflater) MyApp.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(getItemResource(item), null);
//        }

        Log.d(TAG, "position " + position + "; item_id=" + item.getId().toString() + " is good = " + item.getIsGood());
        TextView listCommentsTextView = (TextView) convertView.findViewById(R.id.listCommentsTextView);
        TextView listTagsTextView = (TextView) convertView.findViewById(R.id.listTagsTextView);
        ImageView listPhotoImageView = (ImageView) convertView.findViewById(R.id.listPhotoImageView);
        listPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getImage().isImageExists()) {
                    ImageFragment.newInstance(item.getImage().getImageFilePath())
                            .show(fragment_manager, DIALOG_IMAGE);
                }
            }
        });
        listCommentsTextView.setText(item.getComments());
        listTagsTextView.setText(item.getTags());

        // Я перекрашу холодильник в желтый цвет
        if (mSelectedItemsIds.get(position)) {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        if (item.getImage().isImageExists()) {
            Log.d(TAG, "Show image " + item.getImage().getImageFilePath());
            listPhotoImageView.setImageBitmap(new Image(item.getImage().getImageFilePath()).resize(100, 100));
        } else {
            listPhotoImageView.setImageDrawable(null);
        }

        return convertView;
    }

    private int getItemResource(Item item) {
        if (item.getIsGood().equals("Y")) return R.layout.item_layout_good;
        else return R.layout.item_layout_bad;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


}
