package com.example.bluetoothlib.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 安全的 handler
 * Created by zhangyunfei on 16/7/21.
 */
public class SafeHandler<T> extends Handler {
    private WeakReference<T> innerObject;

    public SafeHandler(T object) {
        this.innerObject = new WeakReference<T>(object);
    }

    public SafeHandler(Looper looper, T object) {
        super(looper);
        this.innerObject = new WeakReference<T>(object);
    }

    public T getInnerObject() {
        return innerObject == null ? null : innerObject.get();
    }

    @Override
    public void handleMessage(Message msg) {
        if (getInnerObject() == null)
            return;

        super.handleMessage(msg);
    }
}
