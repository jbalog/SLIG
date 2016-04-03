/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group24.SLIG.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import group24.SLIG.bluetooth.ServiceConnectionService;
import group24.SLIG.R;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends AppCompatActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mAddressColor;
    private TextView mConnectionState;
    private TextView mDataField;
    private ExpandableListView mGattServicesList;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public Intent deviceIntent;
    public Intent bluetoothIntent;


    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(ServiceConnectionService.getmBluetoothLeService().getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                /** This is where the "gesture data" is sent to displayData method below **/
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.
    // Demonstrates 'Read' and 'Notify' features.
//    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
//                                        int childPosition, long id) {
//                if (mGattCharacteristics != null) {
//                    final BluetoothGattCharacteristic characteristic =
//                            mGattCharacteristics.get(groupPosition).get(childPosition);
//                    final int charaProp = characteristic.getProperties();
//                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                        // If there is an active notification on a characteristic, clear
//                        // it first so it doesn't update the data field on the user interface.
//                        if (mNotifyCharacteristic != null) {
//                            ServiceConnectionService.getmBluetoothLeService().setCharacteristicNotification(
//                                    mNotifyCharacteristic, false);
//                            mNotifyCharacteristic = null;
//                        }
//                        ServiceConnectionService.getmBluetoothLeService().readCharacteristic(characteristic);
//                    }
//                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                        mNotifyCharacteristic = characteristic;
//                        ServiceConnectionService.getmBluetoothLeService().setCharacteristicNotification(
//                                characteristic, true);
//                    }
//                    return true;
//                }
//                return false;
//            }
//    };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Bind to BT Service
        deviceIntent = getIntent();
        ServiceConnectionService.setmDeviceName(deviceIntent.getStringExtra(EXTRAS_DEVICE_NAME));
        ServiceConnectionService.setmDeviceAddress(deviceIntent.getStringExtra(EXTRAS_DEVICE_ADDRESS));
        bluetoothIntent = new Intent(this, BluetoothLeService.class);
        startService(bluetoothIntent);

        // Setup main menu toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ServiceConnectionService.getmDeviceName());

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(ServiceConnectionService.getmDeviceAddress());
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
//        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mAddressColor = (TextView) findViewById(R.id.device_address);
        mAddressColor.setTextColor(getResources().getColor(android.R.color.white));
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mConnectionState.setTextColor(getResources().getColor(android.R.color.white));
        mDataField = (TextView) findViewById(R.id.data_value);
        mDataField.setTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(bluetoothIntent, ServiceConnectionService.getServiceConnection(), Context.BIND_AUTO_CREATE);

        // This registers receiver when created and when re-entering activity
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

        if (ServiceConnectionService.getmBluetoothLeService() != null) {
            final boolean result = ServiceConnectionService.getmBluetoothLeService().connect(ServiceConnectionService.getmDeviceAddress());
            Log.d(TAG, "Connect request result=" + result);
        }
        if(ServiceConnectionService.getmBluetoothLeService() != null) {
            displayGattServices(ServiceConnectionService.getmBluetoothLeService().getSupportedGattServices());
        }
    }

    // Unbind the service when paused so that there is only one active connection
    // to the service at any given time
    @Override
    protected void onPause() {
        super.onPause();
        // Pairing causes activity to pause!!
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(ServiceConnectionService.getServiceConnection());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(bluetoothIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    // Handles menu items on Device Control screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                ServiceConnectionService.getmBluetoothLeService().connect(ServiceConnectionService.getmDeviceAddress());
                return true;
            case R.id.menu_disconnect:
                ServiceConnectionService.getmBluetoothLeService().disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    // TODO This is where the "gesture data" is being displayed... need to pass to MainActivity
    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
//            currentServiceData.put(
//                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
//                currentCharaData.put(
                uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equals(SampleGattAttributes.SLIG_GESTURE_CHARACTERISTIC)) {
                    ServiceConnectionService.setmBleCharacteristic(gattCharacteristic);
                }
//                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 },
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[] {LIST_NAME, LIST_UUID},
//                new int[] { android.R.id.text1, android.R.id.text2 }
//        );
//        mGattServicesList.setAdapter(gattServiceAdapter);
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
