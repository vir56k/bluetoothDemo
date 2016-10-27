package com.example.bluetoothlib.contract;

/**
 * 蓝牙连接状态
 * Created by zhangyunfei on 16/9/30.
 */
public class BLueConnectionState {

    public static final int CONNECTION_STATE_DISCONNECTED = 0;
    public static final int CONNECTION_STATE_WAIT_START = 1;
    public static final int CONNECTION_STATE_CONNECTING = 2;
    public static final int CONNECTION_STATE_CONNECTED = 3;


    public static String toString(int state) {
        String str = "未知";
        if (state == CONNECTION_STATE_DISCONNECTED)
            str = "已断开";
        else if (state == CONNECTION_STATE_WAIT_START)
            str = "等待开始";
        else if (state == CONNECTION_STATE_CONNECTING)
            str = "连接中";
        else if (state == CONNECTION_STATE_CONNECTED)
            str = "已连接";
        return str;
    }
}
