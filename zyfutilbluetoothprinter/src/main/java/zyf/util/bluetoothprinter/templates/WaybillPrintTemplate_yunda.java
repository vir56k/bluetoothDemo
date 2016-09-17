/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 韵达快递的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_yunda extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(35, 27));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(35, 42));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(67, 57));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(125, 27));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(125, 42));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(162, 57));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(115, 64));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(184, 69));
		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(127, 82));

		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(165, 73));
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(130, 73));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(130, 69));

		data.put(Waybill4PrintInfo.KEY_total_price, new Point(115, 72));
	}
}