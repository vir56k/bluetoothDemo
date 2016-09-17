/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.templates;

import java.util.HashMap;

import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;

/**
 * @Description: TODO 中铁快运的运单打印模板
 * @author
 * @date 2015-5-28 下午2:12:25
 * @version V1.0
 */
public class WaybillPrintTemplate_zhongtie extends WaybillPrintTemplate {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void onCreate(HashMap<String, Point> data) {
		data.put(Waybill4PrintInfo.KEY_sender_name, new Point(40, 29));
		data.put(Waybill4PrintInfo.KEY_sender_phone, new Point(70, 29));
		data.put(Waybill4PrintInfo.KEY_sender_address, new Point(40, 44));

		data.put(Waybill4PrintInfo.KEY_receiver_name, new Point(40, 59));
		data.put(Waybill4PrintInfo.KEY_receiver_phone, new Point(70, 59));
		data.put(Waybill4PrintInfo.KEY_receiver_address, new Point(40, 75));

		data.put(Waybill4PrintInfo.KEY_amount, new Point(28, 95));
		data.put(Waybill4PrintInfo.KEY_weight, new Point(42, 95));
		data.put(Waybill4PrintInfo.KEY_volume, new Point(56, 95));

		data.put(Waybill4PrintInfo.KEY_way_songhuo, new Point(42, 106));
		data.put(Waybill4PrintInfo.KEY_way_ziti, new Point(57, 106));

		data.put(Waybill4PrintInfo.KEY_pay_way_dao_fu, new Point(117, 35));
		data.put(Waybill4PrintInfo.KEY_pay_way_cash, new Point(133, 34));

		data.put(Waybill4PrintInfo.KEY_goods_name, new Point(132, 50));
		data.put(Waybill4PrintInfo.KEY_bao_e, new Point(137, 62));
		data.put(Waybill4PrintInfo.KEY_bao_jia, new Point(137, 70));
		data.put(Waybill4PrintInfo.KEY_yun_fei, new Point(137, 80));

		data.put(Waybill4PrintInfo.KEY_total_price, new Point(120, 102));
	}
}