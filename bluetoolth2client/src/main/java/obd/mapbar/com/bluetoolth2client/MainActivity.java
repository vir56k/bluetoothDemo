package obd.mapbar.com.bluetoolth2client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private ListView listview1;
    private Button button1;
    private ArrayAdapter<String> mArrayAdapter;
    private BroadcastReceiver mDiscoveryReceiver;
    private List<BluetoothDevice> datasource;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview1 = (ListView) findViewById(R.id.listview1);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discovery();
            }

        });

        datasource = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listview1.setAdapter(mArrayAdapter);
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetoothDevice = datasource.get(position);
                Intent intent = new Intent(MainActivity.this, CommActivity.class);
                intent.putExtra("device", bluetoothDevice);
                startActivity(intent);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        addDiscoveryReceiver();

    }


    private void discovery() {
        datasource.clear();
        mArrayAdapter.clear();
        findBoundDevices();
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeDiscoveryReceiver();
    }

    private void addDiscoveryReceiver() {
        // Create a BroadcastReceiver for ACTION_FOUND
        mDiscoveryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    print(device.getName() + "\n" + device.getAddress());
                    datasource.add(device);
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mDiscoveryReceiver, filter); // Don't forget to unregister during onDestroy

    }

    private void removeDiscoveryReceiver() {
        if (mDiscoveryReceiver != null) {
            unregisterReceiver(mDiscoveryReceiver);
            mDiscoveryReceiver = null;
        }
    }

    private void findBoundDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                print(device.getName() + "\n" + device.getAddress());
                datasource.add(device);
            }
        }
    }

    private void print(String msg) {
        Log.e(TAG, msg);
    }
}
