package group24.SLIG.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Jason on 4/2/2016.
 */
public class ServiceConnectionService {

    public static String getmDeviceAddress() {
        return mDeviceAddress;
    }

    public static void setmDeviceAddress(String mDeviceAddress) {
        ServiceConnectionService.mDeviceAddress = mDeviceAddress;
    }

    private static String mDeviceAddress;

    public static String getmDeviceName() {
        return mDeviceName;
    }


    public static void setmDeviceName(String mDeviceName) {
        ServiceConnectionService.mDeviceName = mDeviceName;
    }

    private static String mDeviceName;

    public static BluetoothLeService getmBluetoothLeService() {
        return mBluetoothLeService;
    }

    private static BluetoothLeService mBluetoothLeService;

    public static BluetoothGattCharacteristic getmBleCharacteristic() {
        return mBleCharacteristic;
    }

    public static void setmBleCharacteristic(BluetoothGattCharacteristic mBleCharacteristic) {
        ServiceConnectionService.mBleCharacteristic = mBleCharacteristic;
    }

    public static ServiceConnection getServiceConnection() {
        return mServiceConnection;
    }

    private static BluetoothGattCharacteristic mBleCharacteristic;

    private final static String TAG = ServiceConnectionService.class.getSimpleName();

    // Code to manage Service lifecycle.
    /** Defines callbacks for service binding, passed to bindService() */
    private static ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
//                finish();
            }
//          Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
}
