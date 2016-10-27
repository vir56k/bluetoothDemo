/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhangyunfei.bluetirepersuretools.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothlib.ble.BleConnectionChannelExtra;
import com.example.bluetoothlib.contract.ConnectionState;
import com.example.bluetoothlib.util.BluetoothAdapterUtil;
import com.zhangyunfei.bluetirepersuretools.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * 收发消息同步
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothTongbuActivity2 extends Activity {
    // Debugging
    private static final String TAG = "BluetoothTongbu";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int CONNECTED = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 3;
    private int TYPE = 0;

    // Layout Views
    private ListView mConversationView;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuilder mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BleConnectionChannelExtra bluetoothConnection = null;

    private TextView edit_text_out;
    private MyHandlerLoop myHandlerLoop;
    private Switch switch1_loop;
    private byte limit = 0x3E;
    private boolean isLoop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.bluetooth_demo_activity);
        edit_text_out = (TextView) findViewById(R.id.edit_text_out);
        switch1_loop = (Switch) findViewById(R.id.switch1_loop);
        switch1_loop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myHandlerLoop.stopLoop();
                isLoop = false;
            }
        });

        myHandlerLoop = new MyHandlerLoop(this);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (getIntent() != null) {
            TYPE = getIntent().getIntExtra("TYPE", 0);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (bluetoothConnection == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothConnection != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothConnection.getState() == ConnectionState.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothConnection.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        edit_text_out = (EditText) findViewById(R.id.edit_text_out);
//        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                String message = edit_text_out.getText().toString();
                if (isEnableLoop()) {
//                    runLoop();
                    isLoop = true;
                    runLoopWhile();
                    myHandlerLoop.startLoopSend();
                } else {
                    sendMessageTo(message);
                }
            }
        });

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuilder("");

        // Initialize the BluetoothChatService to perform bluetooth connections
//        bluetoothConnection = BluetoothConnectionCreator.createConnection(this, new BluetoothConnectionCallbackImpl(mHandler));
        bluetoothConnection = new BleConnectionChannelExtra(this,
                BluetoothAdapterUtil.getBluetoothAdapter(this),
                new BluetoothConnectionCallbackImpl(mHandler));

    }

    private void runLoopWhile() {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (isLoop) {
                    justSendAndrReceive("ATI");
                }
            }
        });
    }

    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);

    private void runLoop() {
        myHandlerLoop.post(new Runnable() {
            @Override
            public void run() {
                sendOnThread();
                runLoop();
            }
        });

    }

    private void sendOnThread() {
        if (fixedThreadPool == null) return;
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "## invoke sendOnThread ============ threadid = " + Thread.currentThread().getName());
                synchronized (this) {
                    justSendAndrReceive("ATI");
                }
            }


        });
    }

    private void justSend(String message) {
        Log.e(TAG, "## invoke justSend ============ threadid = " + Thread.currentThread().getName());
        try {
             bluetoothConnection.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void justSendAndrReceive(String message) {
        Log.e(TAG, "## invoke justSendAndrReceive ============ threadid = " + Thread.currentThread().getName());
        try {
            byte[] res = bluetoothConnection.sendAndReceive(message.getBytes(), limit, 15000);
            showResponse(res);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            showResponse("超时".getBytes());
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fixedThreadPool.shutdownNow();

        myHandlerLoop.stopLoop();
        // Stop the Bluetooth chat services
        if (bluetoothConnection != null) bluetoothConnection.close();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private synchronized void sendMessageTo(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothConnection.getState() != ConnectionState.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            mOutStringBuffer.append(message);
            mOutStringBuffer.append("\r");
            // Get the message bytes and tell the BluetoothChatService to write
            justSendAndrReceive(message);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
        }
    }

    private void showSend(byte[] bytes) {
        mHandler.obtainMessage(MESSAGE_WRITE, bytes).sendToTarget();
    }

    private void showResponse(byte[] bytes) {
        mHandler.obtainMessage(MESSAGE_READ, bytes).sendToTarget();
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
                    Log.d(TAG, "## 消息状态发生改变: " + msg.arg1);
                    switch (msg.arg1) {
                        case ConnectionState.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
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
                    mConversationArrayAdapter.add(String.format("[%s]发送:  %s", getNowTime(), writeMessage));
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf);
                    mConversationArrayAdapter.add(String.format("[%s]收到: %s ", getNowTime(), readMessage));
                    break;
                case CONNECTED:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "已连接到 "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //先关闭回显
                    justSend("ATE0\r");
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        connectDevice(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data) throws Exception {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
//        bluetoothConnection.connect(device);
        bluetoothConnection.connect(address, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan://触发扫描设备
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable://可被发现设备
                // Ensure this device is discoverable by others
                bluetoothConnection.ensureDiscoverable(getActivity());
                return true;
        }
        return false;
    }

    public boolean isEnableLoop() {
        return switch1_loop.isChecked();
    }

    public Activity getActivity() {
        return this;
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss.SSS");

    public String getNowTime() {
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public static class MyHandlerLoop extends Handler {
        private static final int MSG_SEND_LOOP = 1;
        private static final long LOOP_INTERVAL = 100;
        BluetoothTongbuActivity2 bluetoothDemoActivity;

        public MyHandlerLoop(BluetoothTongbuActivity2 bluetoothDemoActivity) {
            this.bluetoothDemoActivity = bluetoothDemoActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            if (bluetoothDemoActivity == null || bluetoothDemoActivity.isFinishing())
                return;
            if (msg.what == MSG_SEND_LOOP) {
                removeMessages(MSG_SEND_LOOP);
                if (bluetoothDemoActivity.isEnableLoop()) {
                    String message = bluetoothDemoActivity.edit_text_out.getText().toString();
                    if (!TextUtils.isEmpty(message)) {
                        bluetoothDemoActivity.justSendAndrReceive(message);
                        sendMessageDelayed(obtainMessage(MSG_SEND_LOOP), LOOP_INTERVAL);
                    }
                }
            }
            super.handleMessage(msg);
        }


        public void stopLoop() {
            removeMessages(MSG_SEND_LOOP);
        }

        public void startLoopSend() {
            sendMessageDelayed(obtainMessage(MSG_SEND_LOOP), LOOP_INTERVAL);
        }
    }

}
