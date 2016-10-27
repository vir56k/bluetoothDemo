package com.example.bluetoothlib;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.bluetoothlib.ble.BleConnectionChannel;
import com.example.bluetoothlib.ble.BlueToothDiscoveryBLE;
import com.example.bluetoothlib.contract.BlueToothDiscovery;
import com.example.bluetoothlib.contract.ConnectionCallback;
import com.example.bluetoothlib.contract.ConnectionChannel;
import com.example.bluetoothlib.contract.DeviceDiscoveryCallback;
import com.example.bluetoothlib.simple.BlueToothDiscoverySimple;
import com.example.bluetoothlib.simple.BluetoothConnectionSimple;
import com.example.bluetoothlib.util.BluetoothAdapterUtil;

/**
 * Created by zhangyunfei on 16/9/18.
 */
public class BluetoothConnectionCreator {

    private static final String TAG = "bluetoothCreator";

    public static ConnectionChannel createConnectionSimple(Context context, ConnectionCallback bluetoothConnectionCallback) {
        return new BluetoothConnectionSimple(context, bluetoothConnectionCallback);
    }

    public static ConnectionChannel createConnectionBLE(Context context, ConnectionCallback bluetoothConnectionCallback) {
        return new BleConnectionChannel(context,
                BluetoothAdapterUtil.getBluetoothAdapter(context),
                bluetoothConnectionCallback);

    }

    public static ConnectionChannel createConnectionAuto(Context context, ConnectionCallback bluetoothConnectionCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            return createConnectionBLE(context, bluetoothConnectionCallback);
        else
            return createConnectionSimple(context, bluetoothConnectionCallback);
    }

    public static ConnectionChannel createConnectionByType(int mode, Context context, ConnectionCallback bluetoothConnectionCallback) {
        if (mode == BlueToothMode.MODE_AUTO) {
            Log.e(TAG, "## 创建蓝牙连接，自动模式");
            return createConnectionAuto(context, bluetoothConnectionCallback);
        } else if (mode == BlueToothMode.MODE_SIMPLE) {
            Log.e(TAG, "## 创建蓝牙连接，标准模式");
            return createConnectionSimple(context, bluetoothConnectionCallback);
        } else if (mode == BlueToothMode.MODE_BLE) {
            Log.e(TAG, "## 创建蓝牙连接，BLE模式");
            return createConnectionBLE(context, bluetoothConnectionCallback);
        }
        return null;
    }

    public static BlueToothDiscovery createDiscovery(int mode, Context context, DeviceDiscoveryCallback callback) {
        if (mode == BlueToothMode.MODE_AUTO) {
            Log.e(TAG, "## 创建蓝牙连接，自动模式");
            return createDiscoveryAuto(context, callback);
        } else if (mode == BlueToothMode.MODE_SIMPLE) {
            Log.e(TAG, "## 创建蓝牙连接，标准模式");
            return new BlueToothDiscoverySimple(context, callback);
        } else if (mode == BlueToothMode.MODE_BLE) {
            Log.e(TAG, "## 创建蓝牙连接，BLE模式");
            return new BlueToothDiscoveryBLE(context, callback);
        }
        return null;
    }

    private static BlueToothDiscovery createDiscoveryAuto(Context context, DeviceDiscoveryCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            return new BlueToothDiscoveryBLE(context, callback);
        else
            return new BlueToothDiscoverySimple(context, callback);
    }

}
