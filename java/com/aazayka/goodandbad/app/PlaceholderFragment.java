package com.aazayka.goodandbad.app;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "PlaceholderFragment";
    ListView tagsListView;
    SimpleCursorAdapter tagsListAdapter;

    protected Object mActionMode;
    public int selectedItem = -1;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refreshList (){
        tagsListAdapter.getCursor().requery();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_good_and_bad, container, false);
        tagsListView = (ListView)rootView.findViewById(R.id.tagsListView);

        Cursor cursor = DBAdapter.get().getTagsCursor();
        getActivity().startManagingCursor(cursor);
        // now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        tagsListAdapter = new SimpleCursorAdapter(
                MyApp.getAppContext(), // Context.
                android.R.layout.simple_list_item_1, // Specify the row template
                cursor, // Pass in the cursor to bind to.
                new String[] {"tag"},
                // Parallel array of which template objects to bind to those
                // columns.
                new int[] { android.R.id.text1});

        // Bind to our new adapter.
        tagsListView.setAdapter(tagsListAdapter);

        tagsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                    ViewPager viewpager = (ViewPager) getActivity().findViewById(R.id.pager);
                                                    GoodAndBad.SectionsPagerAdapter pagerAdapter = (GoodAndBad.SectionsPagerAdapter) viewpager.getAdapter();
                                                    ShowList showlist = (ShowList) pagerAdapter.getRegisteredFragment(GoodAndBad.SectionsPagerAdapter.ITEM_LIST_PAGE);

                                                    if (showlist == null) {
                                                        Log.e(TAG, "Error on get ShowList fragment");
                                                    } else {
                                                        showlist.filterByTag(getTagId(i), getTagName(i));
                                                        viewpager.setCurrentItem(GoodAndBad.SectionsPagerAdapter.ITEM_LIST_PAGE);
                                                    }
                                                }
                                            }
        );

        tagsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                if (mActionMode != null) {
                    return false;
                }
                selectedItem = position;

                showMenu(view);
                view.setSelected(true);
                return true;
            }
        });


        return rootView;
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);

        // This activity implements OnMenuItemClickListener
        popup.inflate(R.menu.context_good_and_bad);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_delete_tag:
                        delete_tag();
                        return true;
                    default:
                        return false;
                }

            }
        });
        popup.show();
    }

    private long getTagId(int position) {
        Cursor cursor = (Cursor) tagsListAdapter.getItem(position);
        return cursor.getLong(cursor.getColumnIndex("_id"));
    }

    private String getTagName(int position) {
        Cursor cursor = (Cursor) tagsListAdapter.getItem(position);
        return cursor.getString(cursor.getColumnIndex("tag"));
    }

    private void delete_tag() {
        DBAdapter db = DBAdapter.get();
        Long tag_id = getTagId(selectedItem);
        Log.d(TAG, "Delete tag " + getTagName(selectedItem));
        if (db.isEmptyTag(tag_id)) {
            db.deleteTag(tag_id);
            refreshList();
        }
        else
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.error_delete_tag), Toast.LENGTH_LONG).show();
    }



}