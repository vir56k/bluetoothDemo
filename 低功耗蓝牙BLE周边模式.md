#低功耗蓝牙BLE外围模式(peripheral)-使用BLE作为服务端

## Android对外模模式(peripheral)的支持
从Android5.0开始才支持

##关键术语和概念
以下是关键BLE术语和概念的摘要：

* 通用属性简档（GATT） - GATT简档是用于通过BLE链路发送和接收称为“属性”的短数据块的一般规范。 所有当前的低能量应用配置文件都基于GATT。
蓝牙SIG为低能量设备定义了许多配置文件 。 配置文件是设备在特定应用程序中的工作方式的规范。 请注意，设备可以实现多个配置文件。 例如，设备可以包含心率监视器和电池水平检测器。
* 属性协议（ATT） -GATT建立在属性协议（ATT）之上。 这也称为GATT / ATT。 ATT经过优化，可在BLE设备上运行。 为此，它使用尽可能少的字节。 每个属性由通用唯一标识符（UUID）唯一标识，UUID是用于唯一标识信息的字符串ID的标准化128位格式。 由ATT传送的属性被格式化为特征和服务 。
* 特性 -A特性包含描述特性值的单个值和0-n个描述符。 一个特性可以被认为是一个类型，类似于类。
* 描述符 - 描述符是描述特征值的定义属性。 例如，描述符可以指定人类可读的描述，特征值的可接受范围或特征值的特定的测量单位。
* 服务 - 服务是一个集合的特点。 例如，您可以有一个名为“心率监视器”的服务，其中包括诸如“心率测量”的特征。 您可以在bluetooth.org上找到现有基于GATT的个人资料和服务的列表 。


##角色和职责
以下是Android设备与BLE设备互动时适用的角色和职责：

##中央与外围。 这适用于BLE连接本身。 处于中心角色的设备扫描，寻找广告，并且外围角色中的设备进行广告。
GATT服务器与GATT客户端。 这决定了两个设备在建立连接后如何相互通信。


#BLE权限
首先，需要在manifest中声明使用蓝牙和操作蓝牙的权限

在应用程序清单文件中声明蓝牙权限。 例如：
 <uses-permission android:name="android.permission.BLUETOOTH"/>
 <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>

如果您要声明自己的应用只适用于支持BLE的设备，请在应用清单中包含以下内容：

    <uses-feature android：name =“android.hardware.bluetooth_le”android：required =“true”/>

不过，如果您想让应用程式适用于不支援BLE的装置，您仍应在应用的清单中加入这个元素，但required="false"设为required="false" 。
然后在运行时，您可以通过使用PackageManager.hasSystemFeature()确定BLE可用性：

     // Use this check to determine whether BLE is supported on the device.  Then
     // you can selectively disable BLE-related features.
     if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
         Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
         finish();
     }

在android 6.0 以后，要想获得蓝牙扫描结果，还需要下面的权限

     <manifest ... >
         <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
         ...
         <!-- Needed only if your app targets Android 5.0 (API level 21) or higher.  -->
         <uses-feature android:name="android.hardware.location.gps" />
         ...
     </manifest>

#设置蓝牙

## 1.Get the BluetoothAdapter
获得蓝牙适配器

     private BluetoothAdapter mBluetoothAdapter;
     ...
     // Initializes Bluetooth adapter.
     final BluetoothManager bluetoothManager =
             (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
     mBluetoothAdapter = bluetoothManager.getAdapter();

## 2.Enable Bluetooth
打开蓝牙

     // Ensures Bluetooth is available on the device and it is enabled.  If not,
     // displays a dialog requesting user permission to enable Bluetooth.
     if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
         Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
     }

# 3.初始化BLE蓝牙广播（广告）

(1)广播的设置
(2)设置广播的数据
(3)设置响应的数据
(4)设置连接回调

        private void initGATTServer() {
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setConnectable(true)
                    .build();

            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true)
                    .build();

            AdvertiseData scanResponseData = new AdvertiseData.Builder()
                    .addServiceUuid(new ParcelUuid(UUID_SERVER))
                    .setIncludeTxPowerLevel(true)
                    .build();


            AdvertiseCallback callback = new AdvertiseCallback() {

                @Override
                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                    Log.d(TAG, "BLE advertisement added successfully");
                    showText("1. initGATTServer success");
                    println("1. initGATTServer success");
                    initServices(getContext());
                }

                @Override
                public void onStartFailure(int errorCode) {
                    Log.e(TAG, "Failed to add BLE advertisement, reason: " + errorCode);
                    showText("1. initGATTServer failure");
                }
            };

            BluetoothLeAdvertiser bluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, scanResponseData, callback);
        }

在被BLE设备连接后，将触发 AdvertiseCallback 的 onStartSuccess，我们在这之后，初始化GATT的服务

# 4.初始化GATT的服务
(1) 通过 mBluetoothManager.openGattServer() 获得 bluetoothGattServer
(2) 添加 服务，特征，描述。这些内容要让客户端知道。


        private void initServices(Context context) {
            bluetoothGattServer = mBluetoothManager.openGattServer(context, bluetoothGattServerCallback);
            BluetoothGattService service = new BluetoothGattService(UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);

            //add a read characteristic.
            characteristicRead = new BluetoothGattCharacteristic(UUID_CHARREAD, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
            //add a descriptor
            BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(UUID_DESCRIPTOR, BluetoothGattCharacteristic.PERMISSION_WRITE);
            characteristicRead.addDescriptor(descriptor);
            service.addCharacteristic(characteristicRead);

            //add a write characteristic.
            BluetoothGattCharacteristic characteristicWrite = new BluetoothGattCharacteristic(UUID_CHARWRITE,
                    BluetoothGattCharacteristic.PROPERTY_WRITE |
                            BluetoothGattCharacteristic.PROPERTY_READ |
                            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                    BluetoothGattCharacteristic.PERMISSION_WRITE);
            service.addCharacteristic(characteristicWrite);

            bluetoothGattServer.addService(service);
            Log.e(TAG, "2. initServices ok");
            showText("2. initServices ok");
        }

在 openGattServer 方法中，我们需要传入个回调
        bluetoothGattServer = mBluetoothManager.openGattServer(context, bluetoothGattServerCallback);
## 5.配置数据交互回调
回调时间有：连接状态变化，收发消息，通知消息

    /**
         * 服务事件的回调
         */
        private BluetoothGattServerCallback bluetoothGattServerCallback = new BluetoothGattServerCallback() {

            /**
             * 1.连接状态发生变化时
             * @param device
             * @param status
             * @param newState
             */
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                Log.e(TAG, String.format("1.onConnectionStateChange：device name = %s, address = %s", device.getName(), device.getAddress()));
                Log.e(TAG, String.format("1.onConnectionStateChange：status = %s, newState =%s ", status, newState));
                super.onConnectionStateChange(device, status, newState);
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
                Log.e(TAG, String.format("onServiceAdded：status = %s", status));
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                Log.e(TAG, String.format("onCharacteristicReadRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
                Log.e(TAG, String.format("onCharacteristicReadRequest：requestId = %s, offset = %s", requestId, offset));

                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
    //            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            }

            /**
             * 3. onCharacteristicWriteRequest,接收具体的字节
             * @param device
             * @param requestId
             * @param characteristic
             * @param preparedWrite
             * @param responseNeeded
             * @param offset
             * @param requestBytes
             */
            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] requestBytes) {
                Log.e(TAG, String.format("3.onCharacteristicWriteRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
                Log.e(TAG, String.format("3.onCharacteristicWriteRequest：requestId = %s, preparedWrite=%s, responseNeeded=%s, offset=%s, value=%s", requestId, preparedWrite, responseNeeded, offset, OutputStringUtil.toHexString(requestBytes)));
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, requestBytes);
                //4.处理响应内容
                onResponseToClient(requestBytes, device, requestId, characteristic);
            }

            /**
             * 2.描述被写入时，在这里执行 bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS...  收，触发 onCharacteristicWriteRequest
             * @param device
             * @param requestId
             * @param descriptor
             * @param preparedWrite
             * @param responseNeeded
             * @param offset
             * @param value
             */
            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                Log.e(TAG, String.format("2.onDescriptorWriteRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
                Log.e(TAG, String.format("2.onDescriptorWriteRequest：requestId = %s, preparedWrite = %s, responseNeeded = %s, offset = %s, value = %s,", requestId, preparedWrite, responseNeeded, offset, OutputStringUtil.toHexString(value)));

                // now tell the connected device that this was all successfull
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            }

            /**
             * 5.特征被读取。当回复响应成功后，客户端会读取然后触发本方法
             * @param device
             * @param requestId
             * @param offset
             * @param descriptor
             */
            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                Log.e(TAG, String.format("onDescriptorReadRequest：device name = %s, address = %s", device.getName(), device.getAddress()));
                Log.e(TAG, String.format("onDescriptorReadRequest：requestId = %s", requestId));
    //            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
                bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                super.onNotificationSent(device, status);
                Log.e(TAG, String.format("5.onNotificationSent：device name = %s, address = %s", device.getName(), device.getAddress()));
                Log.e(TAG, String.format("5.onNotificationSent：status = %s", status));
            }

            @Override
            public void onMtuChanged(BluetoothDevice device, int mtu) {
                super.onMtuChanged(device, mtu);
                Log.e(TAG, String.format("onMtuChanged：mtu = %s", mtu));
            }

            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                super.onExecuteWrite(device, requestId, execute);
                Log.e(TAG, String.format("onExecuteWrite：requestId = %s", requestId));
            }
        };

## 6.处理来自客户端发来的数据和发送回复数据：
调用 bluetoothGattServer.notifyCharacteristicChanged 方法，通知数据改变。

            /**
             * 4.处理响应内容
             *
             * @param reqeustBytes
             * @param device
             * @param requestId
             * @param characteristic
             */
            private void onResponseToClient(byte[] reqeustBytes, BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic) {
                Log.e(TAG, String.format("4.onResponseToClient：device name = %s, address = %s", device.getName(), device.getAddress()));
                Log.e(TAG, String.format("4.onResponseToClient：requestId = %s", requestId));
                String msg = OutputStringUtil.transferForPrint(reqeustBytes);
                println("4.收到:" + msg);
                showText("4.收到:" + msg);

                String str = new String(reqeustBytes) + " hello>";
                characteristicRead.setValue(str.getBytes());
                bluetoothGattServer.notifyCharacteristicChanged(device, characteristicRead, false);

                println("4.响应:" + str);
                showText("4.响应:" + str);
            }

### 交互流程：
(1) 当客户端开始写入数据时： 触发回调方法 onDescriptorWriteRequest
(2) 在 onDescriptorWriteRequest 方法中，执行下面的方法表示 写入成功 BluetoothGatt.GATT_SUCCESS

           bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);

    执行 sendResponse后，会触发回调方法 onCharacteristicWriteRequest
(3) 在 onCharacteristicWriteRequest方法中

            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] requestBytes) {

    这个里可以获得 来自客户端发来的数据 requestBytes
(4) 处理响应内容,我写了这个方法：

        onResponseToClient(requestBytes, device, requestId, characteristic);
    在这个方法中，通过 bluetoothGattServer.notifyCharacteristicChanged()方法 回复数据

###通过日志，我们看看事件触发的顺序

    1.onConnectionStateChange：device name = null, address = 74:32:DE:49:3C:28
    1.onConnectionStateChange：status = 0, newState =2
    2.onDescriptorWriteRequest：device name = null, address = 74:32:DE:49:3C:28
    2.onDescriptorWriteRequest：requestId = 1, preparedWrite = false, responseNeeded = true, offset = 0, value = [01,00,],
    3.onCharacteristicWriteRequest：device name = null, address = 74:32:DE:49:3C:28
    3.onCharacteristicWriteRequest：requestId = 2, preparedWrite=false, responseNeeded=false, offset=0, value=[41,54,45,30,0D,]
    4.onResponseToClient：device name = null, address = 74:32:DE:49:3C:28
    4.onResponseToClient：requestId = 2
    4.收到:ATE0
    4.响应:ATE0 hello>
    5.onNotificationSent：device name = null, address = 74:32:DE:49:3C:28
    5.onNotificationSent：status = 0

代码托管到github:

https://github.com/vir56k/bluetoothDemo   找到 bleperipheraldemo	 文件夹

