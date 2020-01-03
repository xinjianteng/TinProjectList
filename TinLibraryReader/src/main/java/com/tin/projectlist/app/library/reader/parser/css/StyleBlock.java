package com.tin.projectlist.app.library.reader.parser.css;

import com.core.xml.GBStringMap;

/**
 *
 * 类名： .java<br>
 * 描述： 样式项 创建者： yangn<br>
 * 创建日期：2013-5-15<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class StyleBlock {
	// enum
	public static final byte TAG = 0;
	public static final byte CLASS = 46;
	public static final byte ID = 35;

	// properties

	public String Name;
	public GBStringMap Info = new GBStringMap();

	// method

	public byte getType() {
		if (null == Name||"".equals(Name)) {
			return -1;
		}

		byte current = (byte) Name.charAt(0);
		if (current == ID || current == CLASS) {
			return current;
		} else {
			return 0;
		}
	}
}