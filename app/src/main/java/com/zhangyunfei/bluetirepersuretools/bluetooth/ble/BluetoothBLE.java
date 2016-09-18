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
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.zhangyunfei.bluetirepersuretools.activity.BluetoothDemoActivity2;
import com.zhangyunfei.bluetirepersuretools.bluetooth.simple.BluetoothConnectionCallback;
import com.zhangyunfei.bluetirepersuretools.bluetooth.simple.BluetoothConnectionCallbackImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by yun on 15/12/18.
 */
@TargetApi(18)
public class BluetoothBLE {
    public static final String Tag = "BluetoothBLE";
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCallback mGattCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGattCharacteristic characteristic2;
    private BluetoothGattCharacteristic characteristic1;
    private static UUID uuidServer = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static UUID uuidServec = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    private static UUID uuidChar1 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static UUID uuidChar3 = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static UUID uuidChar2 = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private static UUID uuidChar4 = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private String resData;//指令请求返回的结果
    private StringBuffer sbData = new StringBuffer();
    //    private final int scanStopDelay = 20000;//10秒后自动关闭扫描
    private ArrayList<String> foundDeviceAddresses = new ArrayList<String>();
    private boolean isConnected;
    private String address;
    private boolean connectBle = true;
    private int state;

    public BluetoothBLE(Context context, BluetoothConnectionCallback bluetoothConnectionCallback) {
        mContext = context;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new RuntimeException("ble_not_supported");
        }


        init(context, null, null);
    }

    public int getState() {
        return state;
    }

    public void start() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new RuntimeException("ble_not_supported");
        }
    }

    public void stop() {

    }

    public void ensureDiscoverable(Activity activity) {

    }


    /**
     * 单例持有者
     */
    private static class SingletonHolder {
        public final static BluetoothBLE instance = new BluetoothBLE();
    }

    public int getConnectionState() {
        return -1;//TODO 返回蓝牙连接状态  待设置
    }

    public String getATIresponse(byte[] bytes, byte b) {//TODO 待完善
        return sendAndReceive(new String(bytes), b);
    }

    public boolean adapterIsEnabled() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    public boolean enableAdapter(boolean enable) {
        if (mBluetoothAdapter == null) {
            Log.w(Tag, "[enableAdapter] No *BLUETOOTH ADAPTER* found!");
            return false;
        }
//        if (!mHasBluetoothAdminPermission) {
//            if (Config.DEBUG) {
//                Logger.w(
//                        TAG,
//                        "[enableAdapter] No permission(android.permission.BLUETOOTH_ADMIN) to administrate the BLUETOOTH automatically！");
//            }
//            return false;
//        }
        if (enable) {
            if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                Log.d(Tag,
                        "[enableAdapter] Enable the Bluetooth adapter!");
                return mBluetoothAdapter.enable();
            } else {
                Log.d(Tag, "[enableAdapter] Status of adapter: "
                        + mBluetoothAdapter.getState());
                return false;
            }
        } else {
            Log.d(Tag, "[enableAdapter] Disable the Bluetooth adapter!");
            return mBluetoothAdapter.disable();
        }
    }

    public static BluetoothBLE getInstance() {
        return SingletonHolder.instance;
    }

    public BluetoothBLE() {

    }

    public boolean startLeScan() {
        Log.e(Tag, "startLeScan:");
        foundDeviceAddresses.clear();
        boolean flag = mBluetoothAdapter.startLeScan(null, mLeScanCallback);
//        mBleListener.onEvent(Event.scanStatred, null, null);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                getInstance().stopLeScan();
//            }
//        }, scanStopDelay);
        return flag;
    }

    public void connect(String address) {
        Log.e(Tag, "connect:" + address);
        this.address = address;
        stopLeScan();
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothDevice.getType();
//        mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, mGattCallback);
        mBluetoothGatt = connectGatt(mBluetoothDevice, mContext, false, mGattCallback);
        mBluetoothGatt.connect();
    }

    /**
     * 为了兼容
     * 如果api >= 22，调用带有 tranport参数的。
     * 在我的360手机上，总是连接 connectGatt失败，经测试，调用带有  BluetoothDevice.TRANSPORT_LE才能调用成功
     *
     * @param device BluetoothDevice
     */
    public static BluetoothGatt connectGatt(BluetoothDevice device, Context context, boolean autoConntect, BluetoothGattCallback bluetoothGattCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Little hack with reflect to use the connect gatt with defined transport in Lollipop
            Method connectGattMethod = null;
            try {
                connectGattMethod = device.getClass().getMethod("connectGatt", Context.class, boolean.class, BluetoothGattCallback.class, int.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                if (connectGattMethod != null) {
                    return (BluetoothGatt) connectGattMethod.invoke(device, context, autoConntect, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return device.connectGatt(context, autoConntect, bluetoothGattCallback);
    }


    public void getUUID() {
        List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
        if (gattServices == null) return;

        Log.e(Tag, "--> gattServices.size() == " + gattServices.size());
        for (BluetoothGattService gattService : gattServices) {
            //-----Service的字段信息-----//
            Log.e(Tag, "-->service uuid:" + gattService.getUuid());//TODO

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            Log.e(Tag, "--> gattCharacteristics.size() == " + gattCharacteristics.size());
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(Tag, "---->char uuid:" + gattCharacteristic.getUuid());//TODO

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(Tag, "---->char value:" + new String(data));
                }

                //UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
//                if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
                //测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mBLE.readCharacteristic(gattCharacteristic);
//                        }
//                    }, 500);

                //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
//                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
                //设置数据内容
//                    gattCharacteristic.setValue("send data->");
                //往蓝牙模块写入数据
//                    mBLE.writeCharacteristic(gattCharacteristic);
//                }

                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                Log.e(Tag, "--> gattDescriptors.size() == " + gattDescriptors.size());
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(Tag, "-------->desc uuid:" + gattDescriptor.getUuid());//TODO
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(Tag, "-------->desc permission == :" + descPermission);

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(Tag, "-------->desc value:" + new String(desData));
                    }
                }
            }
        }//

    }

    public boolean write(String cmd) {
       /* synchronized (getInstance()) {
            characteristic2.setValue(cmd);
            characteristic2.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            boolean flag = mBluetoothGatt.writeCharacteristic(characteristic2);
            try {
                getInstance().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return flag;
        }*/
        return write(cmd.getBytes());
    }

    public boolean write(byte[] cmd) {
        Log.e(Tag, "write:" + new String(cmd));
        synchronized (getInstance()) {
            resData = null;
            sbData.delete(0, sbData.length());
            characteristic2.setValue(cmd);
            characteristic2.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            boolean flag = mBluetoothGatt.writeCharacteristic(characteristic2);
            try {
                getInstance().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return flag;
        }
    }

    public void dicoverServer() {
        boolean b = mBluetoothGatt.discoverServices();
    }

    public void getServer() {
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        Log.e(Tag, services.toString());
        BluetoothGattService service = mBluetoothGatt.getService(uuidServer);
        characteristic1 = service.getCharacteristic(uuidChar1);
        characteristic2 = service.getCharacteristic(uuidChar2);

        setDescriptor(characteristic1, "00002902-0000-1000-8000-00805f9b34fb");
        getUUID();
    }

    private void setDescriptor(BluetoothGattCharacteristic characteristic, String uuid) {
        mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(uuid));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

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

    /**
     * @param context
     * @return 系统版本，>=18，支持ble
     */
    public static int getSystemVersionInt(Context context) {
        return Build.VERSION.SDK_INT;
    }

    public void init(Context context, Object bleListener, Object userData) {
        Log.e(Tag, "init");
        this.mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.e(Tag, "onConnectionStateChange:" + newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    ConnectManager.setUseBle(true);
                    if (!isConnected) {
                        dicoverServer();
                        isConnected = true;
                    }
                    connectBle = true;
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//TODO 手机支持4.0但是设备不支持
                    //如果之前是用ble 则使用3.0试  失败后那就失败了
                    //如果之前是3.0  那就直接失败
                    Log.e(Tag, "newState == BluetoothProfile.STATE_DISCONNECTED");
//                    if (ConnectManager.getInstance() instanceof BluetoothBLE && connectBle) {
////                        ConnectManager.getInstance() instanceof BluetoothBLE ? ((BluetoothBLE) ConnectManager.getInstance()) : null;
//                        ConnectManager.setUseBle(false);
////                    ConnectManager.getInstance().startLeScan();
//                        ConnectManager.getInstance().connect(address);
//                        connectBle = false;
//                        return;
//                    }
                    Log.e(Tag, "onConnectionStateChange ConnectManager.getInstance().startLeScan()");
                    isConnected = false;
//                    mBleListener.onEvent(Event.disconnected, gatt.getDevice().getAddress(), null);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                Log.e(Tag, "onServicesDiscovered");
                getServer();
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                Log.e(Tag, "onCharacteristicRead");
                read(characteristic);
            }

            /**
             * @param gatt
             * @param characteristic
             * @param status
             */
            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.e(Tag, "onCharacteristicWrite status == " + status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.e(Tag, "onCharacteristicChanged");
                read(characteristic);
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                Log.e(Tag, "onDescriptorRead");
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.e(Tag, "onDescriptorWrite");
//                mBleListener.onEvent(Event.connectSucc, gatt.getDevice().getAddress(), null);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
                Log.e(Tag, "onReliableWriteCompleted");
            }

            /**
             * @param gatt
             * @param rssi
             * @param status
             */
            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                Log.e(Tag, "onReadRemoteRssi");
            }
        };
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                final String address = bluetoothDevice.getAddress();
                if (!foundDeviceAddresses.contains(address)) {
                    Log.e(Tag, "onLeScan:" + address);
                    foundDeviceAddresses.add(address);
//                    mBleListener.onEvent(Event.found, bluetoothDevice, null);
                }
            }
        };

//        mBleListener = bleListener;
    }

    public void disconnect() {
        Log.e(Tag, "disconnect");
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            isConnected = false;
        }
    }

    public void stopLeScan() {
        Log.e(Tag, "stopLeScan");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        mBleListener.onEvent(Event.scanFinished, null, null);
    }

    private void read(BluetoothGattCharacteristic characteristic) {
        synchronized (getInstance()) {
            String data = new String(characteristic.getValue());
            Log.e(Tag, "readValue:" + data.replace("\r", "").replace(">", ""));
            sbData.append(data);
            if (data.contains(">")) {
                resData = sbData.toString();
                resData = resData.replace(">", "");
                getInstance().notify();
                Log.e(Tag, "read :" + resData);
            }
        }
    }

    public String sendAndReceive(String cmd, byte limit) {
        Log.d(Tag, "sendAndReceive cmd:" + cmd);
        write(cmd);
        Log.d(Tag, "sendAndReceive data:" + resData);
        return resData;
    }

    public boolean sendData(String cmd) {
        Log.e(Tag, "sendData:" + cmd);
        return write(cmd);
    }

    public boolean sendData(byte[] cmd) {
        Log.e(Tag, "sendData:" + cmd);
        return write(cmd);
    }

    public String recevieData(byte limit) {
        Log.e(Tag, "recevieData:" + resData);
        return resData;
    }

//    public void setBleListener(Listener bleListener) {
//        this.mBleListener = bleListener;
//    }

    public void cleanup() {
        Log.e(Tag, "cleanup");

        // Unregister broadcast listeners
        /*if(null != mContext)
            mContext.unregisterReceiver(mBroadcastReceiver);*/

        // Close the connection.
        if (mBluetoothDevice != null) {
            disconnect();
        }

        // Attempt to recover the state of BLUETOOTH ADAPTER
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }

        // reset the members
        mContext = null;
        mBluetoothAdapter = null;
//        mBleListener = null;
        mGattCallback = null;
        mLeScanCallback = null;
        mBluetoothGatt = null;
    }

}



/**
 *
 * 参考 ： https://developer.android.com/sdk/api_diff/23/changes/android.bluetooth.BluetoothDevice.html
 * http://www.wowotech.net/bluetooth/bt_overview.html
 * http://www.it1352.com/336738.html
 *
 * */