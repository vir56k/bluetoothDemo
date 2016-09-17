package com.zhangyunfei.bluetirepersuretools.bluetooth.contract;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * 搜索蓝牙
 * Created by zhangyunfei on 16/9/17.
 */
public abstract class BlueToothDiscovery {
    private DeviceDiscoveryCallback callback;
    private WeakReference<Context> contextWeakReference;

    public BlueToothDiscovery(Context context, DeviceDiscoveryCallback callback) {
        if (context == null)
            throw new NullPointerException();
        if (callback == null)
            throw new NullPointerException();
        contextWeakReference = new WeakReference<Context>(context);
        this.callback = callback;
    }

    public Context getContext() {
        return contextWeakReference == null ? null : contextWeakReference.get();
    }

    protected DeviceDiscoveryCallback getCallback() {
        return callback;
    }

    public void release() {
        callback = null;
        contextWeakReference.clear();
    }

    public abstract Set<BluetoothDevice> getBondedDevices();

    public abstract void cancelDiscovery();

    public abstract boolean isDiscovering();

    public abstract void startDiscovery();
}
