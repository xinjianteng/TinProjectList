package com.tin.projectlist.app.library.reader.parser.text.style;

import com.core.option.GBBooleanOption;
import com.core.option.GBIntegerRangeOption;
import com.core.option.GBStringOption;
import com.core.platform.GBLibrary;
import com.core.text.model.GBTextAlignmentType;
import com.core.text.model.GBTextHyperlink;
import com.core.text.model.GBTextMetrics;

/**
 * 类名： GBTextBaseStyle#ZLTextBaseStyle<br>
 * 描述： 默认基本样式<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextBaseStyle extends GBTextStyle {
    private static final String GROUP = "Style"; // 样式配置组定义
    private static final String OPTIONS = "Options"; // 配置组定义
    // 是否自动断字
    public final GBBooleanOption AutoHyphenationOption = new GBBooleanOption(OPTIONS, "AutoHyphenation", true);

    public final GBBooleanOption BoldOption = new GBBooleanOption(GROUP, "Base:bold", false);
    public final GBBooleanOption ItalicOption = new GBBooleanOption(GROUP, "Base:italic", false);
    public final GBBooleanOption UnderlineOption = new GBBooleanOption(GROUP, "Base:underline", false);
    public final GBBooleanOption StrikeThroughOption = new GBBooleanOption(GROUP, "Base:strikeThrough", false);
    public final GBIntegerRangeOption AlignmentOption = new GBIntegerRangeOption(GROUP, "Base:alignment", 1, 4,
            GBTextAlignmentType.ALIGN_JUSTIFY);
    // 行间距设置
    public final GBIntegerRangeOption LineSpaceOption = new GBIntegerRangeOption(GROUP, "Base:lineSpacing", 10, 20, 15);
    // 段间距
    public final GBIntegerRangeOption ParaSpaceOption = new GBIntegerRangeOption(GROUP, "Base:paraSpacing", 0, 15, 10);
    // 段落缩进
    public final GBIntegerRangeOption LeftIndentOption = new GBIntegerRangeOption(GROUP, "Base:LeftIndent", 0, 50, 35);

    public final GBStringOption FontFamilyOption;
    public final GBIntegerRangeOption FontSizeOption;

    public GBTextBaseStyle(String fontFamily, int fontSize) {
        super(null, GBTextHyperlink.NO_LINK);
        FontFamilyOption = new GBStringOption(GROUP, "Base:fontFamily", fontFamily);
        fontSize = fontSize * GBLibrary.Instance().getDisplayDPI() / 320 * 2;
        FontSizeOption = new GBIntegerRangeOption(GROUP, "Base:fontSize", Math.max(fontSize / 2, 10), Math.min(72,
                fontSize * 2), fontSize);
    }

    @Override
    public String getFontFamily() {
        return FontFamilyOption.getValue();
    }

    private int mFontSize = 0;
    public int getFontSize() {
        return FontSizeOption.getValue();
    }
    // int size转换为float modify by yangn
    @Override
    public float getFontSize(GBTextMetrics metrics) {
        return mFontSize > 0 ? mFontSize : getFontSize();
    }
    @Override
    public boolean isBold() {
        return BoldOption.getValue();
    }

    @Override
    public boolean isItalic() {
        return ItalicOption.getValue();
    }

    @Override
    public boolean isUnderline() {
        return UnderlineOption.getValue();
    }

    @Override
    public boolean isStrikeThrough() {
        return StrikeThroughOption.getValue();
    }

    @Override
    public int getLeftIndent() {
        return LeftIndentOption.getValue();
    }

    @Override
    public int getRightIndent() {
        return mRightIndent;
    }

    @Override
    public int getFirstLineIndentDelta(GBTextMetrics metrics) {
        return LeftIndentOption.getValue() * metrics.DPI / 160;
    }

    @Override
    public int getLineSpacePercent() {
        return LineSpaceOption.getValue() * 10;
    }

    // 段落间距
    @Override
    public int getParaSpace() {
        return ParaSpaceOption.getValue();
    }
    @Override
    public int getVerticalShift() {
        return 0;
    }

    @Override
    public int getSpaceBefore() {
        return 0;
    }

    @Override
    public int getSpaceAfter() {
        return 0;
    }

    @Override
    public byte getAlignment() {
        return (byte) AlignmentOption.getValue();
    }

    @Override
    public boolean allowHyphenations() {
        return true;
    }
    // 扩展属性:(标签可增加配置)
    private int mTextColor = 0;
    // private int mLeftIndent = 35;
    private int mRightIndent = 0;
    private int mLeftMargin = 0;
    private int mRightMargin = 0;
    private int mTopMargin = 0;
    private int mBottomMargin = 0;
    private int mLeftPadding = 0;
    private int mRightPadding = 0;
    private int mTopPadding = 0;
    private int mBottomPadding = 0;
    private int mBackgroundColor = -1;

    @Override
    public int getTextColor() {
        return mTextColor;
    }

    @Override
    public int getTopMargin(GBTextMetrics metrics) {
        return mTopMargin;
    }

    @Override
    public int getBottomMargin(GBTextMetrics metrics) {
        return mBottomMargin;
    }

    @Override
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @Override
    public int getLeftMargin(GBTextMetrics metrics) {
        return mLeftMargin;
    }

    @Override
    public int getRightMargin(GBTextMetrics metrics) {
        return mRightMargin;
    }

    @Override
    public int getLeftPadding(GBTextMetrics metrics) {
        return mLeftPadding;
    }

    @Override
    public int getRightPadding(GBTextMetrics metrics) {
        return mRightPadding;
    }

    @Override
    public int getTopPadding(GBTextMetrics metrics) {
        return mTopPadding;
    }

    @Override
    public int getBottomPadding(GBTextMetrics metrics) {
        return mBottomPadding;
    }

    @Override
    public int getBorderWidth(byte station, GBTextMetrics metrics) {
        return 0;
    }

    @Override
    public byte getBorderStyle(byte station) {
        return 0;
    }

    @Override
    public int getBorderColor(byte station) {
        return 0;
    }

}
