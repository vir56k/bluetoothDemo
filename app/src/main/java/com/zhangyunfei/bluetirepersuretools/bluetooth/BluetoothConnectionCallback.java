package com.zhangyunfei.bluetirepersuretools.bluetooth;

/**
 * Created by zhangyunfei on 16/9/17.
 */
public interface BluetoothConnectionCallback {
    void onMessageStateChange(int oldState, int newState);

    void onConnected(String deviceName);

    void onConnectionFailed();

    void onConnectionLost();

    void onReadMessage(byte[] tmp);

    void onWriteMessage(byte[] buffer);
}
