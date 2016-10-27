package com.example.bluetoothlib.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

/**
 * 获得一个BluetoothAdapter
 * Created by zhangyunfei on 16/10/26.
 */
public class BluetoothAdapterUtil {
    public static final String TAG = "BluetoothAdapterUtil";

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        if (context == null)
            throw new NullPointerException();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            return bluetoothManager.getAdapter();
        }
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 转换蓝牙状态到字符串
     *
     * @param state
     * @return
     */
    public static String stateToStirng(int state) {
        String tmp;
        if (state == BluetoothAdapter.STATE_OFF) {
            tmp = "关闭";
        } else if (state == BluetoothAdapter.STATE_TURNING_ON) {
            tmp = "正在打开";
        } else if (state == BluetoothAdapter.STATE_ON) {
            tmp = "打开";
        } else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
            tmp = "正在关闭";
        } else {
            tmp = "未知";
        }
        return tmp;
    }



}
