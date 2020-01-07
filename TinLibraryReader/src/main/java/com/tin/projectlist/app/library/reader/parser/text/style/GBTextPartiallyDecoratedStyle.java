package com.tin.projectlist.app.library.reader.parser.text.style;

import com.tin.projectlist.app.library.reader.parser.text.model.GBTextHyperlink;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextMetrics;

/**
 * partially 部分的 类名： GBTextPartiallyDecoratedStyle#ZLTextPartiallyDecoratedStyle<br>
 * 描述： html标签样式封装类，判断是否重新设置，没有则使用默认样式<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class GBTextPartiallyDecoratedStyle extends GBTextDecoratedStyle {
    private final GBTextDecorationStyleOption myDecoration;

    GBTextPartiallyDecoratedStyle(GBTextStyle base, GBTextDecorationStyleOption decoration, GBTextHyperlink hyperlink) {
        super(base, hyperlink);
        myDecoration = decoration;
    }

    @Override
    protected String getFontFamilyInternal() {
        String decoratedValue = myDecoration.FontFamilyOption.getValue();
        return (decoratedValue.length() != 0) ? decoratedValue : Base.getFontFamily();
    }

    @Override
    protected float getFontSizeInternal(GBTextMetrics metrics) {
        return Base.getFontSize(metrics) + myDecoration.FontSizeDeltaOption.getValue();
    }

    @Override
    protected boolean isBoldInternal() {
        switch (myDecoration.BoldOption.getValue()) {
            case B3_TRUE :
                return true;
            case B3_FALSE :
                return false;
            default :
                return Base.isBold();
        }
    }

    @Override
    protected boolean isItalicInternal() {
        switch (myDecoration.ItalicOption.getValue()) {
            case B3_TRUE :
                return true;
            case B3_FALSE :
                return false;
            default :
                return Base.isItalic();
        }
    }

    @Override
    protected boolean isUnderlineInternal() {
        switch (myDecoration.UnderlineOption.getValue()) {
            case B3_TRUE :
                return true;
            case B3_FALSE :
                return false;
            default :
                return Base.isUnderline();
        }
    }

    @Override
    protected boolean isStrikeThroughInternal() {
        switch (myDecoration.StrikeThroughOption.getValue()) {
            case B3_TRUE :
                return true;
            case B3_FALSE :
                return false;
            default :
                return Base.isStrikeThrough();
        }
    }

    @Override
    public int getFirstLineIndentDelta(GBTextMetrics metrics) {
        return Base.getFirstLineIndentDelta(metrics);
    }

    @Override
    public int getLineSpacePercent() {
        return Base.getLineSpacePercent();
    }

    @Override
    protected int getVerticalShiftInternal() {
        return Base.getVerticalShift() + myDecoration.VerticalShiftOption.getValue();
    }

    @Override
    public int getSpaceBefore() {
        return Base.getSpaceBefore();
    }

    @Override
    public int getSpaceAfter() {
        return Base.getSpaceAfter();
    }

    @Override
    public byte getAlignment() {
        return Base.getAlignment();
    }

    @Override
    public boolean allowHyphenations() {
        switch (myDecoration.AllowHyphenationsOption.getValue()) {
            case B3_FALSE :
                return false;
            case B3_TRUE :
                return true;
            default :
                return Base.allowHyphenations();
        }
    }

    @Override
    public int getTextColor() {
        return mTextColor;
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

    @Override
    protected void initBoxStyle(GBTextMetrics metrics) {
        mIsInitBox = true;
    }

}
