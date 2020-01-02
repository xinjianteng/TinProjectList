package com.core.text.model.style;

import com.core.common.util.ArrayUtils;
import com.core.log.L;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * 类名： .java<br>
 * 描述： 样式代理类 描述一组属性（同一个标签生效的类样式、ID样式、内嵌样式为一组） 处理一组内的样式冲突 如：<tag color="red"
 * style="color:red;"/>只返回一个有效的颜色。 提供样式实体与序列化char[]之间相互转换 和提供样式管理、获取方法 创建者：
 * yangn<br>
 * 创建日期：2013-5-21<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextStyleEntryProxy {
    // 是否为段落内部样式
    public boolean mIsInnerCss = false;

    public GBTextStyleEntryProxy() {

    }

    public GBTextStyleEntryProxy(GBTextStyleEntry entry) {
        this.put(entry);
    }

    public GBTextStyleEntryProxy(char[] data) throws IllegalArgumentException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        this.mData = data;
        mOffset = data.length;
        toEntrys();
    }

    public GBTextStyleEntryProxy(char[] data, int offset, int len) throws IllegalArgumentException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        this.mData = new char[len];
        for (int i = 0; i < len; i++) {
            mData[i] = data[offset + i];
        }
        mOffset = mData.length;
        toEntrys();
    }

    private ArrayList<GBTextStyleEntry> mEntrys = new ArrayList<GBTextStyleEntry>();
    private char[] mData = new char[128];
    private int mOffset = 0;
    // private static ArrayList<Class> entrySupportList = new
    // ArrayList<Class>();
    private static HashMap<Byte, Class<? extends GBTextStyleEntry>> mTypeMap = new HashMap<Byte, Class<? extends GBTextStyleEntry>>();
    private static byte mKind = 1;

    public int size() {
        return mOffset;
    }

    /**
     *
     * 功能描述： StyleEntry转char[] 创建者： yangn<br>
     * 创建日期：2013-5-20<br>
     *
     * @param
     */
    public char[] toChars() {
        char[] data = null;
        byte kind = -1;
        int offset = 0;
        GBTextStyleEntry entry = null;
        for (int i = 0; i < mEntrys.size(); i++) {
            entry = mEntrys.get(i);
            if (entry == null)
                continue;
            kind = getKey(entry.getClass());
            data = entry.toChars();
            if (data.length >= (mData.length - offset)) {
                // int minimumLength=mData.length+(data.length -
                // (mData.length-offset));
                mData = ArrayUtils.createCopy(mData, mData.length, mData.length + data.length);
            }

            mData[offset++] = (char) kind;
            mData[offset++] = (char) data.length;
            System.arraycopy(data, 0, mData, offset, data.length);
            offset += data.length;
            mOffset = offset;
        }

        char[] realData = new char[mOffset];
        System.arraycopy(mData, 0, realData, 0, mOffset);
        mData = realData;
        return mData;
    }

    /**
     *
     * 功能描述：添加entry 创建者： yangn<br>
     * 创建日期：2013-5-20<br>
     *
     * @param
     */
    public void put(GBTextStyleEntry textStyleEntry) {
        mEntrys.add(textStyleEntry);
    }

    /**
     *
     * 功能描述： 添加entry列表<br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-19<br>
     *
     * @param
     */
    public void putAll(ArrayList<GBTextStyleEntry> textStyleEntrys) {
        mEntrys.addAll(textStyleEntrys);
    }

    /**
     *
     * 功能描述：char[]转换为StyleEntryList 创建者： yangn<br>
     * 创建日期：2013-5-20<br>
     *
     * @param
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     */
    public ArrayList<GBTextStyleEntry> toEntrys() throws NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        mEntrys.clear();
        GBTextStyleEntry entry = null;
        int offset = 0, len = mOffset, entryLen = 0;

        while (offset < len) {

            if (mTypeMap.containsKey((byte) mData[offset])) {
                Class cls = mTypeMap.get((byte) mData[offset++]);
                entryLen = mData[offset++];
                entry = (GBTextStyleEntry) cls.newInstance();
                if (null != entry) {
                    mEntrys.add(entry);
                }

                if (mData.length < entryLen) {
                    L.e("GBTextStyleEntryProxy", "mdata.leng=" + mData.length + "offset=" + offset + "entryLen"
                            + entryLen);
                }

                entry.loadData(mData, offset, entryLen);
                /*
                 * Method method = cls.getMethod("loadData".intern(),
                 * char[].class, int.class, int.class); method.invoke(entry,
                 * mData, offset, entryLen);
                 */

                offset += entryLen;
            } else {
                offset++;
            }
        }

        return mEntrys;
    }

    /**
     *
     * 功能描述： 判断是否包含classType类型的Entry实体 创建者： yangn<br>
     * 创建日期：2013-5-20<br>
     *
     * @param
     */
    public boolean has(Class<? extends GBTextStyleEntry> classType) {
        boolean flag = false;
        GBTextStyleEntry entry = null;
        for (int i = 0; i < mEntrys.size(); i++) {
            entry = mEntrys.get(i);
            if (entry == null)
                continue;
            if (entry.getClass() == classType) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     *
     * 功能描述：根据val获取对应key 创建者： yangn<br>
     * 创建日期：2013-5-21<br>
     *
     * @param
     */
    private byte getKey(Class<? extends GBTextStyleEntry> classType) {
        byte ret = -1;
        if (mTypeMap.isEmpty()) {
            return ret;
        }
        Map.Entry<Byte, Class<? extends GBTextStyleEntry>> entry = null;
        for (Iterator<Map.Entry<Byte, Class<? extends GBTextStyleEntry>>> iterator = mTypeMap.entrySet().iterator(); iterator
                .hasNext();) {
            entry = iterator.next();
            if (entry.getValue() == classType) {
                ret = entry.getKey();
                break;
            }
        }

        return ret;
    }

    /**
     *
     * 功能描述：按Entry类型获取Entry 创建者： yangn<br>
     * 创建日期：2013-5-20<br>
     *
     * @param
     */
    public <T extends GBTextStyleEntry> T getEntry(Class<? extends GBTextStyleEntry> classType) {
        GBTextStyleEntry entry = null;
        for (int i = 0; i < mEntrys.size(); i++) {
            entry = mEntrys.get(i);
            if (entry == null)
                continue;
            if (entry.getClass() == classType) {
                return (T) entry;
            }
        }
        return null;
    }

    /*
     * if(entry instanceof GBTextBoxStyleEntry){ if(((GBTextBoxStyleEntry)
     * entry).getMarginBottom()!=null){ if(((GBTextBoxStyleEntry)
     * entry).getMarginBottom().Size==-1){ break; } }else{ continue; } }else{
     * break; }
     */

    public ArrayList<GBTextStyleEntry> getEntryList(Class<? extends GBTextStyleEntry> classType) {
        ArrayList<GBTextStyleEntry> entryList = new ArrayList<GBTextStyleEntry>();
        GBTextStyleEntry entry = null;
        for (int i = 0; i < mEntrys.size(); i++) {
            entry = mEntrys.get(i);
            if (entry == null)
                continue;
            if (entry.getClass() == classType) {
                entryList.add(entry);
                break;
            }
        }
        return entryList;
    }

    /*
     * class Kind { static final byte CSS_ENTRY = 1; static final byte
     * OTHER_ENTRY = 2; }
     */

    /**
     *
     * 功能描述：注册支持样式类(GBTextStyleEntry每个新子类都需通过该方法注册) 创建者： yangn<br>
     * 创建日期：2013-5-20<br>
     *
     * @param
     */

    public static void reg(Class<? extends GBTextStyleEntry> classType) {
        if (!mTypeMap.containsValue(classType)) {
            mTypeMap.put(mKind++, classType);
        }
    }

    /**
     * 判断是否支持该classType类型 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-5-20<br>
     *
     * @param
     */
    /*
     * public static isSupport(Class<?> classType){ return false; }
     */
}
