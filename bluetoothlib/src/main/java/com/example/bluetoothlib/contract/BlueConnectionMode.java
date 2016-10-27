package com.example.bluetoothlib.contract;

/**
 * Created by zhangyunfei on 16/10/18.
 */
public class BlueConnectionMode {
    private BlueConnectionMode() {
    }

    public static final int MODE_NONE = 0;
    public static final int MODE_BLE = 1;//BLE模式
    public static final int MODE_CLASSIC = 2;//经典蓝牙模式


    public static String toString(int mode) {
        if (mode == MODE_BLE) {
            return "BLE";
        } else if (mode == MODE_CLASSIC) {
            return "CLASSIC";
        } else if (mode == MODE_NONE) {
            return "NONE";
        } else return "NONE";
    }
}
