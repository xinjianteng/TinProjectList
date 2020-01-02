package com.core.text.widget;

import com.core.view.GBPaint;

/**
 * original:ZLTextWord 类名： .java<br>
 * 描述：一段文字 中文：一般为一个中文 英文|符号：不确定 //LineBreak被段字<br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-12<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextWord extends GBTextElement {
    public final char[] Data;//
    public final int Offset;// char[] data的偏移量
    public final int Length;// 该文字包含字符个数
    private int myWidth = -1;
    private Mark myMark;
    private int myParagraphOffset; // 所在段偏移量

    class Mark {
        public final int Start;
        public final int Length;
        private Mark myNext;

        private Mark(int start, int length) {
            Start = start;
            Length = length;
            myNext = null;
        }

        public Mark getNext() {
            return myNext;
        }

        private void setNext(Mark mark) {
            myNext = mark;
        }
    }

    /**
     * 文字 实例 某段的一串字符
     *
     * @param data 字符数组
     * @param offset 偏移量
     * @param length 长度
     * @param paragraphOffset 段偏移量
     */
    public GBTextWord(char[] data, int offset, int length, int paragraphOffset) {
        Data = data;
        Offset = offset;
        Length = length;
        myParagraphOffset = paragraphOffset;
    }

    /**
     * 是否是空格 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    public boolean isASpace() {
        for (int i = Offset; i < Offset + Length; ++i) {
            if (!Character.isWhitespace(Data[i])) {
                return false;
            }
        }
        return true;
    }

    public Mark getMark() {
        return myMark;
    }

    /**
     * 获取段偏移量 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    public int getParagraphOffset() {
        return myParagraphOffset;
    }

    /**
     * 添加书签 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param start 开始位置
     * @param length 长度
     */
    public void addMark(int start, int length) {
        Mark existingMark = myMark;
        Mark mark = new Mark(start, length);
        if ((existingMark == null) || (existingMark.Start > start)) {
            mark.setNext(existingMark);
            myMark = mark;
        } else {
            while ((existingMark.getNext() != null) && (existingMark.getNext().Start < start)) {
                existingMark = existingMark.getNext();
            }
            mark.setNext(existingMark.getNext());
            existingMark.setNext(mark);
        }
    }

    public int getWidth(GBPaint context) {
        int width = myWidth;
        if (width <= 1) {
            width = context.getCharArrWidth(Data, Offset, Length);
            myWidth = width;
        }
        return width;
    }

    @Override
    public String toString() {
        return new String(Data, Offset, Length);
    }
}
