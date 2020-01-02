package com.core.text.model;
/**
 *
 * 描述： 书签实体类 创建者： 燕冠楠<br>
 * 创建日期：2013-3-26<br>
 */
public class GBTextMark implements Comparable<GBTextMark> {

    public final int ChpFileIndex;
    /**
     * 段索引
     */
    public final int ParagraphIndex;

    /**
     * 偏移量（开始索引）
     */
    public final int Offset;

    /**
     * 长度（结束索引）
     */
    public final int Length;

    public GBTextMark(int chpFileindex, int paragraphIndex, int offset, int length) {
        ChpFileIndex = chpFileindex;
        ParagraphIndex = paragraphIndex;
        Offset = offset;
        Length = length;
    }

    public GBTextMark(final GBTextMark mark) {
        ChpFileIndex = mark.ChpFileIndex;
        ParagraphIndex = mark.ParagraphIndex;
        Offset = mark.Offset;
        Length = mark.Length;
    }

    public int compareTo(GBTextMark mark) {
        final int diff = ParagraphIndex - mark.ParagraphIndex;
        return diff != 0 ? diff : Offset - mark.Offset;
    }

    public String toString() {
        return ParagraphIndex + " " + Offset + " " + Length;
    }
}
