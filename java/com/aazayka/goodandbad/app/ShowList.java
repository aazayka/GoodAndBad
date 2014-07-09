package com.aazayka.goodandbad.app;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;


public class ShowList extends ActionBarActivity {

    public static final String TAG = "ShowList";
    static final int REQUEST_SAVE_ITEM = 1;
    static final int REQUEST_UPDATE_ITEM = 2;
    public static final String EXTRA_TAG_ID = "com.aazayka.goodandbad.tag_id";

    ListView itemsListView;
    ImageButton addImageButton;

    private ArrayList<Item> items;
    ItemsArrayAdapter arrayAdapter;
    private int updatePosition;
    private Item oldItem;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        itemsListView = (ListView) findViewById(R.id.itemsListView);
        addImageButton = (ImageButton) findViewById(R.id.addImageButton);

        addImageButton.setOnClickListener(new ImageButton.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  Intent intent = new Intent(ShowList.this, MainActivity.class);
                                                  startActivityForResult(intent, REQUEST_SAVE_ITEM);
                                              }
                                          }
        );

        Long tag_id = this.getIntent().getLongExtra(EXTRA_TAG_ID, 0);
        Log.d(TAG, "tag_id=" + tag_id.toString());
        items = DBAdapter.get().getFilteredItems(tag_id);
        arrayAdapter = new ItemsArrayAdapter(items);
        itemsListView.setAdapter(arrayAdapter);

        itemsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                                                 @Override
                                                 public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                     oldItem = arrayAdapter.getItem(i);
                                                     Log.d(TAG, "Item " + oldItem.comments + " was clicked");
                                                     Intent intent = new Intent(ShowList.this, MainActivity.class);
                                                     intent.putExtra(MainActivity.EXTRA_ITEM_ID, oldItem.id);
                                                     updatePosition = i;
                                                     startActivityForResult(intent, REQUEST_UPDATE_ITEM);
                                                 }
                                             }
        );

        itemsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        itemsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener()
        {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked)
            {
                // Здесь Вы можете что-то сделать, когда элементы выбираются
                // или их выбор отменяется, как например можно обновить
                // заголовок в CAB.
                final int checkedCount = itemsListView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " " + getResources().getString(R.string.selected_for_delete));
                // Calls toggleSelection method from ListViewAdapter Class
                arrayAdapter.toggleSelection(position);
                itemsListView.getChildAt(position).setBackgroundColor(arrayAdapter.getItemColor(position));
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item)
            {
                // Код обработки кликов на действиях в CAB.
                switch (item.getItemId())
                {
                    case R.id.menu_delete_item:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        SparseBooleanArray selected = arrayAdapter.getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Item selectedItem = arrayAdapter.getItem(selected.keyAt(i));
                                // Remove selected items following the ids
                                selectedItem.deleteItem();
                                arrayAdapter.remove(selectedItem);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                // Встраивание меню для CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.show_list, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode)
            {
                // Здесь Вы можете сделать нужные обновления для activity,
                // когда CAB удален. По умолчанию, выбор элементов
                // отменяется (для тех, которые были выбраны).
                arrayAdapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                // Здесь Вы можете выполнить обновления для CAB в ответ
                // на запрос invalidate().
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SAVE_ITEM && resultCode == RESULT_OK) {
            Long item_id = data.getLongExtra(MainActivity.EXTRA_ITEM_ID, 0);
            Item item = DBAdapter.get().getItem(item_id);
            arrayAdapter.add(item);
            arrayAdapter.notifyDataSetChanged();
        }

        if (requestCode == REQUEST_UPDATE_ITEM && resultCode == RESULT_OK) {
            Long item_id = data.getLongExtra(MainActivity.EXTRA_ITEM_ID, 0);
            Item item = DBAdapter.get().getItem(item_id);
            arrayAdapter.remove(oldItem);
            arrayAdapter.insert(item, updatePosition);
            arrayAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
