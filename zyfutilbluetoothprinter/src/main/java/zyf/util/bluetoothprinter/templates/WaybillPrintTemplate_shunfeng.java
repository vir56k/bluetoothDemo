/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * 顺丰速运的运单打印模板
 * 
 * @Description:
 * @author
 * @date 2015-6-1 上午10:44:00
 * 
 */
public class WaybillPrintTemplate_shunfeng extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(80, 38));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(45, 60));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(25, 46));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(80, 78));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(45, 100));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(25, 85));

		data.put(Waybill4PrintInfo.KEY_weight, new Point(112, 133));

		data.put(Waybill4PrintInfo.KEY_is_bao_jia, new Point(99, 107));
		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(115, 107));
		
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(140, 107));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(138, 126));
		data.put(Waybill4PrintInfo.KEY_total_price, new Point(138, 133));
	}
}