package com.tin.projectlist.app.library.reader.parser.text.widget;

import java.util.List;

import com.tin.projectlist.app.library.reader.parser.view.GBPaint;
/**
 * 描述： 描述文本区域范围<br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-18<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextRegion {
    /**
     * 类名： Soul.java<br>
     * 描述： 单一文本元素图书中位置信息定义<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-30<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static abstract class Soul implements Comparable<Soul> {
        final int ChpFileIndex;
        final int ParagraphIndex;
        final int StartElementIndex;
        final int EndElementIndex;

        /**
         *
         * @param paragraphIndex段索引
         * @param startElementIndex 段元素索引
         * @param endElementIndex 段元素字符索引
         */
        protected Soul(int chpFileIndex, int paragraphIndex, int startElementIndex, int endElementIndex) {
            ChpFileIndex = chpFileIndex;
            ParagraphIndex = paragraphIndex;
            StartElementIndex = startElementIndex;
            EndElementIndex = endElementIndex;
        }

        /***
         * 判断与area是否相等 功能描述： <br>
         * 创建者： yangn<br>
         * 创建日期：2013-4-23<br>
         *
         * @param area 对比域
         * @return 相等返回true 否则返回false
         */
        final boolean accepts(GBTextElementArea area) {
            return compareTo(area) == 0;
        }

        /**
         * 与other对比
         *
         * @return 对比与other的地址是否相等 若不相等判断other是否是Soul类型 若是则判断other描述的位置是否相等
         *         相等则返回true否则返回false 若判断other不是Soul类型则返回false
         */
        @Override
        public final boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof Soul)) {
                return false;
            }
            final Soul soul = (Soul) other;
            return ChpFileIndex == soul.ChpFileIndex && ParagraphIndex == soul.ParagraphIndex
                    && StartElementIndex == soul.StartElementIndex && EndElementIndex == soul.EndElementIndex;
        }

        /**
         * 位置对比 段索引小于position则返回-1 大于则返回1 等于情况对比该段元素索引 元素索引等于情况返回0
         *
         */
        public final int compareTo(Soul soul) {
            if (ParagraphIndex != soul.ParagraphIndex) {
                return ParagraphIndex < soul.ParagraphIndex ? -1 : 1;
            }
            if (EndElementIndex < soul.StartElementIndex) {
                return -1;
            }
            if (StartElementIndex > soul.EndElementIndex) {
                return 1;
            }
            return 0;
        }

        /**
         * 文本域位置对比 段索引小于position则返回-1 大于则返回1 等于情况对比该段元素索引 元素索引等于情况0
         *
         */
        public final int compareTo(GBTextElementArea area) {
            if (ParagraphIndex != area.ParagraphIndex) {
                return ParagraphIndex < area.ParagraphIndex ? -1 : 1;
            }
            if (EndElementIndex < area.ElementIndex) {
                return -1;
            }
            if (StartElementIndex > area.ElementIndex) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * 过滤器 类名： .java<br>
     * 描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-23<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    public static interface Filter {
        boolean accepts(GBTextRegion region);
    }

    public static Filter AnyRegionFilter = new Filter() {
        public boolean accepts(GBTextRegion region) {
            return true;
        }
    };

    public static Filter HyperlinkFilter = new Filter() {
        public boolean accepts(GBTextRegion region) {
            return region.getSoul() instanceof GBTextHyperlinkRegionSoul;
        }
    };

    public static Filter ImageOrHyperlinkFilter = new Filter() {
        public boolean accepts(GBTextRegion region) {
            final Soul soul = region.getSoul();
            return soul instanceof GBTextImageRegionSoul || soul instanceof GBTextHyperlinkRegionSoul;
        }
    };

    private final Soul mySoul; // 选中区域段落信息
    // this field must be accessed in synchronized context only
    private final List<GBTextElementArea> myAreaList; // 选中的元素区域集合
    private GBTextElementArea[] myAreas; // 选中元素区域数组
    private final int myFromIndex;
    private int myToIndex;
    private GBTextHorizontalConvexHull myHull;

    GBTextRegion(Soul soul, List<GBTextElementArea> list, int fromIndex) {
        mySoul = soul;
        myAreaList = list;
        myFromIndex = fromIndex;
        myToIndex = fromIndex + 1;
    }

    void extend() {
        ++myToIndex;
        myHull = null;
    }

    public Soul getSoul() {
        return mySoul;
    }

    /**
     * 功能描述： 文本域列表 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-23<br>
     *
     * @param
     */
    private GBTextElementArea[] textAreas() {
        if (myAreas == null || myAreas.length != myToIndex - myFromIndex) {
            synchronized (myAreaList) {
                myAreas = new GBTextElementArea[myToIndex - myFromIndex];
                for (int i = 0; i < myAreas.length; ++i) {
                    myAreas[i] = myAreaList.get(i + myFromIndex);
                }
            }
        }
        return myAreas;
    }

    private GBTextHorizontalConvexHull convexHull() {
        if (myHull == null) {
            myHull = new GBTextHorizontalConvexHull(textAreas());
        }
        return myHull;
    }

    GBTextElementArea getFirstArea() {
        return textAreas()[0];
    }

    GBTextElementArea getLastArea() {
        final GBTextElementArea[] areas = textAreas();
        return areas[areas.length - 1];
    }

    public int getLeft() {
        return getFirstArea().XStart;
    }

    public int getTop() {
        return getFirstArea().YStart;
    }

    public int getRight() {
        return getLastArea().XEnd;
    }

    public int getBottom() {
        return getLastArea().YEnd;
    }

    public void draw(GBPaint context) {
        convexHull().draw(context);
    }

    int distanceTo(int x, int y) {
        return convexHull().distanceTo(x, y);
    }

    boolean isAtRightOf(GBTextRegion other) {
        return other == null || getFirstArea().XStart >= other.getLastArea().XEnd;
    }

    boolean isAtLeftOf(GBTextRegion other) {
        return other == null || other.isAtRightOf(this);
    }

    boolean isUnder(GBTextRegion other) {
        return other == null || getFirstArea().YStart >= other.getLastArea().YEnd;
    }

    boolean isOver(GBTextRegion other) {
        return other == null || other.isUnder(this);
    }

    boolean isExactlyUnder(GBTextRegion other) {
        if (other == null) {
            return true;
        }
        if (!isUnder(other)) {
            return false;
        }
        final GBTextElementArea[] areas0 = textAreas();
        final GBTextElementArea[] areas1 = other.textAreas();
        for (GBTextElementArea i : areas0) {
            for (GBTextElementArea j : areas1) {
                if (i.XStart <= j.XEnd && j.XStart <= i.XEnd) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean isExactlyOver(GBTextRegion other) {
        return other == null || other.isExactlyUnder(this);
    }
}
