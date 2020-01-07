package com.tin.projectlist.app.library.reader.parser.xml;


import com.tin.projectlist.app.library.reader.parser.common.util.ArrayUtils;

/**
 * 可变长字符串<br>
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
final class GBMutableString {
	char[] myData;
	int myLength;

	GBMutableString(int len) {
		myData = new char[len];
	}

	GBMutableString() {
		this(20);
	}

	GBMutableString(GBMutableString container) {
		final int len = container.myLength;
		myData = ArrayUtils.createCopy(container.myData, len, len);
		myLength = len;
	}


	/**
	 * 向当前可变长度字符串对象加入新的字符
	 * @param buffer 要加入的数据源
	 * @param offset 开始下标
	 * @param count  数据长度
	 */
	public void append(char[] buffer, int offset, int count) {
		final int len = myLength;
		char[] data = myData;
		final int newLength = len + count;
		if (data.length < newLength) {
			data = ArrayUtils.createCopy(data, len, newLength);
			myData = data;
		}
		System.arraycopy(buffer, offset, data, len, count);
		myLength = newLength;
	}

	/**
	 *  清空数据
	 */
	public void clear() {
		myLength = 0;
	}

	/**
	 * 比较两个可变长字符串对象是否相同
	 * 默认认为工程中不会拿其他类型的对象进来对比，否则将出现类型转换异常  （极简主义？）
	 */
	public boolean equals(Object o) {
		final GBMutableString container = (GBMutableString)o;
		final int len = myLength;
		if (len != container.myLength) {
			return false;
		}
		final char[] data0 = myData;
		final char[] data1 = container.myData;
		for (int i = len; --i >= 0; ) {
			if (data0[i] != data1[i]) {
				return false;
			}
		}
		return true;
	}

	public int hashCode() {
		final int len = myLength;
		final char[] data = myData;
		int code = len * 31;
		if (len > 1) {
			code += data[0];
			code *= 31;
			code += data[1];
			if (len > 2) {
				code *= 31;
				code += data[2];
			}
		} else if (len > 0) {
			code += data[0];
		}
		return code;
	}

	public String toString() {
		return new String(myData, 0, myLength).intern();
	}
}
