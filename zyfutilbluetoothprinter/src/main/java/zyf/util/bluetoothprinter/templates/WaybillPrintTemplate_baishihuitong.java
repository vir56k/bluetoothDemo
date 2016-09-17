/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 城际速递的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_baishihuitong extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(35, 28));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(35, 61));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(20, 48));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(130, 28));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(130, 61));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(115, 48));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(122, 75));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(143, 68));
		
		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(30, 84));
		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(90, 88));
		
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(198, 81));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(125, 81));
		data.put(Waybill4PrintInfo.KEY_total_price, new Point(180, 85));
	}
}