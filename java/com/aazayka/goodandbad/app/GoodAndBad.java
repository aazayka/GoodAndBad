package com.aazayka.goodandbad.app;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.aazayka.goodandbad.app.R;

public class GoodAndBad extends Activity implements ActionBar.TabListener {

    private static final String TAG = "GoodAndBad";
    static final int REQUEST_SAVE_ITEM = 1;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_and_bad);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.good_and_bad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert:
                Intent intent = new Intent(this, MainActivity.class);
                startActivityForResult(intent, REQUEST_SAVE_ITEM);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SAVE_ITEM && resultCode == RESULT_OK) {
            Long item_id = data.getLongExtra(MainActivity.EXTRA_ITEM_ID, 0);
            ShowList showlist = (ShowList) mSectionsPagerAdapter.getRegisteredFragment(SectionsPagerAdapter.ITEM_LIST_PAGE);
            showlist.refreshOnAddItem(item_id);
            ((PlaceholderFragment) mSectionsPagerAdapter.getRegisteredFragment(SectionsPagerAdapter.TAG_PAGE)).refreshList();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private static final int PAGE_COUNT = 2;
        public static final int TAG_PAGE = 0;
        public static final int ITEM_LIST_PAGE = 1;

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d(TAG, "getItem " + position);
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return new ShowList();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case TAG_PAGE:
                    return getString(R.string.title_section1).toUpperCase(l);
                case ITEM_LIST_PAGE:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        ListView tagsListView;
        SimpleCursorAdapter tagsListAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
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
                    SectionsPagerAdapter pagerAdapter = (SectionsPagerAdapter) viewpager.getAdapter();
                    ShowList showlist = (ShowList) pagerAdapter.getRegisteredFragment(SectionsPagerAdapter.ITEM_LIST_PAGE);

                    Cursor cursor = (Cursor) tagsListAdapter.getItem(i);
                    long tag_id = cursor.getLong(cursor.getColumnIndex("_id"));

                    if (showlist == null) {
                        Log.e(TAG, "Error on get ShowList fragment");
                    } else {
                        showlist.filterByTag(tag_id);
                        viewpager.setCurrentItem(SectionsPagerAdapter.ITEM_LIST_PAGE);
                    }
                }
            }
            );


            return rootView;
        }
    }

}
