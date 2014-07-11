package com.aazayka.goodandbad.app;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


public class ShowList extends Fragment {

    public static final String TAG = "ShowList";
    static final int REQUEST_SAVE_ITEM = 1;
    static final int REQUEST_UPDATE_ITEM = 2;

    ListView itemsListView;
    Button tagFilterButton;

    private ArrayList<Item> items;
    ItemsArrayAdapter arrayAdapter;
    private int updatePosition;
    private Item oldItem;

    public void filterByTag(Long tag_id, String tag_name) {
        items = DBAdapter.get().getFilteredItems(tag_id);
        Log.d(TAG, "Filter " + tag_id + " count = " + items.size());
        arrayAdapter.clear();
        arrayAdapter.addAll(items);
        arrayAdapter.notifyDataSetChanged();
        if (tag_id != null && tag_id != 0) {
            tagFilterButton.setText(tag_name);
            tagFilterButton.setVisibility(View.VISIBLE);
        } else {
            tagFilterButton.setText("");
            tagFilterButton.setVisibility(View.GONE);
        }
    }

    public void refreshOnAddItem(long item_id) {
        Item item = DBAdapter.get().getItem(item_id);
        arrayAdapter.add(item);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_show_list, container, false);
        itemsListView = (ListView) v.findViewById(R.id.itemsListView);
        tagFilterButton= (Button) v.findViewById(R.id.tagFilterButton);
        tagFilterButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                filterByTag(null, null);
            }
        });
        tagFilterButton.setVisibility(View.GONE);

        items = DBAdapter.get().getFilteredItems(null);
        arrayAdapter = new ItemsArrayAdapter(items, getFragmentManager());
        itemsListView.setAdapter(arrayAdapter);

        itemsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                                                 @Override
                                                 public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                     oldItem = arrayAdapter.getItem(i);
                                                     Log.d(TAG, "Item " + oldItem.comments + " was clicked");
                                                     Intent intent = new Intent(getActivity(), MainActivity.class);
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

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UPDATE_ITEM && resultCode == getActivity().RESULT_OK) {
            Long item_id = data.getLongExtra(MainActivity.EXTRA_ITEM_ID, 0);
            Item item = DBAdapter.get().getItem(item_id);
            arrayAdapter.remove(oldItem);
            arrayAdapter.insert(item, updatePosition);
            arrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.show_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_insert) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
