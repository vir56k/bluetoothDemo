package com.zhangyunfei.bluetirepersuretools.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhangyunfei.bluetirepersuretools.activity.BluetoothDemoActivity;

/**
 * Created by zhangyunfei on 16/9/17.
 */
public class BluetoothConnectionCallbackImpl implements BluetoothConnectionCallback {

    private static final String TAG = "BLUE";
    private android.os.Handler mHandler;

    public BluetoothConnectionCallbackImpl(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onMessageStateChange(int oldState, int newState) {
        Log.e(TAG, "## 消息状态发生改变: " + +oldState + " -> " + newState);
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BluetoothDemoActivity.MESSAGE_STATE_CHANGE, newState, -1).sendToTarget();
    }

    @Override
    public void onConnected(String deviceName) {
        Log.e(TAG, "## 连接成功: " + deviceName);
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BluetoothDemoActivity.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDemoActivity.DEVICE_NAME, deviceName);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onConnectionFailed() {
        Log.e(TAG, "## 连接失败 ");
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothDemoActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDemoActivity.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onConnectionLost() {
        Log.e(TAG, "## 连接丢失 ");
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothDemoActivity.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothDemoActivity.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onReadMessage(byte[] tmp) {
        Log.e(TAG, "## 读取到消息 len = " + tmp.length);
        // Send the obtained bytes to the UI Activity
        mHandler.obtainMessage(BluetoothDemoActivity.MESSAGE_READ, tmp)
                .sendToTarget();
    }

    @Override
    public void onWriteMessage(byte[] buffer) {
        Log.e(TAG, "## 发送消息 len = " + buffer.length);
        // Share the sent message back to the UI Activity
        mHandler.obtainMessage(BluetoothDemoActivity.MESSAGE_WRITE, -1, -1, buffer)
                .sendToTarget();
    }
}
