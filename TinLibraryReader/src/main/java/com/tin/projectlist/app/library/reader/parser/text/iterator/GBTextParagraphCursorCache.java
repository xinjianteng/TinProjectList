package com.tin.projectlist.app.library.reader.parser.text.iterator;

import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;

import java.lang.ref.WeakReference;
import java.util.HashMap;
/**
 *
 * 类名： .java<br>
 * 描述： 文本段缓存类 <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-9<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextParagraphCursorCache {
    private final static class Key {
        private final GBTextModel myModel;
        private final int myChpFileIndex;
        private final int myIndex;

        public Key(GBTextModel model, int chpFileIndex, int index) {
            myModel = model;
            myChpFileIndex = chpFileIndex;
            myIndex = index;
        }

        public boolean equals(Object o) {
            Key k = (Key) o;
            return (myModel == k.myModel) && (myChpFileIndex == k.myChpFileIndex) && (myIndex == k.myIndex);
        }

        public int hashCode() {
            return myModel.hashCode() + myChpFileIndex + myIndex;
        }
    }

    private static final HashMap<Key, WeakReference<GBTextParagraphCursor>> ourMap = new HashMap<Key, WeakReference<GBTextParagraphCursor>>();

    public static void put(GBTextModel model, int chpFileIndex, int index, GBTextParagraphCursor cursor) {
        ourMap.put(new Key(model, chpFileIndex, index), new WeakReference<GBTextParagraphCursor>(cursor));
    }

    public static GBTextParagraphCursor get(GBTextModel model, int chpFileIndex, int index) {
        WeakReference<GBTextParagraphCursor> ref = ourMap.get(new Key(model, chpFileIndex, index));
        return (ref != null) ? ref.get() : null;
    }

    public static void clear() {
        ourMap.clear();
    }
}
