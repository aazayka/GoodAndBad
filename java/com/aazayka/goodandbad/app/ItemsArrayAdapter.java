package com.aazayka.goodandbad.app;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by andrey.zaytsev on 02.06.2014.
 */
public class ItemsArrayAdapter extends ArrayAdapter<Item> {
    public static final String TAG = "ItemsArrayAdapter";
//    private final Context context;
    private ArrayList<Item> items;
    private SparseBooleanArray mSelectedItemsIds;

    public ItemsArrayAdapter(ArrayList<Item> items) {
        super(MyApp.getAppContext(), R.layout.item_layout, items);
//        this.context = context;
        this.items = items;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =  ((LayoutInflater) MyApp.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.item_layout, null);
        }

        Item item = getItem(position);
        Log.d(TAG, "position " + position + "; item_id=" + item.getId().toString() + " is good = " + item.getIsGood());
        TextView listCommentsTextView = (TextView) convertView.findViewById(R.id.listCommentsTextView);
        TextView listTagsTextView = (TextView) convertView.findViewById(R.id.listTagsTextView);
        ImageView listPhotoImageView = (ImageView) convertView.findViewById(R.id.listPhotoImageView);
        listCommentsTextView.setText(item.getComments());
        listTagsTextView.setText(item.getTags());

        convertView.setBackgroundColor(getItemColor(position));

        if (!item.getImage().getImageFilePath().equals("")) {
            Log.d(TAG, "Show image " + item.getImage().getImageFilePath());
            listPhotoImageView.setImageBitmap(new Image(item.getImage().getImageFilePath()).resize(100, 100));
        } else {
            listPhotoImageView.setImageDrawable(null);
        }

        return convertView;
    }

    public int getItemColor(int position) {
        Item item = items.get(position);
        if (mSelectedItemsIds.get(position)) {
            return Color.LTGRAY;
        } else if (item.getIsGood().equals("Y")) {
            return Color.GREEN;
        } else {
            return Color.RED;
        }
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
