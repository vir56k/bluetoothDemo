package com.example.bluetoothlib.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import com.example.bluetoothlib.contract.BlueToothDiscovery;
import com.example.bluetoothlib.contract.DeviceDiscoveryCallback;

import java.util.Set;


/**
 * API 大于 18
 * 搜索蓝牙,BLE 蓝牙连接方式
 * Created by zhangyunfei on 16/9/17.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BlueToothDiscoveryBLE extends BlueToothDiscovery {

    private static final long SCAN_PERIOD = 10000;
    private final Handler mHandler;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean isDiscovering;

    public BlueToothDiscoveryBLE(Context context, DeviceDiscoveryCallback callback) {
        super(context, callback);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new RuntimeException("BLE is not supported");
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            throw new RuntimeException("Bluetooth not supported.");
        }
        //开启蓝牙
        mBluetoothAdapter.enable();
    }

    public void release() {
        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            cancelDiscovery();
        }

        mBluetoothAdapter = null;
        super.release();

    }


    public Set<BluetoothDevice> getBondedDevices() {
        return mBluetoothAdapter.getBondedDevices();
    }

    public void cancelDiscovery() {
        if(isDiscovering) {
            isDiscovering = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public boolean isDiscovering() {
        return isDiscovering;
    }

    public void startDiscovery() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        isDiscovering = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelDiscovery();
                if (getCallback() != null)
                    getCallback().onDiscoveryComplete();
            }
        }, SCAN_PERIOD);

    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (getCallback() != null)
                getCallback().onDeviceFound(device);
        }
    };
}
