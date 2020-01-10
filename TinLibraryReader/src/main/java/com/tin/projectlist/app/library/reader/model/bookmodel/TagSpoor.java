package com.tin.projectlist.app.library.reader.model.bookmodel;

import com.geeboo.utils.SimpleStack;

import java.util.HashMap;

/**
 *
 * 类名： .java<br>
 * 描述：标签痕迹 创建者： yangn<br>
 * 创建日期：2013-6-9<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TagSpoor {

    static final String TAG = "TagSpoor";

    public static SimpleStack STACK = new SimpleStack();

    static HashMap<String, Byte> mTagMapping = new HashMap<String, Byte>();
    static HashMap<Byte, String> mTagMappingTwo = new HashMap<Byte, String>();
    static {

        mTagMapping.put("h1", GBTextKind.H1);
        mTagMapping.put("h2", GBTextKind.H2);
        mTagMapping.put("h3", GBTextKind.H3);
        mTagMapping.put("h4", GBTextKind.H4);
        mTagMapping.put("h5", GBTextKind.H5);
        mTagMapping.put("h6", GBTextKind.H6);
        mTagMapping.put("div", GBTextKind.DIV);

        mTagMappingTwo.put(GBTextKind.H1, "h1");
        mTagMappingTwo.put(GBTextKind.H2, "h2");
        mTagMappingTwo.put(GBTextKind.H3, "h3");
        mTagMappingTwo.put(GBTextKind.H4, "h4");
        mTagMappingTwo.put(GBTextKind.H5, "h5");
        mTagMappingTwo.put(GBTextKind.H6, "h6");
        mTagMappingTwo.put(GBTextKind.DIV, "div");
    }

    public static String getKey(byte val) {
        if (mTagMappingTwo.containsKey(val)) {
            return mTagMappingTwo.get(val);
        }
        return null;
    }

    /**
     *
     * 功能描述：标签入栈 创建者： yangn<br>
     * 创建日期：2013-6-13<br>
     *
     * @param
     */
    public static void pushTag(String tagName) {

        if (mTagMapping.containsKey(tagName)) {
            STACK.pushStyleKind(mTagMapping.get(tagName));
        }

    }

    /**
     *
     * 功能描述：标签出栈 创建者： yangn<br>
     * 创建日期：2013-6-13<br>
     *
     * @param
     */
    public static void popTag() {
        if (STACK.size() > 0)
            STACK.pop();
    }

    /**
     *
     * 功能描述： 判断嵌套样式名称是否符合当前标签<br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-18<br>
     * @param styleBlockName 嵌套样式名称  例：div p
     */
    public static boolean endsWith(String styleBlockName) {
        String blockName=styleBlockName.replace(" ", "");
        StringBuilder sb = new StringBuilder();
        int j=0;
        for (int i = TagSpoor.STACK.size()-1; i >=0 ; i--) {
            sb.append(TagSpoor.getKey(TagSpoor.STACK.get(i)));
            if(j++>3){
                break;
            }
        }
        /*if (sb.toString().endsWith(styleBlockName.replace(" ", ""))) {
            L.e(TAG, "block=" + sb.toString());
            return true;
        }
        return false;*/
        return sb.toString().endsWith(blockName);
    }

}
