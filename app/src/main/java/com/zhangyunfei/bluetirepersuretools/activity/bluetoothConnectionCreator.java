package com.zhangyunfei.bluetirepersuretools.activity;

import android.content.Context;
import android.os.Build;

import com.zhangyunfei.bluetirepersuretools.bluetooth.ble.BlueToothConnectionBLE;
import com.zhangyunfei.bluetirepersuretools.bluetooth.contract.BluetoothConnection;
import com.zhangyunfei.bluetirepersuretools.bluetooth.contract.BluetoothConnectionCallback;
import com.zhangyunfei.bluetirepersuretools.bluetooth.simple.BluetoothConnectionSimple;

/**
 * Created by zhangyunfei on 16/9/18.
 */
public class bluetoothConnectionCreator {

    public static BluetoothConnection createConnection(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
//        return new BluetoothConnectionSimple(context, bluetoothConnectionCallback);
        return new BlueToothConnectionBLE(context, bluetoothConnectionCallback);

    }

    public static BluetoothConnection createConnectionAuto(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            return new BlueToothConnectionBLE(context, bluetoothConnectionCallback);
        else
            return new BluetoothConnectionSimple(context, bluetoothConnectionCallback);
    }

    public static BluetoothConnection createConnectionByType(int type, Context context, BluetoothConnectionCallbackImpl bluetoothConnectionCallback) {
        if (type == 1) {
            return new BluetoothConnectionSimple(context, bluetoothConnectionCallback);
        } else if (type == 2) {
            return new BlueToothConnectionBLE(context, bluetoothConnectionCallback);
        } else if (type == 3) {
            return createConnectionAuto(context, bluetoothConnectionCallback);
        }
        return null;
    }
}
