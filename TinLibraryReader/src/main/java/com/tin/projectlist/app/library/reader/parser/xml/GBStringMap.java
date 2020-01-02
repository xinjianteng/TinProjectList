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
public final class GBStringMap {
    private String[] myKeys;
    private String[] myValues;
    private int mySize;

    public GBStringMap() {
        myKeys = new String[8];
        myValues = new String[8];
    }

    public String[] getKeys() {
        return myKeys;
    }

    public String[] getVals() {
        return myValues;
    }

    public void put(String key, String value) {
        final int size = mySize++;
        String[] keys = myKeys;
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
     * @param key 键（必须是String）
     */
    public String getValue(String key) {
        int index = mySize;
        if (index > 0) {
            final String[] keys = myKeys;// optimized p
            while (--index >= 0) {
                if (keys[index].equals(key)) {
                    return myValues[index];
                }
            }
        }
        return null;
    }

    /**
     *
     * 功能描述： 根据传入val获取key<br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-18<br>
     *
     * @param
     */
    public String getKey(String val) {
        int index = mySize;
        if (index > 0) {
            final String[] vals = myValues;// optimized p
            while (--index >= 0) {
                if (myValues[index] == val) {
                    return myKeys[index];
                }
            }
        }
        return null;
    }

    public int getSize() {
        return mySize;
    }

    public String getKey(int index) {
        return myKeys[index];
    }

    String getValue(int index) {
        return myValues[index];
    }

    public void clear() {
        mySize = 0;
    }
}
