package com.example.bluetoothlib.contract;

/**
 * Created by zhangyunfei on 16/9/17.
 */
public interface ConnectionCallback {

    void onConnected(String deviceName);

    void onConnectionFailed(String error);

    void onConnectionLost();

    void onReadMessage(byte[] tmp);

    void onWriteMessage(byte[] buffer);

    void onConnectStart(String mBluetoothDeviceAddress);
}
