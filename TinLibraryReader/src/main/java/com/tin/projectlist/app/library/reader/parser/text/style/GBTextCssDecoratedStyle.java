package com.tin.projectlist.app.library.reader.parser.text.style;

import com.core.text.model.GBTextMetrics;
import com.core.text.model.style.GBTextBackgroundStyleEntry;
import com.core.text.model.style.GBTextBorderStyleEntry;
import com.core.text.model.style.GBTextBoxStyleEntry;
import com.core.text.model.style.GBTextFontStyleEntry;
import com.core.text.model.style.GBTextStyleEntry;
import com.core.text.model.style.GBTextStyleEntry.Length;
import com.core.text.model.style.GBTextStyleEntry.SizeUnit;
import com.core.text.model.style.GBTextStyleEntryProxy;
import com.core.text.model.style.GBTextWordStyleEntry;

/**
 * explicitly adj 明白的，明确的 类名： GBTextCssDecoratedStyle<br>
 * 描述： css样式文字修饰封装类<br>
 * 创建者： jack<br>
 * 创建日期：2013-10-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextCssDecoratedStyle extends GBTextDecoratedStyle
        implements
        GBTextStyleEntry.Feature,
        GBTextStyleEntry.FontModifier {
    private final GBTextStyleEntryProxy mProxy;
    private GBTextFontStyleEntry mFontStyleEntry;
    public boolean isFontStyleNull() {
        return mFontStyleEntry == null;
    }
    private GBTextBorderStyleEntry mBorderStyleEntry;
    public boolean isBorderStyleNull() {
        return mBorderStyleEntry == null;
    }
    private GBTextWordStyleEntry mWordStyleEntry;
    public boolean isWordstyleNull() {
        return mWordStyleEntry == null;
    }
    private GBTextBackgroundStyleEntry mBackgroundStyleEntry;
    public boolean isBackgroundStyleNull() {
        return mBackgroundStyleEntry == null;
    }
    private GBTextBoxStyleEntry mBoxStyleEntry;
    public boolean isBoxStyleNull() {
        return mBoxStyleEntry == null;
    }
    public final int mStyleParaIndex;
    final String TAG = "GBTextCssDecoratedStyle";
    public GBTextCssDecoratedStyle(GBTextStyle base, GBTextStyleEntryProxy entry, int paraIndex) {
        super(base, base.Hyperlink);
        mProxy = entry;
        mStyleParaIndex = paraIndex;
        if (entry != null) {
            mFontStyleEntry = mProxy.getEntry(GBTextFontStyleEntry.class);
            mBorderStyleEntry = mProxy.getEntry(GBTextBorderStyleEntry.class);
            mWordStyleEntry = mProxy.getEntry(GBTextWordStyleEntry.class);
            mBackgroundStyleEntry = mProxy.getEntry(GBTextBackgroundStyleEntry.class);
            mBoxStyleEntry = mProxy.getEntry(GBTextBoxStyleEntry.class);
            loadFontStyle();
            loadBGStyle();
        }
    }
    // 加载字体样式
    private void loadFontStyle() {
        if (mFontStyleEntry != null) {
            mTextColor = mFontStyleEntry.getColor();
        }
    }
    private void loadBGStyle() {
        if (mBackgroundStyleEntry != null) {
            mBackgroundColor = mBackgroundStyleEntry.getBackgroundColor();
        }
    }
    /*
     * 初始化边距样式
     */
    protected void initBoxStyle(GBTextMetrics metrics) {
        if (mBoxStyleEntry != null) {
            mBottomMargin = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getMarginBottom(), metrics, 0);
            mTopMargin = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getMarginTop(), metrics, 0);
            mLeftMargin = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getMarginLeft(), metrics, 0);
            mRightMargin = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getMarginRight(), metrics, 0);
            mBottomPadding = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getPaddingBottom(), metrics, 0);
            mTopPadding = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getPaddingTop(), metrics, 0);
            mLeftPadding = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getPaddingLeft(), metrics, 0);
            mRightPadding = (int) mBoxStyleEntry.getLength(mBoxStyleEntry.getPaddingRight(), metrics, 0);
        }
        mIsInitBox = true;
    }
    @Override
    protected String getFontFamilyInternal() {
        if (mFontStyleEntry != null)
            return mFontStyleEntry.getFamily().equals("") ? Base.getFontFamily() : mFontStyleEntry.getFamily();
        return Base.getFontFamily();
    }

    @Override
    protected float getFontSizeInternal(GBTextMetrics metrics) {
        if (mFontStyleEntry != null)
            return mFontStyleEntry.getFontSize() != null ? mFontStyleEntry.getLength(mFontStyleEntry.getFontSize(),
                    metrics, 0) : Base.getFontSize(metrics);
        return Base.getFontSize(metrics);
    }
    @Override
    protected boolean isBoldInternal() {
        if (mFontStyleEntry != null && mFontStyleEntry.getFontWeight() != 0)
            return mFontStyleEntry.getFontWeight() > 400;
        return Base.isBold();
    }
    @Override
    protected boolean isItalicInternal() {
        return Base.isItalic();
    }
    @Override
    protected boolean isUnderlineInternal() {
        return Base.isUnderline();
    }
    @Override
    protected boolean isStrikeThroughInternal() {
        return Base.isStrikeThrough();
    }

    public int getLeftIndent() {
        return Base.getLeftIndent();
    }
    public int getRightIndent() {
        return Base.getRightIndent();
    }
    public int getFirstLineIndentDelta(GBTextMetrics metrics) {
        if (mWordStyleEntry != null)
            return (int) mWordStyleEntry.getLength(mWordStyleEntry.getTextIndent(), metrics, 0);
        return Base.getFirstLineIndentDelta(metrics);
    }
    // 获取行高
    public int getLineSpacePercent() {
        /*
         * TODO 目前只支持 行高百分比
         */
        if (mFontStyleEntry != null && mFontStyleEntry.getLineHeight() != null) {
            Length len = mFontStyleEntry.getLineHeight();
            if (len.Unit == SizeUnit.PERCENT) {
                return (int) len.Size;
            } else if (len.Unit == SizeUnit.EM_100) {
                return (int) (len.Size * 100);
            }
        }
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
        if (mWordStyleEntry != null && mWordStyleEntry.getTextAlign() != 0)
            return mWordStyleEntry.getTextAlign();
        return Base.getAlignment();
    }
    public boolean allowHyphenations() {
        return Base.allowHyphenations();
    }
    @Override
    public int getBorderWidth(byte station, GBTextMetrics metrics) {
        if (mBorderStyleEntry != null) {
            switch (station) {
                case BorderStation.Left :
                    return (int) mBorderStyleEntry.getLength(mBorderStyleEntry.getBorderLeftWidth(), metrics, 0);
                case BorderStation.Right :
                    return (int) mBorderStyleEntry.getLength(mBorderStyleEntry.getBorderRightWidth(), metrics, 0);
                case BorderStation.Top :
                    return (int) mBorderStyleEntry.getLength(mBorderStyleEntry.getBorderTopWidth(), metrics, 0);
                case BorderStation.Bottom :
                    return (int) mBorderStyleEntry.getLength(mBorderStyleEntry.getBorderBottomWidth(), metrics, 0);
            }
        }
        return 0;
    }
    @Override
    public byte getBorderStyle(byte station) {
        if (mBorderStyleEntry != null) {
            switch (station) {
                case BorderStation.Left :
                    return mBorderStyleEntry.getBorderLeftStyle();
                case BorderStation.Right :
                    return mBorderStyleEntry.getBorderRightStyle();
                case BorderStation.Top :
                    return mBorderStyleEntry.getBorderTopStyle();
                case BorderStation.Bottom :
                    return mBorderStyleEntry.getBorderBottomStyle();
            }
        }
        return 0;
    }
    @Override
    public int getBorderColor(byte station) {
        if (mBorderStyleEntry != null) {
            switch (station) {
                case BorderStation.Left :
                    return mBorderStyleEntry.getBorderLeftColor();
                case BorderStation.Right :
                    return mBorderStyleEntry.getBorderRightColor();
                case BorderStation.Top :
                    return mBorderStyleEntry.getBorderTopColor();
                case BorderStation.Bottom :
                    return mBorderStyleEntry.getBorderBottomColor();
            }
        }
        return 0;
    }

}
