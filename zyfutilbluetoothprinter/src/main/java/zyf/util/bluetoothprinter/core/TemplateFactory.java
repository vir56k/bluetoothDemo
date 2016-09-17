/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.core;

import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_baishihuitong;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_chengji;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_quanfeng;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_shentong;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_shunfeng;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_tiantian;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_youzheng;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_yuantong;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_yunda;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_zhongtie;
import zyf.util.bluetoothprinter.templates.WaybillPrintTemplate_zhongtong;

/**
 * @Description: TODO
 * @author
 * @date 2015-6-1 下午8:22:29
 * @version V1.0
 */
public class TemplateFactory {
	public static final int KEY_TEMPLATE_ID_zhongtie = 1;
	public static final int KEY_TEMPLATE_ID_zhongtong = 2;
	public static final int KEY_TEMPLATE_ID_yunda = 3;
	public static final int KEY_TEMPLATE_ID_yuantong = 4;
	public static final int KEY_TEMPLATE_ID_youzheng = 5;
	public static final int KEY_TEMPLATE_ID_shunfeng = 6;
	public static final int KEY_TEMPLATE_ID_shentong = 7;
	public static final int KEY_TEMPLATE_ID_quanfeng = 8;
	public static final int KEY_TEMPLATE_ID_baishihuitong = 9;
	public static final int KEY_TEMPLATE_ID_chengji = 10;
	public static final int KEY_TEMPLATE_ID_tiantian = 11;

	private TemplateFactory() {
		super();
	}

	/**
	 * 
	 * @Description: TODO 根据id 返回 打印模板
	 * @param @param template_id
	 * @param @return
	 * @return WaybillPrintTemplate
	 * @throws
	 */
	public static WaybillPrintTemplate getTemplate(int template_id) {

		WaybillPrintTemplate tmp = null;
		switch (template_id) {
		case KEY_TEMPLATE_ID_zhongtie:
			tmp = new WaybillPrintTemplate_zhongtie();
			break;
		case KEY_TEMPLATE_ID_zhongtong:
			tmp = new WaybillPrintTemplate_zhongtong();
			break;
		case KEY_TEMPLATE_ID_yunda:
			tmp = new WaybillPrintTemplate_yunda();
			break;
		case KEY_TEMPLATE_ID_yuantong:
			tmp = new WaybillPrintTemplate_yuantong();
			break;
		case KEY_TEMPLATE_ID_youzheng:
			tmp = new WaybillPrintTemplate_youzheng();
			break;
		case KEY_TEMPLATE_ID_shunfeng:
			tmp = new WaybillPrintTemplate_shunfeng();
			break;
		case KEY_TEMPLATE_ID_shentong:
			tmp = new WaybillPrintTemplate_shentong();
			break;
		case KEY_TEMPLATE_ID_quanfeng:
			tmp = new WaybillPrintTemplate_quanfeng();
			break;
		case KEY_TEMPLATE_ID_baishihuitong:
			tmp = new WaybillPrintTemplate_baishihuitong();
			break;
		case KEY_TEMPLATE_ID_chengji:
			tmp = new WaybillPrintTemplate_chengji();
			break;
		case KEY_TEMPLATE_ID_tiantian:
			tmp = new WaybillPrintTemplate_tiantian();
			break;
		}
		return tmp;
	}

}
