package com.core.text.widget;
/**
 * 类名： GBTextAbstractHighlighting.java<br>
 * 描述： 高亮文本定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-23<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
interface GBTextAbstractHighlighting {
    boolean clear();

    boolean isEmpty();
    // 高亮开始位置
    GBTextPosition getStartPosition();
    // 高亮结束位置
    GBTextPosition getEndPosition();
    // 高亮开始文字元素区域
    GBTextElementArea getStartArea(GBTextPage page);
    // 高亮结束文字元素区域
    GBTextElementArea getEndArea(GBTextPage page);

    void setup(GBTextPosition start, GBTextPosition end);

    // 双翻页获取指定页面的区域
    GBTextElementArea getStartArea(GBTextPage page, boolean isLeft);
    GBTextElementArea getEndArea(GBTextPage page, boolean isLeft);
}
