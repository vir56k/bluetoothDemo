package com.zhangyunfei.bluetirepersuretools.bluetooth.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.zhangyunfei.bluetirepersuretools.bluetooth.contract.ConnectionState;
import com.zhangyunfei.bluetirepersuretools.bluetooth.simple.BluetoothConnectionCallback;

import java.util.List;
import java.util.UUID;

/**
 * Created by zhangyunfei on 16/9/17.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleService {
    private static final Object LOCK = new Object();
    private static UUID uuidServer = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static UUID uuidServec = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    private static UUID uuidChar1 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static UUID uuidChar3 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static UUID uuidChar2 = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private static UUID uuidChar4 = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");


    private static final String TAG = "BleService";
    private BluetoothConnectionCallback bluetoothConnectionCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private Handler mHandler = new Handler();
    private int state;
    private BluetoothGattCharacteristic characteristic1;
    private BluetoothGattCharacteristic characteristic2;


    public BleService(Context mContext) {
        this.mContext = mContext;
        initialize();
    }

    public BleService(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
        if (context == null)
            throw new NullPointerException();
        if (bluetoothConnectionCallback == null)
            throw new NullPointerException();
        this.mContext = context;
        this.bluetoothConnectionCallback = bluetoothConnectionCallback;

        initialize();
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }


        // 确保蓝牙在设备上可以开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(enableBtIntent);//, REQUEST_ENABLE_BT);
        }
        return true;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param bluetoothDevice The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final BluetoothDevice bluetoothDevice) throws Exception {
        if (bluetoothDevice == null) {
            throw new Exception("Device not found.  Unable to connect.");
        }
        if (mBluetoothAdapter == null || bluetoothDevice == null) {
            throw new Exception("BluetoothAdapter not initialized or unspecified address.");
        }
        setState(ConnectionState.STATE_CONNECTING);
        mBluetoothDevice = bluetoothDevice;
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDevice.getAddress().equals(bluetoothDevice.getAddress())
                && mBluetoothGatt != null) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = BleHelper.connectGatt(bluetoothDevice, mContext, false, mGattCallback);
        Log.e(TAG, "Trying to create a new connection.");
        return true;
    }


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.e(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.e(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e(TAG, "Disconnected from GATT server.");
                setState(ConnectionState.STATE_NONE);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onServicesDiscovered received:  SUCCESS");
                initCharacteristic();
                setState(ConnectionState.STATE_CONNECTED);
                try {
                    Thread.sleep(200);//延迟发送，否则第一次消息会不成功
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bluetoothConnectionCallback != null)
                    bluetoothConnectionCallback.onConnected(mBluetoothDevice.getName());
            } else {
                Log.e(TAG, "onServicesDiscovered error falure " + status);
                setState(ConnectionState.STATE_NONE);
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, "onCharacteristicWrite status: " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.e(TAG, "onDescriptorWrite status: " + status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.e(TAG, "onDescriptorRead status: " + status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.e(TAG, "onCharacteristicRead status: " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "onCharacteristicChanged characteristic: " + characteristic);
            readCharacteristic(characteristic);
        }
    };

    /**
     * @param context
     * @return true 支持ble，false 不支持
     */
    public static boolean isSupportBLE(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return true;
        } else {
            return false;
        }
    }

    public void initCharacteristic() {
        if (mBluetoothGatt == null) throw new NullPointerException();
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        Log.e(TAG, services.toString());
        BluetoothGattService service = mBluetoothGatt.getService(uuidServer);
        characteristic1 = service.getCharacteristic(uuidChar1);
        characteristic2 = service.getCharacteristic(uuidChar2);

        final String uuid = "00002902-0000-1000-8000-00805f9b34fb";
        if (mBluetoothGatt == null) throw new NullPointerException();
        mBluetoothGatt.setCharacteristicNotification(characteristic1, true);
        BluetoothGattDescriptor descriptor = characteristic1.getDescriptor(UUID.fromString(uuid));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
        byte[] bytes = characteristic.getValue();
        String str = new String(bytes);
        Log.e(TAG, "## readCharacteristic, 读取到: " + str);
        if (bluetoothConnectionCallback != null)
            bluetoothConnectionCallback.onReadMessage(bytes);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public int getState() {
        return state;
    }

    private void setState(int state) {
        this.state = state;
    }

    public void start() {

    }

    public void stop() {
        setState(ConnectionState.STATE_NONE);
        characteristic1 = null;
        characteristic2 = null;
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
    }

    public void write(byte[] cmd) {
        Log.e(TAG, "write:" + new String(cmd));
        synchronized (LOCK) {
            characteristic2.setValue(cmd);
            characteristic2.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mBluetoothGatt.writeCharacteristic(characteristic2);

            if (bluetoothConnectionCallback != null)
                bluetoothConnectionCallback.onWriteMessage(cmd);
        }
    }


    public void ensureDiscoverable(Activity activity) {
    }
}
