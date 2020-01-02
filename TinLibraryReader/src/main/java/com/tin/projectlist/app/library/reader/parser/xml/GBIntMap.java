package com.core.xml;

import com.core.common.util.ArrayUtils;

// optimized partially implemented map String -> String
// key must be interned
// there is no remove() in this implementation
// put with the same key does not remove old entry

/**
 * key value都必须是字符串的自定义map<br>
 *
 * 创建者： 符晨<br>
 * 创建日期：2013-4-2<br>
 */
public final class GBIntMap {
	private int[] myKeys;
	private int[] myValues;
	private int mySize;

	public GBIntMap() {
		myKeys = new int[8];
		myValues = new int[8];
	}

	public int[] getKeys() {
		return myKeys;
	}

	public int[] getVals() {
		return myValues;
	}

	public void put(int key, int value) {
		final int size = mySize++;
		int[] keys = myKeys;
		if (keys.length == size) {
			keys = ArrayUtils.createCopy(keys, size, size << 1);
			myKeys = keys;
			myValues = ArrayUtils.createCopy(myValues, size, size << 1);
		}
		keys[size] = key;
		myValues[size] = value;
	}

	/**
	 * 根据传入key获取值
	 *
	 * @param key
	 *            键（必须是int）
	 */
	public int getVal(int key) {
		int index = mySize;
		if (index > 0) {
			final int[] keys = myKeys;// optimized p
			while (--index >= 0) {
				if (keys[index] == key) {
					return myValues[index];
				}
			}
		}
		return -1;
	}

	/**
	 *
	 * 功能描述： 根据传入val获取key<br>
	 * 创建者： yangn<br>
	 * 创建日期：2013-6-18<br>
	 *
	 * @param
	 */
	public int getKeyword(int val) {
		int index = mySize;
		if (index > 0) {
			final int[] vals = myValues;// optimized p
			while (--index >= 0) {
				if (myValues[index] == val) {
					return myKeys[index];
				}
			}
		}
		return 0;
	}

	public int getSize() {
		return mySize;
	}

	public int getKey(int index) {
		return myKeys[index];
	}

	int getValue(int index) {
		return myValues[index];
	}

	public void clear() {
		mySize = 0;
	}

	boolean isEmpty() {
		return myValues.length == 0;
	}

	public void remove(int key) {
		if(getVal(key)==-1){
			return;
		}

		int index = mySize;
		if (index > 0) {
			final int[] keys = myKeys;// optimized p
			while (--index >= 0) {
				if (keys[index] == key) {
					keys[index] = 0;
					myValues[index] = -1;
				}
			}
		}
	}
}
