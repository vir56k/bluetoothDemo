/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 全峰快递的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_quanfeng extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(40, 28));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(80, 63));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(35, 45));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(135, 28));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(170, 63));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(125, 45));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(100, 70));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(130, 70));
		
		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(130, 80));
		
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(190, 83));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(190, 93));
		data.put(Waybill4PrintInfo.KEY_total_price, new Point(190, 103));
	}
}