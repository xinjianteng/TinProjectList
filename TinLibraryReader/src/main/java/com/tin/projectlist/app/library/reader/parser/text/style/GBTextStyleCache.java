package com.core.text.style;

import com.core.text.model.GBTextModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
/**
 *
 * 类名： GBTextStyleCache.java<br>
 * 描述： 样式信息缓存类 <br>
 * 创建者： jack<br>
 * 创建日期：2014-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextStyleCache {
    private final static class Key {
        private final GBTextModel myModel;
        private final int myChpFileIndex; // 章节索引
        private final int myStyleIndex; // 样式段落索引
        private final int myElemIndex; // 样式元素索引

        public Key(GBTextModel model, int chpFileIndex, int styleParaIndex, int elemIndex) {
            myModel = model;
            myChpFileIndex = chpFileIndex;
            myStyleIndex = styleParaIndex;
            myElemIndex = elemIndex;
        }

        public boolean equals(Object o) {
            Key k = (Key) o;
            return (myModel == k.myModel) && (myChpFileIndex == k.myChpFileIndex) && (myElemIndex == k.myElemIndex)
                    && (myStyleIndex == k.myStyleIndex);
        }

        public int hashCode() {
            return myModel.hashCode() + myChpFileIndex + myStyleIndex + myElemIndex;
        }
    }

    private static final HashMap<Key, WeakReference<GBTextStyle>> ourMap = new HashMap<Key, WeakReference<GBTextStyle>>();

    public static void put(GBTextModel model, int chpFileIndex, int styleParaIndex, int elemIndex, GBTextStyle style) {
        ourMap.put(new Key(model, chpFileIndex, styleParaIndex, elemIndex), new WeakReference<GBTextStyle>(style));
    }

    public static GBTextStyle get(GBTextModel model, int chpFileIndex, int styleParaIndex, int elemIndex) {
        WeakReference<GBTextStyle> ref = ourMap.get(new Key(model, chpFileIndex, styleParaIndex, elemIndex));
        return (ref != null) ? ref.get() : null;
    }

    public static void clear() {
        ourMap.clear();
    }
}
