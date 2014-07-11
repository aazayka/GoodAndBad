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

                                                    Cursor cursor = (Cursor) tagsListAdapter.getItem(i);
                                                    long tag_id = cursor.getLong(cursor.getColumnIndex("_id"));
                                                    String tag_name = cursor.getString(cursor.getColumnIndex("tag"));

                                                    if (showlist == null) {
                                                        Log.e(TAG, "Error on get ShowList fragment");
                                                    } else {
                                                        showlist.filterByTag(tag_id, tag_name);
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

                // start the CAB using the ActionMode.Callback defined above
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });


        return rootView;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // called when the action mode is created; startActionMode() was called
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            // assumes that you have "contexual.xml" menu resources
            inflater.inflate(R.menu.context_good_and_bad, menu);
            return true;
        }

        // the following method is called each time
        // the action mode is shown. Always called after
        // onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // called when the user selects a contextual menu item
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete_tag:
                    show();
                    // the Action was executed, close the CAB
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        // called when the user exits the action mode
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            selectedItem = -1;
        }
    };

    private void show() {
        Toast.makeText(getActivity(),
                String.valueOf(selectedItem), Toast.LENGTH_LONG).show();
    }

}