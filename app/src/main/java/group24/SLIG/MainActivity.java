package group24.SLIG;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import group24.SLIG.bluetooth.DeviceScanActivity;
import group24.SLIG.tabs.FragmentLibrary;
import group24.SLIG.tabs.FragmentTranslator;
import group24.SLIG.tabs.MyPageAdapter;
import group24.SLIG.tabs.MyTabFactory;
/**
 * Main activity...
 */

public class MainActivity extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

    MyPageAdapter pageAdapter;
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private Toolbar mActionBartoolbar;
    private Toolbar mSupportActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup main menu toolbar
        mActionBartoolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mActionBartoolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_slig);
        mActionBartoolbar.inflateMenu(R.menu.menu_main);

        // Set an OnMenuItemClickListener to handle menu item clicks
        mActionBartoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Go to BLE setup activity when bluetooth button pressed
                // TODO... start new scan OR just return to BLE activity?
                startBleScan();
                return true;
            }
        });

        // Start Bluetooth configuration
        startBleScan();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Tab Initialization
        initialiseTabHost();

        // Fragments and ViewPager Initialization
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setOnPageChangeListener(MainActivity.this);
    }

   /* // Inflate a menu to be displayed in the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Go to BLE scan when button pressed
            case R.id.bleButton:
                startBleScan();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }*/

    //Bluetooth configuration
    private void startBleScan() {
        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);
    }

    // Method to add a TabHost
        private static void AddTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
            tabSpec.setContent(new MyTabFactory(activity));
            tabHost.addTab(tabSpec);
        }

        // Manages the Tab changes, synchronizing it with Pages
        public void onTabChanged(String tag) {
            int pos = this.mTabHost.getCurrentTab();
            this.mViewPager.setCurrentItem(pos);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        // Manages the Page changes, synchronizing it with Tabs
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            int pos = this.mViewPager.getCurrentItem();
            this.mTabHost.setCurrentTab(pos);
        }

        @Override
        public void onPageSelected(int arg0) {
        }

        private List<Fragment> getFragments(){
            List<Fragment> fList = new ArrayList<Fragment>();

            // TODO Put here your Fragments
            FragmentTranslator f1 = FragmentTranslator.newInstance("Translator Fragment");
            FragmentLibrary f2 = FragmentLibrary.newInstance("Library Fragment");
            fList.add(f1);
            fList.add(f2);

            return fList;
        }

        // Tabs Creation
        private void initialiseTabHost() {
            mTabHost = (TabHost) findViewById(android.R.id.tabhost);
            mTabHost.setup();

            // TODO Put here your Tabs
            MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Translator").setIndicator("Translator"));
            MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Gesture Library").setIndicator("Gesture Library"));

            mTabHost.setOnTabChangedListener(this);
        }

    public void setSupportActionBar(Toolbar supportActionBar) {
        mSupportActionBar = supportActionBar;
    }

    public Toolbar getSupportActionBar() {
        return mSupportActionBar;
    }
}

