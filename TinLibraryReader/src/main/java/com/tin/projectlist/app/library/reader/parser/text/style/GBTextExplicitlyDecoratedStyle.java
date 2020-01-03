package com.tin.projectlist.app.library.reader.parser.text.style;

import com.core.object.GBBoolean3;
import com.core.text.model.GBTextMetrics;
import com.core.text.model.style.GBTextBackgroundStyleEntry;
import com.core.text.model.style.GBTextFontStyleEntry;
import com.core.text.model.style.GBTextStyleEntry;

/**
 * explicitly adj 明白的，明确的 类名：
 * GBTextExplicitlyDecoratedStyle#ZLTextExplicitlyDecoratedStyle<br>
 * 描述： 文字修饰类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextExplicitlyDecoratedStyle extends GBTextDecoratedStyle
        implements
        GBTextStyleEntry.Feature,
        GBTextStyleEntry.FontModifier {
    private final GBTextStyleEntry myEntry;

    final String TAG = "GBTextExplicitlyDecoratedStyle";
    public GBTextExplicitlyDecoratedStyle(GBTextStyle base, GBTextStyleEntry entry) {
        super(base, base.Hyperlink);
        if (entry == null) {
            myEntry = new GBTextFontStyleEntry();
        } else {
            myEntry = entry;
            mTextColor = ((GBTextFontStyleEntry) entry).getColor();
        }

    }

    @Override
    protected String getFontFamilyInternal() {
        if (myEntry.isFeatureSupported(FONT_FAMILY)) {
            // TODO: implement
        }
        return Base.getFontFamily();
    }
    @Override
    protected float getFontSizeInternal(GBTextMetrics metrics) {
        /*
         * if (myEntry instanceof GBTextCSSStyleEntry &&
         * !GBTextStyleCollection.Instance().UseCSSFontSizeOption.getValue()) {
         * return Base.getFontSize(metrics); }
         */
        if (myEntry.isFeatureSupported(FONT_STYLE_MODIFIER)) {
            if (myEntry.getFontModifier(FONT_MODIFIER_INHERIT) == GBBoolean3.B3_TRUE) {
                return Base.Base.getFontSize(metrics);
            }
            if (myEntry.getFontModifier(FONT_MODIFIER_LARGER) == GBBoolean3.B3_TRUE) {
                return Base.Base.getFontSize(metrics) * 120 / 100;
            }
            if (myEntry.getFontModifier(FONT_MODIFIER_SMALLER) == GBBoolean3.B3_TRUE) {
                return Base.Base.getFontSize(metrics) * 100 / 120;
            }
        }
        if (myEntry.isFeatureSupported(LENGTH_FONT_SIZE)) {
            return myEntry.getLength(LENGTH_FONT_SIZE, metrics);
        }
        return Base.getFontSize(metrics);
    }

    @Override
    protected boolean isBoldInternal() {
        switch (myEntry.getFontModifier(FONT_MODIFIER_BOLD)) {
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
        switch (myEntry.getFontModifier(FONT_MODIFIER_ITALIC)) {
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
        switch (myEntry.getFontModifier(FONT_MODIFIER_UNDERLINED)) {
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
        switch (myEntry.getFontModifier(FONT_MODIFIER_STRIKEDTHROUGH)) {
            case B3_TRUE :
                return true;
            case B3_FALSE :
                return false;
            default :
                return Base.isStrikeThrough();
        }
    }

    public int getLeftIndent() {
        return Base.getLeftIndent();
    }
    public int getRightIndent() {
        return Base.getRightIndent();
    }
    public int getFirstLineIndentDelta(GBTextMetrics metrics) {
        return Base.getFirstLineIndentDelta(metrics);
    }
    public int getLineSpacePercent() {
        return Base.getLineSpacePercent();
    }
    @Override
    protected int getVerticalShiftInternal() {
        return Base.getVerticalShift();
    }
    public int getSpaceBefore() {
        return Base.getSpaceBefore();
    }
    public int getSpaceAfter() {
        return Base.getSpaceAfter();
    }
    public byte getAlignment() {
        if (myEntry instanceof GBTextBackgroundStyleEntry
                && !GBTextStyleCollection.Instance().UseCSSTextAlignmentOption.getValue()) {
            return Base.getAlignment();
        }
        return myEntry.isFeatureSupported(ALIGNMENT_TYPE) ? myEntry.getAlignmentType() : Base.getAlignment();
    }

    public boolean allowHyphenations() {
        return Base.allowHyphenations();
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
