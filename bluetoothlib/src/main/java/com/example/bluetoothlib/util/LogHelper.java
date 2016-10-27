package com.example.bluetoothlib.util;

/**
 * Created by zhangyunfei on 16/9/29.
 */
public class LogHelper {
    public static void d(String tag, String msg) {
        android.util.Log.e(tag, msg);
    }

    public static void e(String tag, String s) {
        android.util.Log.e(tag, s);

    }
}
