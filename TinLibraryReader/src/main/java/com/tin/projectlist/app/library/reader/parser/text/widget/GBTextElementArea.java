package com.core.text.widget;

import com.core.text.style.GBTextStyle;

/**
 * 文本域 关联一个元素在屏幕上的xy坐标 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-18<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextElementArea extends GBTextFixedPosition {
    public final int XStart;
    public final int XEnd;
    public final int YStart;
    public final int YEnd;

    final int Length;
    final boolean AddHyphenationSign;
    final boolean ChangeStyle;
    public final GBTextStyle Style;
    public final GBTextElement Element;

    private final boolean myIsLastInElement;
    // 服务于双翻页，是否左页
    public final boolean mIsLeftPage;
    /**
     * 构造方法
     *
     * @param paragraphIndex 段落下标
     * @param elementIndex 元素下标
     * @param charIndex 字符下标
     * @param length 字符长度
     * @param lastInElement 是否最后一个元素
     * @param addHyphenationSign 是否有超链接标示
     * @param changeStyle 是否改变样式
     * @param style 样式
     * @param element 元素信息
     * @param xStart 该元素绘制的x开始位置
     * @param xEnd 该元素绘制的x结束位置
     * @param yStart 该元素绘制的y开始位置
     * @param yEnd 该元素绘制的y结束位置
     * @param isLeft 是否左侧页面
     */
    GBTextElementArea(int chpFileIndex, int paragraphIndex, int elementIndex, int charIndex, int length,
                      boolean lastInElement, boolean addHyphenationSign, boolean changeStyle, GBTextStyle style,
                      GBTextElement element, int xStart, int xEnd, int yStart, int yEnd, boolean isLeft) {
        super(chpFileIndex, paragraphIndex, elementIndex, charIndex);

        XStart = xStart;
        XEnd = xEnd;
        YStart = yStart;
        YEnd = yEnd;

        Length = length;
        myIsLastInElement = lastInElement;

        AddHyphenationSign = addHyphenationSign;
        ChangeStyle = changeStyle;
        Style = style;
        Element = element;

        mIsLeftPage = isLeft;
    }

    /**
     *
     * 功能描述：判断是否包含点 x y 创建者： yangn<br>
     * 创建日期：2013-4-17<br>
     *
     * @param x
     * @param y
     */
    boolean contains(int x, int y) {
        return (y >= YStart) && (y <= YEnd) && (x >= XStart) && (x <= XEnd);
    }

    boolean isFirstInElement() {
        return CharIndex == 0;
    }

    boolean isLastInElement() {
        return myIsLastInElement;
    }

    @Override
    public String toString() {
        return Element.toString() + "--" + XStart + "--" + XEnd + "--" + YStart + "--" + YEnd;
    }
}
