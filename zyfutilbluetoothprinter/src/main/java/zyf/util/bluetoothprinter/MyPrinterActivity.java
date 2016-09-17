package zyf.util.bluetoothprinter;

import zyf.util.bluetoothprinter.core.PrintUtil;
import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.bluetoothprinter.service.PrintDataService;
import com.jerry.bluetoothprinter.view.BluetoothActivity;

public class MyPrinterActivity extends Activity {
	private static final String LAST_DEVICE_ADDRESS = "last_deviceAddress";
	private static final String LAST_DEVICE_NAME = "LAST_DEVICE_NAME";
	private static final int REQUEST_SETTINGS = 2;

	private static final int MESSAGE_CODE_STATE_Connecting = 2;
	private static final int MESSAGE_CODE_STATE_Connected = 3;
	private static final int MESSAGE_CODE_STATE_Failure = 4;
	private static final int MESSAGE_CODE_SHOW_PRINTER_INFO = 5;
	private static final int MESSAGE_CODE_CONNECT = 6;

	Waybill4PrintInfo bean;
	WaybillPrintTemplate template;

	public PrintDataService printDataService = null;
	String mDeviceAddress;
	View btn_print;
	View btn_settings;
	View printer_info;
	TextView textview_printer_name;
	TextView textView_state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_printer_activity);

		Intent intent = getIntent();
		if (intent == null)
			throw new NullPointerException("缺少必须的参数");
		bean = (Waybill4PrintInfo) intent.getSerializableExtra("bean");
		template = (WaybillPrintTemplate) intent.getSerializableExtra("template");
		if (bean == null)
			throw new NullPointerException("缺少必须的参数:bean");
		if (template == null)
			throw new NullPointerException("缺少必须的参数:template");

		printer_info = findViewById(R.id.printer_info);
		textView_state = (TextView) findViewById(R.id.textView_state);
		btn_print = findViewById(R.id.btn_print);
		btn_print.setVisibility(View.INVISIBLE);
		btn_print.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doPrint();
			}

		});
		textview_printer_name = (TextView) findViewById(R.id.textview_printer_name);
		btn_settings = findViewById(R.id.btn_settings);
		btn_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MyPrinterActivity.this, BluetoothActivity.class),
						REQUEST_SETTINGS);
			}
		});
		loadPrinterInfo();
	}

	Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_CODE_STATE_Connecting:
				textView_state.setText("连接中...");
				break;
			case MESSAGE_CODE_STATE_Connected:
				textView_state.setText("连接成功");
				btn_print.setVisibility(View.VISIBLE);
				break;
			case MESSAGE_CODE_STATE_Failure:
				textView_state.setText("连接失败");
				btn_print.setVisibility(View.INVISIBLE);
				break;
			case MESSAGE_CODE_SHOW_PRINTER_INFO:
				post(new Runnable() {

					@Override
					public void run() {
						loadPrinterInfo();
					}
				});
				break;
			case MESSAGE_CODE_CONNECT:
				connect();
				break;
			default:
				break;
			}

		};
	};
	private String mDeviceName;

	private void loadPrinterInfo() {
		SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
		mDeviceAddress = sp.getString(LAST_DEVICE_ADDRESS, null);
		mDeviceName = sp.getString(LAST_DEVICE_NAME, null);
		Log.d("SP", "尝试发现上次使用的打印机：" + mDeviceAddress);
		if (TextUtils.isEmpty(mDeviceAddress) || TextUtils.isEmpty(mDeviceName)) {
			printer_info.setVisibility(View.INVISIBLE);
			btn_print.setVisibility(View.INVISIBLE);
			return;
		} else {
			printer_info.setVisibility(View.VISIBLE);
			btn_print.setVisibility(View.VISIBLE);
		}
		textview_printer_name.setText(mDeviceName);

		new MyTask().execute();
	}

	private class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			connect();
			return null;
		}

	}

	private void connect() {
		myHandler.obtainMessage(MESSAGE_CODE_STATE_Connecting).sendToTarget();
		try {
			this.printDataService = new PrintDataService(this, mDeviceAddress);
			// 一上来就先连接蓝牙设备
			boolean flag = this.printDataService.connect();
			if (flag == false) {
				// 连接失败
				myHandler.obtainMessage(MESSAGE_CODE_STATE_Failure).sendToTarget();
			} else {
				// 连接成功
				myHandler.obtainMessage(MESSAGE_CODE_STATE_Connected).sendToTarget();
			}
		} catch (Exception e) {
			e.printStackTrace();
			myHandler.obtainMessage(MESSAGE_CODE_STATE_Failure).sendToTarget();
		}
	}

	private void doPrint() {
		// // 设置当前设备名称
		// this.deviceName.setText(this.printDataService.getDeviceName());

		// 一上来就先连接蓝牙设备
		boolean flag = this.printDataService.connect();
		if (flag == false) {
			// 连接失败
			alert("连接失败！");

		} else {
			// 连接成功
			PrintUtil.create(printDataService).setTemplate(template).setData(bean).print();
		}
	}

	private void alert(String string) {
		Toast.makeText(this, string, 0).show();
	}

	@Override
	protected void onDestroy() {
		PrintDataService.disconnect();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_SETTINGS == requestCode && resultCode == Activity.RESULT_OK && data != null) {
			String deviceAddress = data.getStringExtra("deviceAddress");
			String deviceName = data.getStringExtra("deviceName");
			if (TextUtils.isEmpty(deviceAddress))
				return;
			SharedPreferences sp = getSharedPreferences("SP", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(LAST_DEVICE_ADDRESS, deviceAddress);
			editor.putString(LAST_DEVICE_NAME, deviceName);
			editor.commit();
			myHandler.obtainMessage(MESSAGE_CODE_SHOW_PRINTER_INFO).sendToTarget();

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
