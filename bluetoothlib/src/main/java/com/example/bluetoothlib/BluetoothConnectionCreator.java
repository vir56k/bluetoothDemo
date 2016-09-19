package com.example.bluetoothlib;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.bluetoothlib.ble.BlueToothConnectionBLE;
import com.example.bluetoothlib.ble.BlueToothDiscoveryBLE;
import com.example.bluetoothlib.contract.BlueToothDiscovery;
import com.example.bluetoothlib.contract.BluetoothConnection;
import com.example.bluetoothlib.contract.BluetoothConnectionCallback;
import com.example.bluetoothlib.contract.DeviceDiscoveryCallback;
import com.example.bluetoothlib.simple.BlueToothDiscoverySimple;
import com.example.bluetoothlib.simple.BluetoothConnectionSimple;

/**
 * Created by zhangyunfei on 16/9/18.
 */
public class BluetoothConnectionCreator {

    private static final String TAG = "bluetoothCreator";

    public static BluetoothConnection createConnectionSimple(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
        return new BluetoothConnectionSimple(context, bluetoothConnectionCallback);
    }

    public static BluetoothConnection createConnectionBLE(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
        return new BlueToothConnectionBLE(context, bluetoothConnectionCallback);

    }

    public static BluetoothConnection createConnectionAuto(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            return new BlueToothConnectionBLE(context, bluetoothConnectionCallback);
        else
            return new BluetoothConnectionSimple(context, bluetoothConnectionCallback);
    }

    public static BluetoothConnection createConnectionByType(int mode, Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
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
