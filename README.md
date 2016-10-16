# bluetoothDemo

普通蓝牙EDR连接，BLE蓝牙连接,示例，操作蓝牙打印机demo

#子module描述
    * bluetoothlib 基础蓝牙类库
    * app 普通蓝牙，BLE蓝牙扫描和连接演示
    * zyfutilbluetoothprinter 蓝牙打印机演示


==========================================

#Android使用BLE(低功耗蓝牙，Bluetooth Low Energy)

#背景
在学习BLE的过程中，积累了一些心得的DEMO，放到Github，形成本文。感兴趣的同学可以下载到源代码。
github: https://github.com/vir56k/bluetoothDemo

#什么是BLE(低功耗蓝牙)

BLE(Bluetooth Low Energy，低功耗蓝牙)是对传统蓝牙BR/EDR技术的补充。
尽管BLE和传统蓝牙都称之为蓝牙标准，且共享射频，但是，BLE是一个完全不一样的技术。
BLE不具备和传统蓝牙BR/EDR的兼容性。它是专为小数据率、离散传输的应用而设计的。
通信距离上也有改变，传统蓝牙的传输距离几十米到几百米不等，BLE则规定为100米。

##低功耗蓝牙特点
*功耗低
*连接更快，无需配对
*异步通讯

##常见两种蓝牙模式
*普通蓝牙连接（2.0）
*BLE(蓝牙4.0)
	
##关键术语和概念
*Generic Attribute Profile（GATT）—GATT配置文件是一个通用规范，用于在BLE链路上发送和接收被称为“属性”的数据块。目前所有的BLE应用都基于GATT。 蓝牙SIG规定了许多低功耗设备的配置文件。配置文件是设备如何在特定的应用程序中工作的规格说明。注意一个设备可以实现多个配置文件。例如，一个设备可能包括心率监测仪和电量检测。
*Attribute Protocol（ATT）—GATT在ATT协议基础上建立，也被称为GATT/ATT。ATT对在BLE设备上运行进行了优化，为此，它使用了尽可能少的字节。每个属性通过一个唯一的的统一标识符（UUID）来标识，每个String类型UUID使用128 bit标准格式。属性通过ATT被格式化为characteristics和services。
*Characteristic 一个characteristic包括一个单一变量和0-n个用来描述characteristic变量的descriptor，characteristic可以被认为是一个类型，类似于类。
*Descriptor Descriptor用来描述characteristic变量的属性。例如，一个descriptor可以规定一个可读的描述，或者一个characteristic变量可接受的范围，或者一个characteristic变量特定的测量单位。
*Service service是characteristic的集合。例如，你可能有一个叫“Heart Rate Monitor(心率监测仪)”的service，它包括了很多characteristics，如“heart rate measurement(心率测量)”等。你可以在bluetooth.org 找到一个目前支持的基于GATT的配置文件和服务列表。


##角色和责任
以下是Android设备与BLE设备交互时的角色和责任：

*中央 VS 外围设备。 适用于BLE连接本身。中央设备扫描，寻找广播；外围设备发出广播。
*GATT 服务端 VS GATT 客户端。决定了两个设备在建立连接后如何互相交流。

为了方便理解，想象你有一个Android手机和一个用于活动跟踪BLE设备，手机支持中央角色，活动跟踪器支持外围（为了建立BLE连接你需要注意两件事，只支持外围设备的两方或者只支持中央设备的两方不能互相通信）。
当手机和运动追踪器建立连接后，他们开始向另一方传输GATT数据。哪一方作为服务器取决于他们传输数据的种类。例如，如果运动追踪器想向手机报告传感器数据，运动追踪器是服务端。如果运动追踪器更新来自手机的数据，手机会作为服务端。
在这份文档的例子中，android app(运行在android设备上）作为GATT客户端。app从gatt服务端获得数据，gatt服务端即支持Heart Rate Profile(心率配置）的BLE心率监测仪。但是你可以自己设计android app去扮演GATT服务端角色

#设备对BLE的支持
分为两种情况
    * 目标设备是否支持BLE
    * Android手机是否支持BLE
    
目标设备是否支持要看具体目标设备的情况，请参考硬件提供商和说明书。
一般情况下Android4.3以后的手机具有蓝牙模块的话都会支持BLE，具体可以再代码中判断。

为了在app中使用蓝牙功能，必须声明蓝牙权限BLUETOOTH。利用这个权限去执行蓝牙通信，例如请求连接、接受连接、和传输数据。
如果想让你的app启动设备发现或操纵蓝牙设置，必须声明BLUETOOTH_ADMIN权限。注意：如果你使用BLUETOOTH_ADMIN权限，你也必须声明BLUETOOTH权限。
在你的app manifest文件中声明蓝牙权限。
 
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>


如果想声明你的app只为具有BLE的设备提供，在manifest文件中包括：

    <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
    
代码中判断手机是否支持BLE特性：

    // 使用此检查确定BLE是否支持在设备上，然后你可以有选择性禁用BLE相关的功能
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        finish();
    }
    
    
#在Android中使用BLE
##1.获取 BluetoothAdapter
所有的蓝牙活动都需要蓝牙适配器。BluetoothAdapter代表设备本身的蓝牙适配器(蓝牙无线）。整个系统只有一个蓝牙适配器，而且你的app使用它与系统交互。

    //使用getSystemService（）返回BluetoothManager，然后将其用于获取适配器的一个实例。
    // 初始化蓝牙适配器
    final BluetoothManager bluetoothManager =
            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();
    
##2.开启蓝牙
调用isEnabled())去检测蓝牙当前是否开启。如果该方法返回false,蓝牙被禁用。下面的代码检查蓝牙是否开启，如果没有开启，将显示错误提示用户去设置开启蓝牙

    // 确保蓝牙在设备上可以开启
    if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
       Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
       startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
    
##3.搜索蓝牙设备
为了发现BLE设备，使用startLeScan())方法。这个方法需要一个参数BluetoothAdapter.LeScanCallback。你必须实现它的回调函数，那就是返回的扫描结果。因为扫描非常消耗电量，你应当遵守以下准则：
*只要找到所需的设备，停止扫描。
*不要在循环里扫描，并且对扫描设置时间限制。以前可用的设备可能已经移出范围，继续扫描消耗电池电量。

       public void cancelDiscovery() {
            if(isDiscovering) {
                isDiscovering = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    
        public boolean isDiscovering() {
            return isDiscovering;
        }
    
        public void startDiscovery() {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            isDiscovering = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cancelDiscovery();
                    if (getCallback() != null)
                        getCallback().onDiscoveryComplete();
                }
            }, SCAN_PERIOD);
    
        }
    
    
        private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (getCallback() != null)
                    getCallback().onDeviceFound(device);
            }
        };
    
##GATT连接
搜索结束后，我们可得到一个搜索结果 BluetoothDevice ，它表示搜到的蓝牙设备
1.调用 
    
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);

可以建立一个GATT连接，它需要一个 回调mGattCallback 参数。
2.在回调方法的 onConnectionStateChange 中，我们可以通过 status 判断是否GATT连接成功
3.在GATT连接建立成功后，我们调用 mBluetoothGatt.discoverServices() 方法 发现GATT服务。
    如果搜到服务将会触发onServicesDiscovered回调


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
                    if (getConnectionCallback() != null)
                        getConnectionCallback().onConnectionLost();
                }
            }
    
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.e(TAG, "onServicesDiscovered received:  SUCCESS");
                    initCharacteristic();
                    try {
                        Thread.sleep(200);//延迟发送，否则第一次消息会不成功
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (getConnectionCallback() != null)
                        getConnectionCallback().onConnected(mBluetoothDevice.getName());
                    setState(ConnectionState.STATE_CONNECTED);
                } else {
                    Log.e(TAG, "onServicesDiscovered error falure " + status);
                    setState(ConnectionState.STATE_NONE);
    
                    if (getConnectionCallback() != null)
                        getConnectionCallback().onConnectionLost();
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

    
##发现服务 （触发onServicesDiscovered）
在发现服务后，会触发 GATT回调的onServicesDiscovered 方法，我们需要在这里初始化我们的操作，包括：
1 查看服务。或者便利查找指定的（和目标硬件UUID符合的）服务。
2 获得指定服务的特征 characteristic1
3 订阅“特征”发生变化的通知”

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
 
##订阅“特征”发生变化的通知”
调用  mBluetoothGatt.setCharacteristicNotification()  方法，传入一个特征 characteristic 对象。
当这个特征里的数据发生变化（接收到数据了），会触发 回调方法的  onCharacteristicChanged 方法。我们在这个回调方法中读取数据。

                final String uuid = "00002902-0000-1000-8000-00805f9b34fb";
                if (mBluetoothGatt == null) throw new NullPointerException();
                mBluetoothGatt.setCharacteristicNotification(characteristic1, true);
                BluetoothGattDescriptor descriptor = characteristic1.getDescriptor(UUID.fromString(uuid));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
  
##读取数据
GATT的回调中有  onCharacteristicChanged 方法，我们在这里可以获得接收的数据

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    Log.e(TAG, "onCharacteristicChanged characteristic: " + characteristic);
                    readCharacteristic(characteristic);
                }
                
调用 characteristic.getValue() 方法，获得字节
    
                    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
                        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                            Log.e(TAG, "BluetoothAdapter not initialized");
                            return;
                        }
                        mBluetoothGatt.readCharacteristic(characteristic);
                        byte[] bytes = characteristic.getValue();
                        String str = new String(bytes);
                        Log.e(TAG, "## readCharacteristic, 读取到: " + str);
                        if (getConnectionCallback() != null)
                            getConnectionCallback().onReadMessage(bytes);
                    }


##写入数据
写入数据时，我们需要先获得特征，特征存在于服务内，一般在发现服务的 onServicesDiscovered 时，查找到特征对象。
    
        public void write(byte[] cmd) {
            Log.e(TAG, "write:" + new String(cmd));
            synchronized (LOCK) {
                characteristic2.setValue(cmd);
                characteristic2.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                mBluetoothGatt.writeCharacteristic(characteristic2);
    
                if (getConnectionCallback() != null)
                    getConnectionCallback().onWriteMessage(cmd);
            }
        }
    
##关闭蓝牙连接
public void close() {
    if (mBluetoothGatt == null) {
        return;
    }
    mBluetoothGatt.close();
    mBluetoothGatt = null;
}
    
 #参考
 https://developer.android.com/guide/topics/connectivity/bluetooth-le.html
 