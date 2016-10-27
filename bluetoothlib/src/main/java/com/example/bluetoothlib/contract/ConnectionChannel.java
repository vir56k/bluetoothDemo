package com.example.bluetoothlib.contract;

import android.content.Context;


import com.example.bluetoothlib.util.LogHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by zhangyunfei on 16/9/18.
 */
public abstract class ConnectionChannel {
    private static final String TAG = "ConnectionChannel";
    private static final boolean DEBUG = true;
    private int mState = BLueConnectionState.CONNECTION_STATE_DISCONNECTED;
    private ConnectionCallback connectionCallback;
    private WeakReference<Context> contextWeakReference;

    public ConnectionChannel(Context context, ConnectionCallback connectionCallback) {
        contextWeakReference = new WeakReference<Context>(context);
        this.connectionCallback = connectionCallback;
    }

    protected ConnectionCallback getConnectionCallback() {
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
            LogHelper.e(TAG, "setState() " + BLueConnectionState.toString(oldState) + " -> " + BLueConnectionState.toString(state));
        mState = state;
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
     * @param deviceAddress The BluetoothDevice to connect
     */
    public abstract void connect(String deviceAddress, boolean autoConnect) throws Exception;

    /**
     * Stop all threads
     */
    public abstract void close();

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     */
    public abstract void write(byte[] out) throws IOException;

    /**
     * 可被发现
     *
     * @param activity
     */
    public abstract void ensureDiscoverable(Context activity);
}
