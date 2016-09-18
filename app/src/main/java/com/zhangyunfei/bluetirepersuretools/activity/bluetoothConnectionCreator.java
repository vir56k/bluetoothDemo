package com.zhangyunfei.bluetirepersuretools.activity;

import android.content.Context;

import com.zhangyunfei.bluetirepersuretools.bluetooth.contract.BluetoothConnection;
import com.zhangyunfei.bluetirepersuretools.bluetooth.simple.BluetoothConnectionCallback;
import com.zhangyunfei.bluetirepersuretools.bluetooth.simple.BluetoothService2;

/**
 * Created by zhangyunfei on 16/9/18.
 */
public class bluetoothConnectionCreator {

    public static BluetoothConnection createConnection(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
        return new BluetoothService2(context, bluetoothConnectionCallback);

    }
}
