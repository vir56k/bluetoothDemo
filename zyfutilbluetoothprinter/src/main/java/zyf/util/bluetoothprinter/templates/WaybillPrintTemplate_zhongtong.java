/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate.Point;

/**
 * 中通快递的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_zhongtong extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(35, 32));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(35, 39));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(35, 62));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(130, 32));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(130, 39));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(130, 62));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(158, 78));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(164, 75));

		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(77, 80));
		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(30, 77));
		
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(150, 87));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(170, 87));
		data.put(Waybill4PrintInfo.KEY_total_price, new Point(187, 87));
	}
}