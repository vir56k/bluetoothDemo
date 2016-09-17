package com.zhangyunfei.bluetirepersuretools.bluetooth.contract;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zhangyunfei.bluetirepersuretools.R;

import java.util.Set;

/**
 * 搜索蓝牙
 * Created by zhangyunfei on 16/9/17.
 */
public class BlueToothDiscovery {
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private DeviceDiscoveryCallback callback;

    public BlueToothDiscovery(Context context, DeviceDiscoveryCallback callback) {
        if (context == null)
            throw new NullPointerException();
        if (callback == null)
            throw new NullPointerException();
        this.callback = callback;

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);
    }

    public void release(Context context) {
        if (context == null) return;
        context.unregisterReceiver(mReceiver);

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            cancelDiscovery();
        }

        mBtAdapter = null;
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (callback != null)
                        callback.onDeviceFound(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (callback != null)
                    callback.onDiscoveryComplete();
            }
        }
    };

    public Set<BluetoothDevice> getBondedDevices() {
        return mBtAdapter.getBondedDevices();
    }

    public void cancelDiscovery() {
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
    }

    public boolean isDiscovering() {
        return mBtAdapter.isDiscovering();
    }

    public void startDiscovery() {
        mBtAdapter.startDiscovery();
    }
}
