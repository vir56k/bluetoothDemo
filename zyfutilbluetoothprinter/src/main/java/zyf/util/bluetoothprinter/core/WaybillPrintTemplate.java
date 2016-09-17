/*   
 * Copyright (c) 2014-2015 Zhong Ke Fu Chuang (Beijing) Technology Co., Ltd.  All Rights Reserved.   
 *    
 */

package zyf.util.bluetoothprinter.core;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Description: TODO 运单打印模板
 * @author
 * @date 2015-5-28 下午2:11:57
 * @version V1.0
 */
public abstract class WaybillPrintTemplate implements Serializable {
	private static final long serialVersionUID = -6091496540902660789L;
	protected HashMap<String, Point> data;

	public WaybillPrintTemplate() {
		super();
		data = new HashMap<String, Point>(16);
		onCreate(data);
	}

	public abstract void onCreate(HashMap<String, Point> data);

	public HashMap<String, Point> getData() {
		return data;
	}

	/**
	 * 
	 * @Description: TODO 设置上边距
	 * @param @return
	 * @return int
	 * @throws
	 */
	public int getMarginTop() {
		return 0;
	}

	public static class Point implements Serializable {

		private Point() {
			super();
		}

		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y - 5;
		}

		private static final long serialVersionUID = 6161091125031695270L;
		public int x;
		public int y;
	}
}