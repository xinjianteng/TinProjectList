package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextParagraphCursor;
import com.tin.projectlist.app.library.reader.parser.text.model.impl.GBTextTrParagraphImpl;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyle;

/**
 * 行信息 某段 某个元素的某个字符索引 标记开始 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-18<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
final class GBTextLineInfo {
    final GBTextParagraphCursor ParagraphCursor;

    /**
     * 段元素数量
     */
    final int ParagraphCursorLength;

    final int StartElementIndex;
    final int StartCharIndex;
    int RealStartElementIndex;
    int RealStartCharIndex;
    int EndElementIndex;
    int EndCharIndex;

    boolean IsVisible;
    /**
     * 左缩进
     */
    int LeftIndent;
    int Width; // 行宽
    int Height; // 行高
    int Descent; //
    int VSpaceAfter; // 行间距
    int SpaceCounter;
    GBTextStyle StartStyle;

    // public boolean isStyleLine;
    public boolean isRealLine; // hr 标签标志 一条横线
    // 存储边距边框信息 add by jack
    public int mTop = 0;
    public int mRight = 0;
    public int mLeft = 0;
    public int mBottom = 0; // 行底边剧
    // 冗余边框
    public int mMorePadding = 0;
    public int mEveragePadding = 0;

    // 是否单元格行元素
    public boolean mIsTrLine = false;
    public boolean mIsTrEnd = false;
    public float mTrFontSize = -1;
    // 单元格索引
    public int mTdIndex = 0;
    // 单元格总底边剧
    public int mYEndMax = 0;
    public int mMaxheight = 0;
    // 行x起始位置
    public int mXStartOffset = 0;
    // 行x结束位置
    public int mXEndOffset = 0;
    /**
     * 构造方法
     *
     * @param paragraphCursor 段指针
     * @param elementIndex 开始元素索引
     * @param charIndex 开始字符索引 （所在元素的第几个字母）
     * @param style 样式
     */
    GBTextLineInfo(GBTextParagraphCursor paragraphCursor, int elementIndex, int charIndex, GBTextStyle style) {
        ParagraphCursor = paragraphCursor;
        ParagraphCursorLength = paragraphCursor.getParagraphLength();

        StartElementIndex = elementIndex;
        StartCharIndex = charIndex;
        RealStartElementIndex = elementIndex;
        RealStartCharIndex = charIndex;
        EndElementIndex = elementIndex;
        EndCharIndex = charIndex;

        StartStyle = style;
        isRealLine = false;
    }

    boolean isEndOfParagraph() {
        return EndElementIndex == ParagraphCursorLength;
    }

    boolean isStartOfParagraph() {
        if (mIsTrLine) {
            final GBTextTrParagraphImpl para = (GBTextTrParagraphImpl) ParagraphCursor.mParagraph;
            return StartElementIndex == para.mIndex.get(0) && StartCharIndex == 0;
        }
        return StartElementIndex == 0 && StartCharIndex == 0;
    }

    public boolean equals(Object o) {
        GBTextLineInfo info = (GBTextLineInfo) o;
        return (ParagraphCursor == info.ParagraphCursor) && (StartElementIndex == info.StartElementIndex)
                && (StartCharIndex == info.StartCharIndex);
    }

    public int hashCode() {
        return ParagraphCursor.hashCode() + StartElementIndex + 239 * StartCharIndex;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        for (int i = RealStartElementIndex; i < EndElementIndex; i++) {
            if (ParagraphCursor.getElement(i) instanceof GBTextWord)
                sb.append(ParagraphCursor.getElement(i).toString());
        }
        return sb.toString();
    }

}
