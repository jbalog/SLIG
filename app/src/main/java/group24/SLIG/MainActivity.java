package group24.SLIG;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import group24.SLIG.bluetooth.BluetoothLeService;
import group24.SLIG.bluetooth.DeviceScanActivity;
import group24.SLIG.bluetooth.ServiceConnectionService;
import group24.SLIG.tabs.FragmentLibrary;
import group24.SLIG.tabs.FragmentTranslator;
import group24.SLIG.tabs.MyPageAdapter;
import group24.SLIG.tabs.MyTabFactory;

/** Main activity...*/

public class MainActivity extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

    MyPageAdapter pageAdapter;
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private Toolbar mActionBartoolbar;
    private Toolbar mSupportActionBar;
    private Intent bluetoothIntent;
    private String gestureArray = " ";

    public void onDestroy() {
        super.onDestroy();
        unbindService(ServiceConnectionService.getServiceConnection());
    }

    public void onResume() {
        super.onResume();
        bluetoothIntent = new Intent(this, BluetoothLeService.class);
        bindService(bluetoothIntent, ServiceConnectionService.getServiceConnection(), Context.BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        BluetoothGattCharacteristic bleChar = ServiceConnectionService.getmBleCharacteristic();
        if(bleChar != null) {
            final int charaProp = bleChar.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                ServiceConnectionService.getmBluetoothLeService().readCharacteristic(bleChar);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                ServiceConnectionService.getmBluetoothLeService().setCharacteristicNotification(bleChar, true);
            }
        }
    }

    public void onPause() {
        super.onPause();
        unbindService(ServiceConnectionService.getServiceConnection());
        unregisterReceiver(mGattUpdateReceiver);
    }

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
                startBleScan();
                return true;
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        // Tab Initialization
        initialiseTabHost();

        // Fragments and ViewPager Initialization
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setOnPageChangeListener(MainActivity.this);
    }

    //Bluetooth configuration
    private void startBleScan() {
        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                /** This is where the "gesture data" is sent to displayData method below **/
                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                displayData(data);
            }
        }
    };

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

            // Put your Fragments here
            FragmentTranslator f1 = FragmentTranslator.newInstance("");
            FragmentLibrary f2 = FragmentLibrary.newInstance("");
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

    private void displayData(String data) {

        if (data != null) {
            TextView primaryGesture = (TextView) findViewById(R.id.txtViewPrimaryGesture);
            TextView gestureList = (TextView) findViewById(R.id.txtViewGestureList);
            Character letter = data.charAt(0);
            String gesture = letter.toString();
            if(Character.isLetter(letter)) {
                primaryGesture.setText(gesture);
                gestureArray = gestureArray + gesture;
                Character testChar = gestureArray.charAt(gestureArray.length() - 2);
                if(testChar != letter)    {
                    gestureList.setText(gestureList.getText() + letter.toString());
                }
            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}

