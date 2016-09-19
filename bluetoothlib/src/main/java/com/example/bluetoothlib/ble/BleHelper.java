package com.example.bluetoothlib.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.bluetoothlib.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by zhangyunfei on 16/9/18.
 */
public class BleHelper {

    private static final String TAG = "BleHelper";

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


    public static void listAllServices(BluetoothGatt gatt) {
        List<BluetoothGattService> gattServices = gatt.getServices();
        if (gattServices == null) return;
        for (BluetoothGattService gattService : gattServices) {
            //-----Service的字段信息-----//
            Log.e(TAG,"\t \t BluetoothGattService");
            int type = gattService.getType();
            Log.e(TAG,"\t -->service type:"+ Utils.getServiceType(type));
            Log.e(TAG,"\t -->includedServices size:"+gattService.getIncludedServices().size());
            Log.e(TAG,"\t -->service uuid:"+gattService.getUuid());

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics =gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic  gattCharacteristic: gattCharacteristics) {
                Log.e(TAG,"\t \t BluetoothGattCharacteristic");
                Log.e(TAG,"\t \t ---->char uuid:"+gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG,"\t \t ---->char permission:"+Utils.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG,"\t \t ---->char property:"+Utils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(TAG,"\t \t ---->char value:"+new String(data));
                }

//                //UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
//                if(gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)){
//                    //测试读取当前Characteristic数据，会触发mOnDataAvailable.onCharacteristicRead()
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mBLE.readCharacteristic(gattCharacteristic);
//                        }
//                    }, 500);
//
//                    //接受Characteristic被写的通知,收到蓝牙模块的数据后会触发mOnDataAvailable.onCharacteristicWrite()
//                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
//                    //设置数据内容
//                    gattCharacteristic.setValue("send data->");
//                    //往蓝牙模块写入数据
//                    mBLE.writeCharacteristic(gattCharacteristic);
//                }

                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(TAG,"\t \t BluetoothGattDescriptor");
                    Log.e(TAG, "\t \t \t -------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG,"\t \t \t -------->desc permission:"+ Utils.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(TAG, "\t \t \t -------->desc value:"+ new String(desData));
                    }
                }
            }
        }//

    }

}
