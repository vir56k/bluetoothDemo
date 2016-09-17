/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 百世汇通的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_chengji extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(35, 40));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(75, 40));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(20, 61));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(130, 40));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(175, 40));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(110, 61));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(120, 78));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(170, 103));
		
		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(94, 103));
		
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(165, 85));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(120, 85));
		data.put(Waybill4PrintInfo.KEY_total_price, new Point(125, 90));
	}
}