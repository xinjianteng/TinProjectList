
package com.core.html;

import com.core.object.GBByteBuffer;

import java.nio.charset.CharsetDecoder;

/**
 * 类名： GBHtmlAttrMap.java#ZLHtmlAttributeMap<br>
 * 描述： 指定字节数组作为key-value存储的自定义map集合<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-8<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBHtmlAttrMap {
	private GBByteBuffer[] mKeys;
	private GBByteBuffer[] mValues;
	private int mSize;  //当前集合长度

	public GBHtmlAttrMap() {
		mKeys = new GBByteBuffer[8];
		mValues = new GBByteBuffer[8];
	}

	public void put(GBByteBuffer key, GBByteBuffer value) {
		final int size = mSize++;
		GBByteBuffer[] keys = mKeys;
		if (keys.length == size) {
			keys = new GBByteBuffer[size << 1];
			System.arraycopy(mKeys, 0, keys, 0, size);
			mKeys = keys;

			final GBByteBuffer[] values = new GBByteBuffer[size << 1];
			System.arraycopy(mValues, 0, values, 0, size);
			mValues = values;
		}
		keys[size] = key;
		mValues[size] = value;
	}
	/**
	 * 功能描述： 根据（全小写字符串）key获取value字符数组<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-8<br>
	 * @param lcString 小写字符串
	 * @return
	 */
	public GBByteBuffer getValue(String lcString) {
		int index = mSize;
		if (index > 0) {
			final GBByteBuffer[] keys = mKeys;
			while (--index >= 0) {
				if (keys[index].equalsToLCString(lcString)) {
					return mValues[index];
				}
			}
		}
		return null;
	}
	/**
	 * 功能描述： 根据（全小写字符串）key获取指定解码器的value字符串<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-8<br>
	 * @param lcString 小写字符串
	 * @param decoder 解码器
	 * @return
	 */
	public String getStringValue(String lcString, CharsetDecoder decoder) {
		final GBByteBuffer buffer = getValue(lcString);
		return (buffer != null) ? buffer.toString(decoder) : null;
	}

	public int size() {
		return mSize;
	}

	public GBByteBuffer getKey(int index) {
		return mKeys[index];
	}

	public void clear() {
		mSize = 0;
	}
}
