package com.jerry.bluetoothprinter.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

public class PrintDataService {
	private Context context = null;
	private String deviceAddress = null;
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice device = null;
	private static BluetoothSocket bluetoothSocket = null;
	private static OutputStream outputStream = null;
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final String TAG = "PrintDataService";
	private boolean isConnection = false;

	public PrintDataService(Context context, String deviceAddress) {
		super();
		this.context = context;
		this.deviceAddress = deviceAddress;
		this.device = this.bluetoothAdapter.getRemoteDevice(this.deviceAddress);
	}

	public boolean isConnection() {
		return isConnection;
	}

	/**
	 * 获取设备名称
	 * 
	 * @return String
	 */
	public String getDeviceName() {
		return this.device.getName();
	}

	/**
	 * 连接蓝牙设备
	 */
	public boolean connect() {
		if (!this.isConnection) {
			try {
				bluetoothSocket = this.device.createRfcommSocketToServiceRecord(uuid);
				bluetoothSocket.connect();
				outputStream = bluetoothSocket.getOutputStream();
				this.isConnection = true;
				if (this.bluetoothAdapter.isDiscovering()) {
					System.out.println("关闭适配器！");
					this.bluetoothAdapter.isDiscovering();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		} else {
			return true;
		}
	}

	/**
	 * 断开蓝牙设备连接
	 */
	public static void disconnect() {
		System.out.println("断开蓝牙设备连接");
		try {
			if (bluetoothSocket != null)
				bluetoothSocket.close();
			if (outputStream != null)
				outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// /**
	// * 发送数据
	// */
	// public void send(String sendData) {
	// if (this.isConnection) {
	// System.out.println("开始打印！！");
	// try {
	// byte[] data = sendData.getBytes("GBK");
	// // byte[] data1 = {0x0c};
	// byte[] quitData = new byte[1];
	// quitData[0] = (byte) 12;
	// outputStream.write(data, 0, data.length);
	// outputStream.write(quitData, 0, quitData.length);
	// outputStream.flush();
	// } catch (IOException e) {
	// Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT).show();
	// }
	// } else {
	// Toast.makeText(this.context, "设备未连接，请重新连接！", Toast.LENGTH_SHORT).show();
	// }
	// }

	/**
	 * 发送数据
	 */
	public void send(byte[] data) {
		if (this.isConnection) {
			try {
				outputStream.write(data, 0, data.length);
			} catch (IOException e) {
				Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this.context, "设备未连接，请重新连接！", Toast.LENGTH_SHORT).show();
		}
	}

	public void move_vertical_relative(int y_mm) {
		log(String.format("移动_垂直方向_相对位置: %s 毫米", y_mm));
		int y = parseToY(y_mm);
		// 因为偏移量描述时1个字节，不能大于256，所有如果比256大，则需要发送多次
		int total = y;
		int tmp = 0;
		while (total > 0) {
			tmp = (total >= 255 ? 255 : total);
			send(new byte[] { 0x1b, 0x4a, (byte) tmp });
			total -= tmp;
		}

	}

	private void log(String format) {
		Log.i(TAG, format);
	}

	public static int parseToY(int y_mm) {
		int t = (int) Math.round((float) y_mm / 10 / 2.54 * 180); // 小数点后两位前移，并四舍五入
		return t;
	}

	public void move_horizontal_abslute(int x_mm) {
		log(String.format("移动_水平方向_绝对位置： %s 毫米", x_mm));
		int x = (int) Math.round((double) x_mm / 25.4 * 180 / 3);
		// 指定x轴 ，打印位置从左边界向右移动 (n2*256+n1)/60英寸
		byte[] x_byte = parseToByte_X(x);
		byte[] cmd_move_x = new byte[] { 0x1b, 0x24, x_byte[0], x_byte[1] };
		send(cmd_move_x);
	}

	/**
	 * 
	 * @Description: TODO 发送数据
	 * @param @param x 毫米
	 * @param @param y 毫米
	 * @param @param sendData
	 * @return void
	 * @throws
	 */
	public void send(String sendData) {
		log(String.format("发送字符串：%s", sendData));
		byte[] data;
		try {
			data = sendData.getBytes("GBK");
			send(data);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void sendComplete() {
		log("发送结束");
		if (this.isConnection) {
			try {
				byte[] quitData = new byte[1];
				quitData[0] = (byte) 12;
				outputStream.write(quitData, 0, quitData.length);
				outputStream.flush();
			} catch (IOException e) {
				Toast.makeText(this.context, "发送失败！", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this.context, "设备未连接，请重新连接！", Toast.LENGTH_SHORT).show();
		}
	}

	// 转换成 双字节的描述 x 的格式 n1,n2 , 算法 x= n2*255+n1;
	private byte[] parseToByte_X(int x) {
		int n1 = x % 256;
		int n2 = x / 256;
		return new byte[] { (byte) n1, (byte) n2 };
	}

	/**
	 * 解析图片
	 */
	private byte[] readbmp(Bitmap bitmap, int startline) {
		int nWidth = bitmap.getWidth();
		int nHeight = bitmap.getHeight();
		int nRealLines;
		int nSize;
		int nLines;
		int endLine;
		byte[] bData;
		if (startline >= nHeight) {
			return null;
		}
		nRealLines = nHeight - startline;
		if (nRealLines > 24) {
			nRealLines = 24;
		}
		if (nRealLines % 8 == 0) {
			nLines = nRealLines / 8;
		} else {
			nLines = nRealLines / 8 + 1;
		}
		nSize = nWidth * 3 + 6;
		bData = new byte[nSize];
		for (int i = 0; i < nSize; i++) {
			bData[i] = (byte) 0;
		}
		bData[0] = 0x1B;
		bData[1] = 0x2A;
		bData[2] = (byte) 40;
		bData[3] = (byte) (nWidth % 256);
		bData[4] = (byte) (nWidth / 256);

		int nTempValue;
		int nTempData;
		int nPixColor;
		int nDataIndex;
		int nBitIndex;

		for (int i = 0; i < nLines; i++) {
			if (nRealLines - 8 * (i + 1) > 0) {
				endLine = startline + 8 * (i + 1);
			} else {
				endLine = startline + nRealLines;
			}
			nDataIndex = 5 + i;
			for (int w = 0; w < nWidth; w++) {
				nBitIndex = 0;
				for (int h = startline + i * 8; h < endLine; h++) {
					nPixColor = bitmap.getPixel(w, h);
					nPixColor = nPixColor & 0x00111111;
					nBitIndex++;
					if (nPixColor == 0) {
						nTempValue = 1;
						nTempValue = nTempValue << (8 - nBitIndex);
						nTempData = bData[nDataIndex];
						nTempData = nTempData | nTempValue;
						bData[nDataIndex] = (byte) nTempData;
					}
				}
				nDataIndex = nDataIndex + 3;
			}
		}
		// 换行
		bData[nSize - 1] = 0x0a;

		return bData;
	}

	/**
	 * 打印图片
	 */
	public void printbmp(Bitmap bitmap) {
		if (this.isConnection) {
			System.out.println("开始打印！！");
			if (bitmap != null) {
				// 设置行间距
				byte lsData[] = new byte[3];
				lsData[0] = 0x1B;
				lsData[1] = 0x30;
				// lsData[2] = 0x05;
				try {
					outputStream.write(lsData, 0, lsData.length);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// 打印结束退纸
				byte[] quitData = new byte[1];
				quitData[0] = (byte) 12;

				int nStartLine = 0;
				int nHeight = bitmap.getHeight();

				while (nStartLine < nHeight) {
					byte bmpData[];
					bmpData = readbmp(bitmap, nStartLine);
					if (bmpData == null) {
						break;
					} else {
						try {
							outputStream.write(bmpData, 0, bmpData.length);
							outputStream.flush();
							nStartLine = nStartLine + 24;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				try {

					outputStream.write(quitData, 0, quitData.length);
					outputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

}