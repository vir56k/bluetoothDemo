/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 天天快递的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_tiantian extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(40, 31));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(35, 65));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(20, 55));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(135, 31));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(135, 65));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(115, 55));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(195, 73));

		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(168, 85));
		
		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(16, 85));
		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(122, 85));
		
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(45, 85));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(115, 91));
		data.put(Waybill4PrintInfo.KEY_total_price, new Point(185, 91));
	}
}