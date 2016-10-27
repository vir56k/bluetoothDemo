package com.zhangyunfei.bluetirepersuretools.activity.lookservice;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bluetoothlib.BlueToothMode;
import com.example.bluetoothlib.BluetoothConnectionCreator;
import com.example.bluetoothlib.contract.ConnectionChannel;
import com.example.bluetoothlib.contract.ConnectionState;
import com.zhangyunfei.bluetirepersuretools.R;
import com.zhangyunfei.bluetirepersuretools.activity.BluetoothConnectionCallbackImpl;

public class ServiceListActivity extends Activity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private ConnectionChannel bluetoothConnection = null;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int CONNECTED = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Debugging
    private static final String TAG = "BluetoothDemoActivity";
    private static final boolean D = true;
    public String mConnectedDeviceName;
    private BluetoothAdapter mBluetoothAdapter;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);
        address = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);

        bluetoothConnection = BluetoothConnectionCreator.createConnectionByType(BlueToothMode.MODE_BLE, this, new BluetoothConnectionCallbackImpl(mHandler));
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        findViewById(R.id.btnConn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(address);
                try {
                    bluetoothConnection.connect(remoteDevice.getAddress(),false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(bluetoothConnection != null)
            bluetoothConnection.close();
        super.onDestroy();
    }

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "## 状态发生改变: " + msg.arg1);
                    switch (msg.arg1) {
                        case ConnectionState.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
//                            mConversationArrayAdapter.clear();
                            break;
                        case ConnectionState.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case ConnectionState.STATE_LISTEN:
                        case ConnectionState.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("发送:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf);
//                    mConversationArrayAdapter.add("收到:  " + readMessage);
                    break;
                case CONNECTED:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "已连接到 "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //先关闭回显
//                    sendMessageTo("ATE0");
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
