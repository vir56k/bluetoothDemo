package com.zhangyunfei.bluetirepersuretools.activity.lookservice;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bluetoothlib.BlueToothMode;
import com.example.bluetoothlib.BluetoothConnectionCreator;
import com.example.bluetoothlib.contract.BlueToothDiscovery;
import com.example.bluetoothlib.contract.DeviceDiscoveryCallback;
import com.zhangyunfei.bluetirepersuretools.R;

public class LookServiceActivity extends Activity {
    private BlueToothDiscovery blueToothDiscovery;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private static final String TAG = "LookServiceActivity";
    private boolean isDiscovering = false;
    private Button btnSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_look_service);

        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);


        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        blueToothDiscovery = BluetoothConnectionCreator.createDiscovery(BlueToothMode.MODE_AUTO, this, deviceDiscoveryCallback);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDiscovering)
                    doDiscovery();
            }
        });

        doDiscovery();
    }


    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            blueToothDiscovery.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent(LookServiceActivity.this, ServiceListActivity.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            startActivity(intent);
//            finish();
        }
    };

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        isDiscovering = true;
        mNewDevicesArrayAdapter.clear();
        mNewDevicesArrayAdapter.notifyDataSetChanged();
        // Request discover from BluetoothAdapter
        blueToothDiscovery.startDiscovery();
    }


    private DeviceDiscoveryCallback deviceDiscoveryCallback = new DeviceDiscoveryCallback() {
        @Override
        public void onDeviceFound(BluetoothDevice device) {
            String str = device.getName() + "\n" + device.getAddress();
            for (int i = 0; i < mNewDevicesArrayAdapter.getCount(); i++) {
                if (mNewDevicesArrayAdapter.getItem(i).equals(str))
                    return;
            }
            mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        @Override
        public void onDiscoveryComplete() {
            isDiscovering = false;
            setProgressBarIndeterminateVisibility(false);
            setTitle(R.string.select_device);
            if (mNewDevicesArrayAdapter.getCount() == 0) {
                String noDevices = getResources().getText(R.string.none_found).toString();
                mNewDevicesArrayAdapter.add(noDevices);
                mNewDevicesArrayAdapter.notifyDataSetChanged();
            }
        }
    };
}
