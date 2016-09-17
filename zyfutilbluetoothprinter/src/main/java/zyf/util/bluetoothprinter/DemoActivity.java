/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter;

import zyf.util.bluetoothprinter.core.TemplateFactory;
import zyf.util.bluetoothprinter.core.Waybill4PrintInfo;
import zyf.util.bluetoothprinter.core.WaybillPrintTemplate;
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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @Description: TODO
 * @author
 * @date 2015-5-30 上午11:20:56
 * @version V1.0
 */
public class DemoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_activity);
	}

	public void onClick(View v) {
		Waybill4PrintInfo bean = null;
		WaybillPrintTemplate template = null;

		bean = getDataBean2();
		template = TemplateFactory.getTemplate(TemplateFactory.KEY_TEMPLATE_ID_youzheng);

		Intent intent1;
		intent1 = new Intent(this, MyPrinterActivity.class);
		intent1.putExtra("bean", bean);
		intent1.putExtra("template", template);
		startActivity(intent1);
	}

	/**
	 * 
	 * @Description: TODO 创建实体，描述了快递单的内容
	 * @param @return
	 * @return Waybill4PrintInfo
	 * @throws
	 */
	public static Waybill4PrintInfo getDataBean() {
		Waybill4PrintInfo bean = new Waybill4PrintInfo();
		bean.put(Waybill4PrintInfo.KEY_sender_name, "王大锤");
		bean.put(Waybill4PrintInfo.KEY_sender_phone, "15910622863");
		bean.put(Waybill4PrintInfo.KEY_sender_address, "北京市朝阳区#3好楼11011室");

		bean.put(Waybill4PrintInfo.KEY_receiver_name, "张三丰");
		bean.put(Waybill4PrintInfo.KEY_receiver_phone, "13901010101");
		bean.put(Waybill4PrintInfo.KEY_receiver_address, "山东西湖路小巷18号");

		bean.put(Waybill4PrintInfo.KEY_amount, "3");
		bean.put(Waybill4PrintInfo.KEY_weight, "100");
		bean.put(Waybill4PrintInfo.KEY_volume, "30");

		bean.put(Waybill4PrintInfo.KEY_way_songhuo, "√");
		bean.put(Waybill4PrintInfo.KEY_way_ziti, "");

		bean.put(Waybill4PrintInfo.KEY_pay_way_dao_fu, "");
		bean.put(Waybill4PrintInfo.KEY_pay_way_cash, "√");
		bean.put(Waybill4PrintInfo.KEY_is_bao_jia, "√");

		bean.put(Waybill4PrintInfo.KEY_goods_name, "太极剑");
		bean.put(Waybill4PrintInfo.KEY_bao_e, "1000");
		bean.put(Waybill4PrintInfo.KEY_bao_jia, "1000");
		bean.put(Waybill4PrintInfo.KEY_yun_fei, "99");

		bean.put(Waybill4PrintInfo.KEY_total_price, "199.9");
		return bean;
	}

	public static Waybill4PrintInfo getDataBean2() {
		Waybill4PrintInfo bean = new Waybill4PrintInfo();
		bean.put(Waybill4PrintInfo.KEY_sender_name, "王大锤");
		bean.put(Waybill4PrintInfo.KEY_sender_phone, "15910622863");
		bean.put(Waybill4PrintInfo.KEY_sender_address, "北京市朝阳区#3好楼11011室");

		bean.put(Waybill4PrintInfo.KEY_receiver_name, "张三丰");
		bean.put(Waybill4PrintInfo.KEY_receiver_phone, "13901010101");
		bean.put(Waybill4PrintInfo.KEY_receiver_address, "山东西湖路小巷18号");

		bean.put(Waybill4PrintInfo.KEY_weight, "100");

		bean.put(Waybill4PrintInfo.KEY_pay_way_cash, "√");
		bean.put(Waybill4PrintInfo.KEY_is_bao_jia, "√");

		bean.put(Waybill4PrintInfo.KEY_bao_e, "1000");
		bean.put(Waybill4PrintInfo.KEY_bao_jia, "1000");
		bean.put(Waybill4PrintInfo.KEY_yun_fei, "99");

		bean.put(Waybill4PrintInfo.KEY_total_price, "199.9");
		return bean;
	}
}
