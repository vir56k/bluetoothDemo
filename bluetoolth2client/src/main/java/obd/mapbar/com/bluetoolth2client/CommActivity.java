package obd.mapbar.com.bluetoolth2client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.os.Looper.loop;

public class CommActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MSG_CLIENT_READ = 1;
    private static final int MSG_APPEND = 2;
    private static final String TAG = "CommActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private static final UUID SPP_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private TextView textView1;
    private ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);
        textView1 = (TextView) findViewById(R.id.textView1);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        BluetoothDevice bluetoothDevice = getIntent().getParcelableExtra("device");
        connect(bluetoothDevice);


    }


    private void onConnectedDevice() {
        startLoop();
    }

    private int num = 0;

    private void startLoop() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (connectedThread != null) {
                    num++;
                    String cmd = String.format("[%s] go >", num);
                    connectedThread.write(cmd.getBytes());
                    print("ready send: " + cmd);
                    showText("ready send: " + cmd);
                }
                startLoop();
            }

        }, 200);
    }

    private void connect(BluetoothDevice bluetoothDevice) {
        ConnectThread connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();


    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            print("## ConnectThread run");
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);

        }


        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            onConnectedDevice();
            print("## ConnectedThread run");
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MSG_CLIENT_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }


        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            if (bytes == null) {
                return;
            }
            print("## ConnectedThread write: " + new String(bytes));
            showText("write: " + new String(bytes) + "\r\n");
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (MSG_CLIENT_READ == msg.what) {
                if (textView1.getText().length() > 300) {
                    textView1.setText("");
                }
                String str = new String((byte[]) msg.obj);
                textView1.append(str + "\r\n");
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

    private void print(String msg) {
        Log.e(TAG, msg);
    }


    private void showText(String msg) {
        mHandler.obtainMessage(MSG_APPEND, msg).sendToTarget();
    }
}
