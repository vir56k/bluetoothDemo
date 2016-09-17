package com.zhangyunfei.bluetirepersuretools.bluetooth.contract;

import android.bluetooth.BluetoothDevice;

/**
 * 搜索设备的回调
 * Created by zhangyunfei on 16/9/17.
 */
public interface DeviceDiscoveryCallback {

    /**
     * 当搜索到设备时
     *
     * @param device
     */
    public void onDeviceFound(BluetoothDevice device);


    public void onDiscoveryComplete();
}
