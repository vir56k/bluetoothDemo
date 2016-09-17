/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.core;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Description: TODO 运单 ，for 打印用的，描述信息
 * @author
 * @date 2015-5-28 下午2:11:13
 * @version V1.0
 */
public class Waybill4PrintInfo implements Serializable {
	private static final long serialVersionUID = 3406805761985154572L;

	/**
	 * 发件人姓名
	 */
	public final static String KEY_sender_name = "KEY_sender_name";
	// public final static String KEY_sender_compony;
	/**
	 * 发件人电话
	 */
	public final static String KEY_sender_phone = "KEY_sender_phone";
	/**
	 * 发件人地址
	 */
	public final static String KEY_sender_address = "KEY_sender_address";
	/**
	 * 收件人姓名
	 */
	public final static String KEY_receiver_name = "KEY_receiver_name";
	// public final static String KEY_receiver_compony;
	/**
	 * 收件人电话
	 */
	public final static String KEY_receiver_phone = "KEY_receiver_phone";
	/**
	 * 收件人地址
	 */
	public final static String KEY_receiver_address = "KEY_receiver_address";
	/**
	 * 件数
	 */
	public static final String KEY_amount = "KEY_amount";
	/**
	 * 重量
	 */
	public static final String KEY_weight = "KEY_weight";
	/**
	 * 体积
	 */
	public static final String KEY_volume = "KEY_volume";
	/**
	 * 交付方式，送货
	 */
	public static final String KEY_way_songhuo = "KEY_way_songhuo";
	/**
	 * 交付方式，自提
	 */
	public static final String KEY_way_ziti = "KEY_way_ziti";
	/**
	 * 支付方式，到付
	 */
	public static final String KEY_pay_way_dao_fu = "KEY_pay_way_dao_fu";
	/**
	 * 支付方式，现金
	 */
	public static final String KEY_pay_way_cash = "KEY_pay_way_cash";
	/**
	 * 物品名称
	 */
	public static final String KEY_goods_name = "KEY_goods_name";
	/**
	 * 是否报价
	 */
	public static final String KEY_is_bao_jia = "KEY_is_bao_jia";
	/**
	 * 保价声明价值
	 */
	public static final String KEY_bao_e = "KEY_bao_e";
	/**
	 * 保价费
	 */
	public static final String KEY_bao_jia = "KEY_bao_jia";
	/**
	 * 运费
	 */
	public static final String KEY_yun_fei = "KEY_yun_fei";
	/**
	 * 费用合计
	 */
	public static final String KEY_total_price = "KEY_total_price";

	private HashMap<String, String> data;

	public Waybill4PrintInfo() {
		super();
		data = new HashMap<String, String>(16);
	}

	public void put(String key, String value) {
		data.put(key, value);
	}

	public String get(String key) {
		return data.get(key);
	}

}