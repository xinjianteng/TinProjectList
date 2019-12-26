package com.tin.projectlist.app.library.reader.bookElements.text;
/**
 * original:ZLTextPosition 类名： .java<br>
 * 描述： 文本位置定义<br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-12<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBTextPosition implements Comparable<GBTextPosition> {

    /**
     * 功能描述： 获取章节索引<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-5<br>
     *
     * @return
     */
    public abstract int getChpFileIndex();
    /**
     * 段索引 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param
     */
    public abstract int getParagraphIndex();
    /**
     * 元素索引 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param
     */
    public abstract int getElementIndex();
    /**
     * 字符索引 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param
     */
    public abstract int getCharIndex();

    /**
     *
     * 功能描述： 获取GBTextPosition所描述在本章节中字符索引<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-20<br>
     *
     * @param
     */
    public long getChpInWordNum() {
        return chpInWordNum;
    }

    long chpInWordNum = 0L;

    /**
     *
     * 功能描述： 设置本章中字符索引<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-24<br>
     *
     * @param
     */
    public void setChpInWordNum(long chpInWordNum) {
        this.chpInWordNum = chpInWordNum;
    }

    /**
     * 位置对比 paragraphIndex && elementIndex && charIndex 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param
     */
    public boolean samePositionAs(GBTextPosition position) {
        return getChpFileIndex() == position.getChpFileIndex() && getParagraphIndex() == position.getParagraphIndex()
                && getElementIndex() == position.getElementIndex() && getCharIndex() == position.getCharIndex();
    }

    /**
     * 位置对比 段索引小于position则返回-1 大于则返回1 等于情况对比该段元素索引 元素索引等于情况对比元素字符
     * 元素字符等于情况返回zero0
     *
     */
    public int compareTo(GBTextPosition position) {
        final int chp0 = getChpFileIndex();
        final int chp1 = position.getChpFileIndex();
        if (chp0 != chp1) {
            return chp0 < chp1 ? -1 : 1;
        }

        final int p0 = getParagraphIndex();
        final int p1 = position.getParagraphIndex();
        if (p0 != p1) {
            return p0 < p1 ? -1 : 1;
        }

        final int e0 = getElementIndex();
        final int e1 = position.getElementIndex();
        if (e0 != e1) {
            return e0 < e1 ? -1 : 1;
        }

        final int c0 = getCharIndex();
        final int c1 = position.getCharIndex();
        if (c0 != c1) {
            return c0 < c1 ? -1 : 1;
        }

        return 0;
    }

    @Override
    public int hashCode() {
        return (getParagraphIndex() << 16) + (getElementIndex() << 8) + getCharIndex();
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof GBTextPosition)) {
            return false;
        }
        final GBTextPosition position = (GBTextPosition) object;
        return getParagraphIndex() == position.getParagraphIndex() && getElementIndex() == position.getElementIndex()
                && getCharIndex() == position.getCharIndex();
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + getChpFileIndex() + " " + getParagraphIndex() + " " + getElementIndex()
                + " " + getCharIndex() + " " + getChpInWordNum();
    }
}
