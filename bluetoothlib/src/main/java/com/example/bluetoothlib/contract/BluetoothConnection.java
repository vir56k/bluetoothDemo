package com.example.bluetoothlib.contract;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.example.bluetoothlib.simple.BluetoothConnectionSimple;

import java.lang.ref.WeakReference;

/**
 * Created by zhangyunfei on 16/9/18.
 */
public abstract class BluetoothConnection {
    private static final String TAG = "BluetoothConnection";
    private static final boolean DEBUG = true;
    private int mState = ConnectionState.STATE_NONE;
    private BluetoothConnectionCallback connectionCallback;
    private WeakReference<Context> contextWeakReference;

    public BluetoothConnection(Context context, BluetoothConnectionCallback connectionCallback) {
        contextWeakReference = new WeakReference<Context>(context);
        this.connectionCallback = connectionCallback;
    }

    protected BluetoothConnectionCallback getConnectionCallback() {
        return connectionCallback;
    }

    public Context getContext() {
        return contextWeakReference == null ? null : contextWeakReference.get();
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    protected synchronized void setState(int state) {
        if (this.mState == state) return;
        int oldState = mState;
        if (DEBUG)
            Log.e(TAG, "setState() " + oldState + " -> " + state);
        mState = state;
        if (connectionCallback != null)
            connectionCallback.onMessageStateChange(oldState, mState);
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public abstract void start();

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public abstract void connect(BluetoothDevice device) throws Exception;

    /**
     * Stop all threads
     */
    public abstract void stop();

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see BluetoothConnectionSimple.ConnectedThread#write(byte[])
     */
    public abstract void write(byte[] out);

    /**
     * 可被发现
     *
     * @param activity
     */
    public abstract void ensureDiscoverable(Context activity);
}
