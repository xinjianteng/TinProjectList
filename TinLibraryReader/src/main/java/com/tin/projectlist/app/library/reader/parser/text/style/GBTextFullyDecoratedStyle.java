package com.tin.projectlist.app.library.reader.parser.text.style;

import com.core.text.model.GBTextAlignmentType;
import com.core.text.model.GBTextHyperlink;
import com.core.text.model.GBTextMetrics;

/**
 * 类名： ZLTextFullyDecoratedStyle.java<br>
 * 描述： 完整的样式装饰类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextFullyDecoratedStyle extends GBTextPartiallyDecoratedStyle {
    private final GBTextFullDecorationStyleOption myFullDecoration;

    GBTextFullyDecoratedStyle(GBTextStyle base, GBTextFullDecorationStyleOption decoration, GBTextHyperlink hyperlink) {
        super(base, decoration, hyperlink);
        myFullDecoration = decoration;
    }

    @Override
    public int getLeftIndent() {
        return Base.getLeftIndent() + myFullDecoration.LeftIndentOption.getValue();
    }

    @Override
    public int getRightIndent() {
        return Base.getRightIndent() + myFullDecoration.RightIndentOption.getValue();
    }

    @Override
    public int getFirstLineIndentDelta(GBTextMetrics metrics) {
        return (getAlignment() == GBTextAlignmentType.ALIGN_CENTER) ? 0 : Base.getFirstLineIndentDelta(metrics)
                + myFullDecoration.FirstLineIndentDeltaOption.getValue();
    }

    @Override
    public int getLineSpacePercent() {
        int value = myFullDecoration.LineSpacePercentOption.getValue();
        return (value != -1) ? value : Base.getLineSpacePercent();
    }

    @Override
    public int getSpaceBefore() {
        return myFullDecoration.SpaceBeforeOption.getValue();
    }

    @Override
    public int getSpaceAfter() {
        return myFullDecoration.SpaceAfterOption.getValue();
    }

    @Override
    public byte getAlignment() {
        byte value = (byte) myFullDecoration.AlignmentOption.getValue();
        return (value == GBTextAlignmentType.ALIGN_UNDEFINED) ? Base.getAlignment() : value;
    }
}
