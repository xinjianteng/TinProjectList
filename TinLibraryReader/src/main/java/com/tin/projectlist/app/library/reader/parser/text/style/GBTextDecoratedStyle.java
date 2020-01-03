package com.tin.projectlist.app.library.reader.parser.text.style;

import com.core.text.model.GBTextHyperlink;
import com.core.text.model.GBTextMetrics;
/**
 * 类名： GBTextDecoratedStyle.java#ZLTextDecoratedStyle<br>
 * 描述： 文字装饰类的抽象定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBTextDecoratedStyle extends GBTextStyle {
    // fields to be cached
    private String myFontFamily;
    private boolean myIsItalic;
    private boolean myIsBold;
    private boolean myIsUnderline;
    private boolean myIsStrikeThrough;
    private int myVerticalShift;

    private boolean myIsNotCached = true;

    private float myFontSize; // int size转换为float modify by yangn
    // add by jack
    protected int mTextColor = 0;
    protected int mLeftIndent = 0;
    protected int mRightIndent = 0;

    protected int mLeftMargin = 0;
    protected int mRightMargin = 0;
    protected int mTopMargin = 0;
    protected int mBottomMargin = 0;
    protected int mLeftPadding = 0;
    protected int mRightPadding = 0;
    protected int mTopPadding = 0;
    protected int mBottomPadding = 0;
    protected int mBackgroundColor = 0;
    protected boolean mIsInitBox = false;
    protected abstract void initBoxStyle(GBTextMetrics metrics);

    private GBTextMetrics myMetrics; // 文字度量实体

    protected GBTextDecoratedStyle(GBTextStyle base, GBTextHyperlink hyperlink) {
        super(base, (hyperlink != null) ? hyperlink : base.Hyperlink);
    }

    private void initCache() {
        myFontFamily = getFontFamilyInternal();
        myIsItalic = isItalicInternal();
        myIsBold = isBoldInternal();
        myIsUnderline = isUnderlineInternal();
        myIsStrikeThrough = isStrikeThroughInternal();
        myVerticalShift = getVerticalShiftInternal();

        myIsNotCached = false;
    }

    private void initMetricsCache(GBTextMetrics metrics) {
        myMetrics = metrics;
        myFontSize = getFontSizeInternal(metrics);
    }

    @Override
    public final String getFontFamily() {
        if (myIsNotCached) {
            initCache();
        }
        return myFontFamily;
    }
    protected abstract String getFontFamilyInternal();
    // int size转换为float modify by yangn
    @Override
    public final float getFontSize(GBTextMetrics metrics) {
        if (!metrics.equals(myMetrics)) {
            initMetricsCache(metrics);
        }
        return myFontSize;
    }
    // int size转换为float modify by yangn
    protected abstract float getFontSizeInternal(GBTextMetrics metrics);

    @Override
    public final boolean isItalic() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsItalic;
    }
    protected abstract boolean isItalicInternal();

    @Override
    public final boolean isBold() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsBold;
    }
    protected abstract boolean isBoldInternal();

    @Override
    public final boolean isUnderline() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsUnderline;
    }
    protected abstract boolean isUnderlineInternal();

    @Override
    public final boolean isStrikeThrough() {
        if (myIsNotCached) {
            initCache();
        }
        return myIsStrikeThrough;
    }
    protected abstract boolean isStrikeThroughInternal();

    @Override
    public final int getVerticalShift() {
        if (myIsNotCached) {
            initCache();
        }
        return myVerticalShift;
    }
    protected abstract int getVerticalShiftInternal();

    @Override
    public int getTopMargin(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mTopMargin == 0 && Base != null) ? Base.getTopMargin(metrics) : mTopMargin;
    }

    @Override
    public int getBottomMargin(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mBottomMargin == 0 && Base != null) ? Base.getBottomMargin(metrics) : mBottomMargin;
    }

    @Override
    public int getBackgroundColor() {
        return (mBackgroundColor == 0 && Base != null) ? Base.getBackgroundColor() : mBackgroundColor;
    }

    @Override
    public int getTextColor() {
        return (mTextColor == -1 && Base != null) ? Base.getTextColor() : mTextColor;
    }

    @Override
    public int getLeftMargin(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mLeftMargin == 0 && Base != null) ? Base.getLeftMargin(metrics) : mLeftMargin;
    }

    @Override
    public int getRightMargin(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mRightMargin == 0 && Base != null) ? Base.getRightMargin(metrics) : mRightMargin;
    }

    @Override
    public int getLeftPadding(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mLeftPadding == 0 && Base != null) ? Base.getLeftPadding(metrics) : mLeftPadding;
    }

    @Override
    public int getRightPadding(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mRightPadding == 0 && Base != null) ? Base.getRightPadding(metrics) : mRightPadding;
    }

    @Override
    public int getTopPadding(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mTopPadding == 0 && Base != null) ? Base.getTopPadding(metrics) : mTopPadding;
    }

    @Override
    public int getBottomPadding(GBTextMetrics metrics) {
        if (!mIsInitBox) {
            initBoxStyle(metrics);
        }
        return (mBottomPadding == 0 && Base != null) ? Base.getBottomPadding(metrics) : mBottomPadding;
    }
    @Override
    public int getLeftIndent() {
        return (mLeftIndent == 0 && Base != null) ? Base.getLeftIndent() : 0;
    }
    @Override
    public int getRightIndent() {
        return (mRightIndent == 0 && Base != null) ? Base.getRightIndent() : 0;
    }

    @Override
    public int getParaSpace() {
        return Base.getParaSpace();
    }

}
