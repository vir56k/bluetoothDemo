/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 中国邮政的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_youzheng extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(35, 50));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(80, 50));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(45, 63));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(36, 76));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(80, 76));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(45, 90));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(170, 76));

		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(138, 70));

		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(135, 76));
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(175, 94));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(170, 82));

		data.put(Waybill4PrintInfo.KEY_total_price, new Point(170, 106));
	}
}