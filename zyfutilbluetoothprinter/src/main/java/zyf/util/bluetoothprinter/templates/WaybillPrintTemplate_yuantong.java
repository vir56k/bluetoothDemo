/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 圆通快递的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_yuantong extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(35, 27));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(20, 45));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(45, 57));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(130, 27));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(115, 45));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(140, 57));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(95, 68));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(156, 86));
		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(121, 70));

		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(198, 68));
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(163, 72));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(120, 82));

		data.put(Waybill4PrintInfo.KEY_total_price, new Point(175, 82));
	}
}