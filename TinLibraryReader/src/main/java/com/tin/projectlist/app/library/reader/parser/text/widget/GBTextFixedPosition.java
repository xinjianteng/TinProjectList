package com.core.text.widget;
/**
 * 描述不可改变的位置 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-23<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextFixedPosition extends GBTextPosition {
    public final int ChpFileIndex;
    public final int ParagraphIndex;
    public final int ElementIndex;
    public final int CharIndex;

    /**
     *
     * @param paragraphIndex 段索引
     * @param elementIndex 段元素索引
     * @param charIndex 段元素字符索引
     */
    public GBTextFixedPosition(int chpFileIndex, int paragraphIndex, int elementIndex, int charIndex) {
        ChpFileIndex = chpFileIndex;
        ParagraphIndex = paragraphIndex;
        ElementIndex = elementIndex;
        CharIndex = charIndex;
    }


    public GBTextFixedPosition(int chpFileIndex, int paragraphIndex, int elementIndex, int charIndex,long chpInWordNum) {
        ChpFileIndex = chpFileIndex;
        ParagraphIndex = paragraphIndex;
        ElementIndex = elementIndex;
        CharIndex = charIndex;
        this.chpInWordNum=chpInWordNum;
    }

    /**
     *
     * @param position 可改变位置
     */
    public GBTextFixedPosition(GBTextPosition position) {
        ChpFileIndex = position.getChpFileIndex();
        ParagraphIndex = position.getParagraphIndex();
        ElementIndex = position.getElementIndex();
        CharIndex = position.getCharIndex();
    }

    /**
     * 获取该位置的段索引
     */
    public final int getParagraphIndex() {
        return ParagraphIndex;
    }

    /**
     * 获取该位置段中元素索引
     */
    public final int getElementIndex() {
        return ElementIndex;
    }

    /**
     * 获取该位置段元素中的字符索引
     */
    public final int getCharIndex() {
        return CharIndex;
    }

    @Override
    public int getChpFileIndex() {
        return ChpFileIndex;
    }
}
