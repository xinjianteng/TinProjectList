package com.core.object;

import com.core.common.util.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
/**
 * 类名： GBByteBuffer.java#ZLByteBuffer<br>
 * 描述： 自定义可扩展字节数组对象<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-8<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBByteBuffer {
	byte[] mData; //当前数据字节数组
	int mLength;  //当前数据长度

	GBByteBuffer(int len) {
		mData = new byte[len];
	}

	public GBByteBuffer() {
		this(20);
	}

	public GBByteBuffer(String value) {
		mLength = value.length();
		mData = value.getBytes();
	}

	public byte[] getmData() {
		return mData;
	}
	public int size(){
		return mLength;
	}

	public GBByteBuffer(GBByteBuffer container) {
		final int len = container.mLength;
		mData = ArrayUtils.createCopy(container.mData, len, len);
		mLength = len;
	}

	public boolean isEmpty() {
		return mLength == 0;
	}
	/**
	 * 功能描述： 追加指定字节长度数据<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-8<br>
	 * @param buffer 要追加的数组
	 * @param offset 追加的开始
	 * @param count 追加的长度
	 */
	public void append(byte[] buffer, int offset, int count) {
		final int len = mLength;
		byte[] data = mData;
		final int newLength = len + count;
		if (data.length < newLength) {
			data = ArrayUtils.createCopy(data, len, newLength);
			mData = data;
		}
		System.arraycopy(buffer, offset, data, len, count);
		mLength = newLength;
		mStringValue = null;
	}

	public void clear() {
		mLength = 0;
		mStringValue = null;
	}

	public boolean equals(Object o) {
		final GBByteBuffer container = (GBByteBuffer)o;
		final int len = mLength;
		if (len != container.mLength) {
			return false;
		}
		final byte[] data0 = mData;
		final byte[] data1 = container.mData;
		for (int i = len; --i >= 0; ) {
			if (data0[i] != data1[i]) {
				return false;
			}
		}
		return true;
	}

	public int hashCode() {
		final int len = mLength;
		final byte[] data = mData;
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
	/**
	 * 功能描述： 和全小写字符串比较<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-8<br>
	 * @param lcString 小写字符串
	 * @return
	 */
	public boolean equalsToLCString(String lcString) {
		return (mLength == lcString.length()) &&
				lcString.equals(new String(mData, 0, mLength).toLowerCase());
	}

	private static final Object myConverterLock = new Object();
	private static char[] myConverterBuffer = new char[20];
	private String mStringValue;
	/**
	 * 功能描述： 制定字符解码器的获取字符串方法<br>
	 * 创建者： jack<br>
	 * 创建日期：2013-4-8<br>
	 * @param decoder 字符解码器
	 * @return
	 */
	public String toString(CharsetDecoder decoder) {
		if (mStringValue == null) {
			synchronized (myConverterLock) {
				if (myConverterBuffer.length < mLength) {
					myConverterBuffer = new char[mLength];
				}
				ByteBuffer byteBuffer = ByteBuffer.wrap(mData, 0, mLength);
				CharBuffer charBuffer = CharBuffer.wrap(myConverterBuffer);
				decoder.decode(byteBuffer, charBuffer, true);
				mStringValue = new String(myConverterBuffer, 0, charBuffer.position());
			}
		}
		return mStringValue;
	}

	public String toString() {
		if (mStringValue == null) {
			mStringValue = new String(mData, 0, mLength);
		}
		return mStringValue;
	}
}
