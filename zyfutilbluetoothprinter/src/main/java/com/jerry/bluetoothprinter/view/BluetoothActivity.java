package com.jerry.bluetoothprinter.view;

import zyf.util.bluetoothprinter.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.jerry.bluetoothprinter.action.BluetoothAction;

public class BluetoothActivity extends Activity
{

	private Context context = null;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.context = this;
		setTitle("蓝牙打印");
		setContentView(R.layout.bluetooth_layout);
		this.initListener();
	}

	private void initListener()
	{
		ListView unbondDevices = (ListView) this.findViewById(R.id.unbondDevices);
		ListView bondDevices = (ListView) this.findViewById(R.id.bondDevices);
		Button switchBT = (Button) this.findViewById(R.id.openBluetooth_tb);
		Button searchDevices = (Button) this.findViewById(R.id.searchDevices);

		BluetoothAction bluetoothAction = new BluetoothAction(this, unbondDevices,
				bondDevices, switchBT, searchDevices, BluetoothActivity.this);

		Button returnButton = (Button) this.findViewById(R.id.return_Bluetooth_btn);
		bluetoothAction.setSearchDevices(searchDevices);
		bluetoothAction.initView();

		switchBT.setOnClickListener(bluetoothAction);
		searchDevices.setOnClickListener(bluetoothAction);
		returnButton.setOnClickListener(bluetoothAction);
	}

	// 屏蔽返回键的代码:
	/*public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/
	

}
