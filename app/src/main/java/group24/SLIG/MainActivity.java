package group24.SLIG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import group24.SLIG.bluetooth.BluetoothLeService;
import group24.SLIG.bluetooth.DeviceScanActivity;
import group24.SLIG.bluetooth.ServiceConnectionService;
import group24.SLIG.tabs.FragmentLearning;
import group24.SLIG.tabs.FragmentTranslator;
import group24.SLIG.tabs.MyPageAdapter;
import group24.SLIG.tabs.MyTabFactory;

import static group24.SLIG.R.*;

/** Main activity...*/

public class MainActivity extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

    MyPageAdapter pageAdapter;
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private Toolbar mActionBartoolbar;
    private Toolbar mSupportActionBar;
    private Intent bluetoothIntent;
    private String gestureArray = " ";
    final Random rnd = new Random();

    int[] mImageArray = {drawable.img_0, drawable.img_1, drawable.img_2, drawable.img_3, drawable.img_4, drawable.img_5};
    String[] mLetterArray = {"A", "B", "C", "D", "E", "F"};
    private int mLengthOfArray = mImageArray.length;
    private float mScaleFactor = (float) 3;

    public void onDestroy() {
        super.onDestroy();
        unbindService(ServiceConnectionService.getServiceConnection());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
        setContentView(layout.activity_main);

        // Setup main menu toolbar
        mActionBartoolbar = (Toolbar) findViewById(id.toolbar_main);
        setSupportActionBar(mActionBartoolbar);
        getSupportActionBar().setTitle(string.app_name);
        getSupportActionBar().setLogo(mipmap.ic_launcher_slig);
        mActionBartoolbar.inflateMenu(menu.menu_main);

        // Set an OnMenuItemClickListener to handle menu item clicks
        mActionBartoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Go to BLE setup activity when bluetooth button pressed
                startBleScan();
                return true;
            }
        });

        mViewPager = (ViewPager) findViewById(id.viewpager);

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
            FragmentLearning f2 = FragmentLearning.newInstance("");
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
            MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("Learning Mode").setIndicator("Learning Mode"));

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
            // Learning Mode display
            ImageView gestureImage = (ImageView) findViewById(R.id.imgViewLearningGesture);
            TextView learningGesture = (TextView) findViewById(id.txtViewLearningMode);
            int r = rnd.nextInt(5);
//            scaleImage(imgResize, mScaleFactor);
            gestureImage.setImageDrawable(getResources().getDrawable(mImageArray[r]));
//            Drawable imgResize = gestureImage.getDrawable();
//            resize(imgResize);
//            gestureImage.setImageDrawable(imgResize);

//            Bitmap bMap = BitmapFactory.decodeResource(getResources(), gestureImage);
//            Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 200, 200, true);
//            gestureImage.setImageBitmap(bMapScaled);

            // Translator display
            TextView primaryGesture = (TextView) findViewById(id.txtViewTranslator);
            TextView gestureList = (TextView) findViewById(id.txtViewGestureList);
            Character letter = data.charAt(0);
            String gesture = letter.toString();
            if(Character.isLetter(letter)) {
                primaryGesture.setText(gesture);
                learningGesture.setText(gesture);
                gestureArray = gestureArray + gesture;
                Character testChar = gestureArray.charAt(gestureArray.length() - 2);
                if(testChar != letter)    {
                    gestureList.setText(gestureList.getText() + " " + letter.toString());
                }
            }

            boolean letterTest = indexOfArray(r, gesture);
            if(letterTest)  {
                Toast.makeText(MainActivity.this, "Good job!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean indexOfArray(int r,String gesture) {
        int resID = getResources().getIdentifier(String.valueOf(mImageArray[r]), "drawable", getPackageName());
        for (int i=0; i<mLengthOfArray; i++)  {
            if(resID == mImageArray[i]){
                return true;
            }
        }
        return false;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public Drawable scaleImage(Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }
        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);
        image = new BitmapDrawable(getResources(), bitmapResized);
        return image;
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 300, 300, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}

