package com.jerry.bluetoothprinter.view;

import zyf.util.bluetoothprinter.R;
import zyf.util.bluetoothprinter.core.PrintUtil;
import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_zhongtie;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.bluetoothprinter.service.PrintDataService;

public class PrintDataActivity extends Activity {
	private Context context = null;

	Button send;
	Button command;
	Button printPicture;
	EditText printData;

	private TextView deviceName = null;
	private TextView connectState = null;

	private String deviceAddress = null;
	public PrintDataService printDataService = null;

	final String[] items = { "复位打印机", "标准ASCII字体", "压缩ASCII字体", "字体不放大", "宽高加倍", "取消加粗模式",
			"选择加粗模式", "取消倒置打印", "选择倒置打印", "取消黑白反显", "选择黑白反显", "取消顺时针旋转90°", "选择顺时针旋转90°" };
	final byte[][] byteCommands = { { 0x1b, 0x40 },// 复位打印机
			{ 0x1b, 0x4d, 0x00 },// 标准ASCII字体
			{ 0x1b, 0x4d, 0x01 },// 压缩ASCII字体
			{ 0x1d, 0x21, 0x00 },// 字体不放大
			{ 0x1d, 0x21, 0x11 },// 宽高加倍
			{ 0x1b, 0x45, 0x00 },// 取消加粗模式
			{ 0x1b, 0x45, 0x01 },// 选择加粗模式
			{ 0x1b, 0x7b, 0x00 },// 取消倒置打印
			{ 0x1b, 0x7b, 0x01 },// 选择倒置打印
			{ 0x1d, 0x42, 0x00 },// 取消黑白反显
			{ 0x1d, 0x42, 0x01 },// 选择黑白反显
			{ 0x1b, 0x56, 0x00 },// 取消顺时针旋转90°
			{ 0x1b, 0x56, 0x01 },// 选择顺时针旋转90°
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("蓝牙打印");
		this.setContentView(R.layout.printdata_layout);

		send = (Button) this.findViewById(R.id.send);
		command = (Button) this.findViewById(R.id.command);
		printPicture = (Button) this.findViewById(R.id.printPicture);

		printData = (EditText) this.findViewById(R.id.print_data);

		deviceName = (TextView) this.findViewById(R.id.device_name);
		connectState = (TextView) this.findViewById(R.id.connect_state);

		this.context = this;
		this.initPrintService();
		this.initListener();
	}

	// 获得从上一个Activity传来的蓝牙地址
	private void initPrintService() {
		// 直接通过Context类的getIntent()即可获取Intent
		Intent intent = this.getIntent();
		// 判断
		if (intent != null) {
			this.deviceAddress = intent.getStringExtra("deviceAddress");
		} else {
			throw new NullPointerException("缺少必须的参数");
		}

		this.printDataService = new PrintDataService(this.context, this.deviceAddress);
		// 设置当前设备名称
		this.deviceName.setText(this.printDataService.getDeviceName());
		// 一上来就先连接蓝牙设备
		boolean flag = this.printDataService.connect();
		if (flag == false) {
			// 连接失败
			this.connectState.setText("连接失败！");
		} else {
			// 连接成功
			this.connectState.setText("连接成功！");

		}
	}

	private void initListener() {

		send.setOnClickListener(mOnClickListener_send);
		command.setOnClickListener(mOnClickListener_1);
		printPicture.setOnClickListener(mOnClickListener_1);
	}

	OnClickListener mOnClickListener_send = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String sendData = printData.getText().toString();
			try {
				Waybill4PrintInfo bean = new Waybill4PrintInfo();
				bean.put(Waybill4PrintInfo.KEY_sender_name, "王大锤");
				bean.put(Waybill4PrintInfo.KEY_sender_phone, "15910622863");
				bean.put(Waybill4PrintInfo.KEY_sender_address, "北京市朝阳区#3好楼11011室");

				bean.put(Waybill4PrintInfo.KEY_receiver_name, "张三丰");
				bean.put(Waybill4PrintInfo.KEY_receiver_phone, "13901010101");
				bean.put(Waybill4PrintInfo.KEY_receiver_address, "山东西湖路小巷18号");

				bean.put(Waybill4PrintInfo.KEY_amount, "3");
				bean.put(Waybill4PrintInfo.KEY_weight, "100");
				bean.put(Waybill4PrintInfo.KEY_volume, "30");

				bean.put(Waybill4PrintInfo.KEY_way_songhuo, "√");
				bean.put(Waybill4PrintInfo.KEY_way_ziti, "");

				bean.put(Waybill4PrintInfo.KEY_pay_way_dao_fu, "");
				bean.put(Waybill4PrintInfo.KEY_pay_way_cash, "√");

				bean.put(Waybill4PrintInfo.KEY_goods_name, "太极剑");
				bean.put(Waybill4PrintInfo.KEY_bao_e, "1000");
				bean.put(Waybill4PrintInfo.KEY_bao_jia, "1000");
				bean.put(Waybill4PrintInfo.KEY_yun_fei, "99");

				bean.put(Waybill4PrintInfo.KEY_total_price, "199.9");

				WaybillPrintTemplate template = new WaybillPrintTemplate_zhongtie();
				PrintUtil.create(printDataService).setTemplate(template).setData(bean).print();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(PrintDataActivity.this, e.getMessage(), 0).show();
			}

		}
	};

	OnClickListener mOnClickListener_1 = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.command) {
				new AlertDialog.Builder(context).setTitle("请选择指令")
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (printDataService.isConnection()) {
									printDataService.send(byteCommands[which]);
									System.out.println(byteCommands[which].toString());
								} else {
									Toast.makeText(context, "设备未连接，请重新连接！", Toast.LENGTH_SHORT)
											.show();
								}
							}
						}).create().show();

			} else if (v.getId() == R.id.printPicture) {
				Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.aa);
				printDataService.printbmp(bmp);
			}

		}
	};

	@Override
	protected void onDestroy() {
		PrintDataService.disconnect();
		super.onDestroy();
	}

}