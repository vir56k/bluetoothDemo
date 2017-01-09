package obd.mapbar.com.bluetoolth2server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERY = 2;
    private static final String NAME = "ZYF-test";
    private static final UUID SPP_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "MainActivity";


    private TextView textView1;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver bluetoothStateReceiver;
    private BroadcastReceiver scanModeChangedReceiver;
    private AcceptThread acceptThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textview1);

        runServer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeStateListener();
        removeScanModeChangedListener();
    }

    private void runServer() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //the device doesn't support bluetooth
        } else {
            //the device support bluetooth
            addStateListener();

            enalbeAdapter();
            acceptThread = new AcceptThread();
            acceptThread.start();
            enableBeDiscovery();

            addScanModeChangedListener();
        }
    }

    /**
     * 默认情况下，设备将变为可检测到并持续 120 秒钟。 您可以通过添加 EXTRA_DISCOVERABLE_DURATION Intent Extra 来定义不同的持续时间。 应用可以设置的最大持续时间为 3600 秒，值为 0 则表示设备始终可检测到。 任何小于 0 或大于 3600 的值都会自动设为 120 秒。 例如，以下片段会将持续时间设为 300 秒：
     */
    private void enableBeDiscovery() {
        int MAX = 300;
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, MAX);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERY);
        println("## enableBeDiscovery");
        showText(String.format("开启蓝牙可见 %s秒", MAX));
    }


    private void addStateListener() {
        bluetoothStateReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String stateExtra = BluetoothAdapter.EXTRA_STATE;

                String stateStr = "未知";
                int state = intent.getIntExtra(stateExtra, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        stateStr = "STATE_TURNING_ON";
                        break;
                    case BluetoothAdapter.STATE_ON:
                        stateStr = "STATE_ON";
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        stateStr = "STATE_TURNING_OFF";
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        stateStr = "STATE_OFF";
                        break;
                }
                showText(String.format("蓝牙状态变化: %s", stateStr));

            }
        };

        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        println("## invoke addStateListener");
    }

    private void println(String msg) {
        Log.e(TAG, msg);
    }

    private void removeStateListener() {
        if (bluetoothStateReceiver != null) {
            unregisterReceiver(bluetoothStateReceiver);
            bluetoothStateReceiver = null;
        }
    }


    private void addScanModeChangedListener() {
        scanModeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                int preScanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0);
                //枚举：SCAN_MODE_CONNECTABLE_DISCOVERABLE、 SCAN_MODE_CONNECTABLE 或 SCAN_MODE_NONE
                showText(String.format("扫描模式改变：%s => %s", scanModeToString(preScanMode), scanModeToString(scanMode)));

            }
        };

        registerReceiver(scanModeChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
    }

    private String scanModeToString(int scanMode) {
        String str = "未知";
        switch (scanMode) {
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                str = "SCAN_MODE_CONNECTABLE_DISCOVERABLE";
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                str = "SCAN_MODE_CONNECTABLE";
                break;
            case BluetoothAdapter.SCAN_MODE_NONE:
                str = "SCAN_MODE_NONE";
                break;
        }
        return str;
    }

    private void removeScanModeChangedListener() {
        if (scanModeChangedReceiver != null)
            unregisterReceiver(scanModeChangedReceiver);
    }

    private void enalbeAdapter() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, SPP_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            println("## AcceptThread run");
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    println("## accept done");
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }


    ServerReadThread mServerReadThread;

    private void manageConnectedSocket(BluetoothSocket socket) {
        mServerReadThread = new ServerReadThread(socket);
        mServerReadThread.start();
    }

    private static final int MSG_SERVER_READ = 1;
    private static final int MSG_APPEND = 2;

    // 读取数据
    private class ServerReadThread extends Thread {
        BluetoothSocket mSocket;

        public ServerReadThread(BluetoothSocket socket) {
            this.mSocket = socket;
        }

        public void run() {
            println("## ServerReadThread run");
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream is = null;
            try {
                is = mSocket.getInputStream();
                while (true) {
                    if ((bytes = is.read(buffer)) > 0) {

                        byte[] result = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            result[i] = buffer[i];
                        }


                        String s = new String(result);
                        Message msg = new Message();
                        msg.obj = s;
                        msg.what = MSG_SERVER_READ;
                        mHandler.sendMessage(msg);
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (MSG_SERVER_READ == msg.what) {
                if (textView1.getText().length() > 300) {
                    textView1.setText("");
                }
                textView1.append("收到: " + msg.obj.toString() + "\r\n");
                return;
            } else if (MSG_APPEND == msg.what) {
                if (textView1.getText().length() > 300) {
                    textView1.setText("");
                }
                textView1.append(msg.obj.toString() + "\r\n");
                return;
            }
            super.handleMessage(msg);
        }
    };

    private void showText(String msg) {
        mHandler.obtainMessage(MSG_APPEND, msg).sendToTarget();
    }
}
