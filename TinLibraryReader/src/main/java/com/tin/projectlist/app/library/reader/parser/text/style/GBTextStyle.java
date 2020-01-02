package com.core.text.style;

import com.core.text.model.GBTextHyperlink;
import com.core.text.model.GBTextMetrics;
/**
 * 类名： GBTextyStyle#ZLTextStyle<br>
 * 描述： 文字样式抽象定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-19<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBTextStyle {
    // 基本样式
    public final GBTextStyle Base;
    // 超链接文本
    public final GBTextHyperlink Hyperlink;

    protected GBTextStyle(GBTextStyle base, GBTextHyperlink hyperlink) {
        Base = base != null ? base : this;
        Hyperlink = hyperlink;
    }
    // 字体样式
    public abstract String getFontFamily();
    public abstract float getFontSize(GBTextMetrics metrics); // int
    // size转换为float
    // modify by yangn
    public abstract int getTextColor();
    public abstract boolean isBold(); // 是否粗体
    public abstract boolean isItalic(); // 是否斜体
    public abstract boolean isUnderline(); // 是否有下划线
    public abstract boolean isStrikeThrough(); // 是否有删除线
    public abstract boolean allowHyphenations(); // 是否允许断字链接
    // 段落位置
    public abstract int getLeftIndent(); // 获取段落左缩进大小
    public abstract int getRightIndent(); // 右缩进
    public abstract int getFirstLineIndentDelta(GBTextMetrics metrics); // 获取首行缩进大小
    public abstract int getLineSpacePercent(); // 获取一行所占空间的百分比 行间距配置
    public abstract int getVerticalShift(); // 获取内部垂直偏移量
    public abstract int getSpaceBefore();
    public abstract int getSpaceAfter();
    public abstract byte getAlignment(); // 对齐模式
    public abstract int getParaSpace(); // 段落间距
    // 外边距
    public abstract int getLeftMargin(GBTextMetrics metrics);
    public abstract int getRightMargin(GBTextMetrics metrics);
    public abstract int getTopMargin(GBTextMetrics metrics);
    public abstract int getBottomMargin(GBTextMetrics metrics);
    // 内边距
    public abstract int getLeftPadding(GBTextMetrics metrics);
    public abstract int getRightPadding(GBTextMetrics metrics);
    public abstract int getTopPadding(GBTextMetrics metrics);
    public abstract int getBottomPadding(GBTextMetrics metrics);
    // 背景样式
    public abstract int getBackgroundColor(); // 获取背景色
    // 边框
    public interface BorderStation {
        // 边框位置
        byte Left = 0;
        byte Right = 1;
        byte Top = 2;
        byte Bottom = 3;
    }
    public abstract int getBorderWidth(byte station, GBTextMetrics metrics);
    public abstract byte getBorderStyle(byte station);
    public abstract int getBorderColor(byte station);
}
