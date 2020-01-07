package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.object.GBColor;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextParagraphCursor;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextParagraph;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextBorderStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextCssDecoratedStyle;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyle;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyle.BorderStation;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyleCache;

/**
 * 类名： CssProvider.java<br>
 * 描述： 样式组织者对象<br>
 * 创建者： jack<br>
 * 创建日期：2013-11-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class CssProvider {

    private final GBTextView mView;
    private final GBTextModel myModel;

    public CssProvider(GBTextView view, GBTextModel model) {
        this.mView = view;
        this.myModel = model;
    }

    /**
     * 功能描述： 加载样式信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-5<br>
     *
     * @param chpFileIndex 章节索引
     * @param paraIndex 文本段
     */
    public void loadCssStyle(int chpFileIndex, int paraIndex) {
        if (!mView.isUseCssStyleImp())
            return;
        if (mView.mStyleChapIndex == chpFileIndex && mView.mStyleParaIndex == paraIndex)
            return;

        mView.mStyleStack.clear();
        mView.mStyleParaIndex = chpFileIndex;
        mView.mStyleParaIndex = paraIndex;

        int[] mStyleIndex = myModel.getStyleParagraphIncluded(chpFileIndex, paraIndex);
        if (mStyleIndex == null || mStyleIndex.length == 0)
            return;
        int styleIndex = -1;
        for (int i = 0; i < mStyleIndex.length; i++) {
            styleIndex = mStyleIndex[i];
            if (styleIndex > -1) {
                GBTextParagraphCursor ci = GBTextParagraphCursor.cursor(myModel, chpFileIndex, styleIndex);
                int index = 0, end = ci.getParagraphLength();
                for (; index != end; ++index) {
                    if (ci.getElement(index) instanceof GBTextStyleElement) {
                        final GBTextStyleElement gse = (GBTextStyleElement) ci.getElement(index);
                        if (gse.Entry != null) {
                            GBTextCssDecoratedStyle gcds = (GBTextCssDecoratedStyle) GBTextStyleCache.get(myModel,
                                    chpFileIndex, styleIndex, index);
                            if (gcds == null) {
                                gcds = new GBTextCssDecoratedStyle(mView.getTextStyle(), gse.Entry, styleIndex);
                                GBTextStyleCache.put(myModel, chpFileIndex, styleIndex, index, gcds);
                            }
                            mView.mStyleStack.push(gcds);
                        }
                    }
                }
            }
        }
        if (mView.mStyleStack.size() > 0)
            mView.setTextStyle(mView.mStyleStack.peek());
    }
    /**
     * 功能描述：绘制css设置的背景和边框 <br>
     * 创建者： jack<br>
     * 创建日期：2013-6-7<br>
     *
     * @param page 要渲染的页内容
     * @param info 要绘制的行
     * @param x 绘制行的左边距离
     * @param y 绘制行距顶部的高度
     */
    public void drawCssBgBorder(final GBTextPage page, final GBTextLineInfo info, int x, int y) {
        if (info.Height == 0 || !info.IsVisible)
            return;
        int ml = 0, mr = 0, mt = 0, mb = 0;
        GBTextStyle p = null;
        for (int j = 0; j < mView.mStyleStack.size(); j++) {
            p = mView.mStyleStack.get(j);
            if (p != null && p instanceof GBTextCssDecoratedStyle) {
                GBTextCssDecoratedStyle gbcs = (GBTextCssDecoratedStyle) p;

                boolean isPreDirect = info.isStartOfParagraph()
                        && isPreDirectStyle(info.ParagraphCursor, gbcs.mStyleParaIndex);
                boolean isLastEffect = info.isEndOfParagraph()
                        && isLastEffectStyle(info.ParagraphCursor, gbcs.mStyleParaIndex);

                if (!gbcs.isBoxStyleNull()) {
                    ml += gbcs.getLeftMargin(mView.metrics());
                    mr += gbcs.getRightMargin(mView.metrics());
                    if (isPreDirect)
                        mt += gbcs.getTopMargin(mView.metrics());
                    if (isLastEffect)
                        mb += gbcs.getBottomMargin(mView.metrics());
                }
                // 绘制背景
                if (!gbcs.isBackgroundStyleNull() && gbcs.getBackgroundColor() != -1) {
                    mView.getPaint().setFillColor(new GBColor(gbcs.getBackgroundColor()));
                    // 计算处理背景绘制区域
                    int temp = mView.mStyleStack.size() - j;
                    int endX = mView.getLeftMargin() + mView.getTextAreaSize().mWidth - mr;
                    for (int i = info.RealStartElementIndex; i <= info.EndElementIndex; i++) {
                        if (temp < 0 && info.ParagraphCursor.getElement(i) instanceof GBTextElement) {
                            final GBTextElementArea gea = page.getElementArea(info.ParagraphCursor.getElement(i));
                            if (gea != null)
                                endX = page.getElementArea(info.ParagraphCursor.getElement(i)).XEnd;
                            break;
                        }
                        if (info.ParagraphCursor.getElement(i) == GBTextElement.StyleClose)
                            temp--;
                        else if (info.ParagraphCursor.getElement(i) instanceof GBTextStyleElement)
                            temp++;
                    }
                    // 计算绘制背景色
                    mView.getPaint().fillRectangle(
                            x == 0 ? mView.getLeftMargin() + ml : x,
                            (info.StartElementIndex == 0 && info.StartCharIndex == 0) ? y + mt : y,
                            endX,
                            y
                                    + (info.isEndOfParagraph() ? Math.max(info.Height - info.mBottom + mb, info.Height
                                    - info.mBottom) : info.Height) + info.Descent);
                }
                // 绘制边框
                if (!gbcs.isBorderStyleNull()) {
                    drawBorder(info, y, gbcs, ml, mr, mt, mb);
                    if (isLastEffect)
                        mb += gbcs.getBorderWidth(BorderStation.Bottom, mView.metrics());
                    if (isPreDirect)
                        mt += gbcs.getBorderWidth(BorderStation.Top, mView.metrics());
                    ml += gbcs.getBorderWidth(BorderStation.Left, mView.metrics());
                    mr += gbcs.getBorderWidth(BorderStation.Right, mView.metrics());
                }

                if (!gbcs.isBoxStyleNull()) {
                    ml += gbcs.getLeftPadding(mView.metrics());
                    mr += gbcs.getRightPadding(mView.metrics());
                    if (isPreDirect)
                        mt += gbcs.getTopPadding(mView.metrics());
                    if (isLastEffect)
                        mb += gbcs.getBottomPadding(mView.metrics());
                }
            }
        }

    }
    /**
     * 功能描述： 绘制边框<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-9<br>
     *
     * @param info 要绘制的行
     * @param y y轴的开始位置
     */
    private void drawBorder(final GBTextLineInfo info, int y, GBTextCssDecoratedStyle gbcds, int ml, int mr, int mt,
                            int mb) {
        if (!info.IsVisible || info.Height <= 0 || mView.mStyleStack.size() == 0)
            return;
        mb = mb > 0 ? mb : 0;
        int topBorder = gbcds.getBorderWidth(BorderStation.Top, mView.metrics());
        int bottomBorder = gbcds.getBorderWidth(BorderStation.Bottom, mView.metrics());
        int leftBorder = gbcds.getBorderWidth(BorderStation.Left, mView.metrics());
        int rightBorder = gbcds.getBorderWidth(BorderStation.Right, mView.metrics());

        int x0, y0, x1, y1;
        // 绘制顶边框
        if (info.isStartOfParagraph() && topBorder > 0 && isPreDirectStyle(info.ParagraphCursor, gbcds.mStyleParaIndex)) {
            int topColor = gbcds.getBorderColor(BorderStation.Top);
            if (topColor != -1)
                mView.getPaint().setFillColor(new GBColor(topColor));
            x0 = mView.getLeftMargin() + ml;
            y0 = y + mt;
            x1 = mView.getLeftMargin() + mView.getTextAreaSize().mWidth - mr;
            y1 = y + mt + topBorder;
            switch (gbcds.getBorderStyle(BorderStation.Top)) {
                case GBTextBorderStyleEntry.FeatureModifier.DOTTED :
                    // 绘制点线
                    int start1 = mView.getLeftMargin() + ml + topBorder;
                    int size1 = mView.getTextAreaSize().mWidth - mr - ml;
                    boolean flag1 = false;
                    float radius = (float) topBorder / 2;
                    for (int i = start1; i < size1; i += topBorder) {
                        if (flag1 = !flag1)
                            mView.getPaint().drawCircle(i, y + mt + radius, 5);
                    }
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.DOUBLE :
                    // 绘制双线
                    if (topColor != -1)
                        mView.getPaint().setLineColor(new GBColor(topColor));
                    mView.getPaint().fillRectangle(x0, y0, x1, y + mt + 2);
                    mView.getPaint().fillRectangle(x0 + leftBorder - 2, y0 + topBorder - 2, x1 - rightBorder + 2, y1);
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.DASHED :
                    // 绘制虚线
                    int start = mView.getLeftMargin() + ml;
                    int size = mView.getTextAreaSize().mWidth - mr - ml;
                    for (int i = start; i < size; i += 15) {
                        mView.getPaint().fillRectangle(i, y + mt, (i + 15) < (start + size) ? i + 15 : start + size,
                                y + mt + topBorder);
                        i += 5;
                    }
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.NONE :
                    // 空
                    break;
                default :
                    // 绘制实线
                    mView.getPaint().fillRectangle(x0, y0, x1, y1);
            }
        }
        // 绘制底边框
        if ((info.isEndOfParagraph() || (info.mIsTrLine && info.mIsTrEnd)) && bottomBorder > 0
                && isLastEffectStyle(info.ParagraphCursor, gbcds.mStyleParaIndex)) {
            int bottomColor = gbcds.getBorderColor(BorderStation.Bottom);

            if (bottomColor != -1)
                mView.getPaint().setFillColor(new GBColor(bottomColor));
            // TODO 表格边框问题
            int tempY = y;
            if (info.mIsTrLine && y < info.mYEndMax) {
                tempY = info.mYEndMax - info.Height - info.Descent - info.VSpaceAfter;
            }
            x0 = mView.getLeftMargin() + ml;
            y0 = tempY + info.Height - mb - bottomBorder + mView.getPaint().getDescent();
            x1 = mView.getLeftMargin() + mView.getTextAreaSize().mWidth - mr;
            y1 = tempY + info.Height - mb + mView.getPaint().getDescent();
            switch (gbcds.getBorderStyle(BorderStation.Bottom)) {
                case GBTextBorderStyleEntry.FeatureModifier.DOTTED :
                    // 绘制点线
                    int start1 = mView.getLeftMargin() + ml + bottomBorder;
                    int size1 = mView.getTextAreaSize().mWidth - mr - ml;
                    boolean flag1 = false;
                    float radius = (float) bottomBorder / 2;
                    for (int i = start1; i < size1; i += bottomBorder) {
                        if (flag1 = !flag1)
                            mView.getPaint().drawCircle(i,
                                    y + info.Height - mb - bottomBorder + mView.getPaint().getDescent() + radius, 5);
                    }
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.DOUBLE :
                    // 绘制双线
                    if (bottomColor != -1)
                        mView.getPaint().setLineColor(new GBColor(bottomColor));
                    // 内线
                    mView.getPaint()
                            .fillRectangle(x0 + leftBorder - 2, y0, x1 - rightBorder + 2, y1 - bottomBorder + 2);
                    mView.getPaint().fillRectangle(x0 + 2, y0 + bottomBorder - 2, x1 - 2, y1);
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.DASHED :
                    // 绘制虚线
                    int start = mView.getLeftMargin() + ml;
                    int size = mView.getTextAreaSize().mWidth - mr - ml;
                    for (int i = start; i < size; i += 10) {
                        mView.getPaint().fillRectangle(i,
                                y + info.Height - mb - bottomBorder + mView.getPaint().getDescent(),
                                (i + 10) < (start + size) ? i + 10 : start + size,
                                y + info.Height - mb + mView.getPaint().getDescent());
                        i += 5;
                    }
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.NONE :
                    // 空
                    break;
                default :
                    // 绘制实线
                    mView.getPaint().fillRectangle(x0, y0, x1, y1);
            }
        }
        // 绘制左边框
        if (leftBorder > 0) {
            int leftColor = gbcds.getBorderColor(BorderStation.Left);
            if (leftColor != -1)
                mView.getPaint().setFillColor(new GBColor(leftColor));
            x0 = (info.mIsTrLine ? info.mXStartOffset : mView.getLeftMargin()) + ml;
            y0 = y
                    + (info.isStartOfParagraph() && isPreDirectStyle(info.ParagraphCursor, gbcds.mStyleParaIndex)
                    ? mt
                    : 0);
            x1 = (info.mIsTrLine ? info.mXStartOffset : mView.getLeftMargin()) + ml;
            y1 = y + info.Height - (info.isEndOfParagraph() ? mb : 0) + mView.getPaint().getDescent();
            if (info.mIsTrLine)
                y1 = Math.max(y1, info.mYEndMax);
            switch (gbcds.getBorderStyle(BorderStation.Left)) {
                case GBTextBorderStyleEntry.FeatureModifier.DOUBLE :
                    // 绘制双线
                    if (leftColor != -1)
                        mView.getPaint().setLineColor(new GBColor(leftColor));
                    // 外线
                    mView.getPaint().fillRectangle(x0, y0, x1 + 2, y1);
                    mView.getPaint().fillRectangle(
                            x0 + leftBorder - 2,
                            y
                                    + (info.isStartOfParagraph()
                                    && isPreDirectStyle(info.ParagraphCursor, gbcds.mStyleParaIndex) ? mt
                                    + topBorder : 0),
                            x1 + leftBorder,
                            y + info.Height - (info.isEndOfParagraph() ? mb + bottomBorder : 0)
                                    + mView.getPaint().getDescent());
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.NONE :
                    // 空
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.DOTTED :
                    // 绘制点线
                case GBTextBorderStyleEntry.FeatureModifier.DASHED :
                    // 绘制虚线
                default :
                    // 绘制实线
                    mView.getPaint().fillRectangle(x0, y0, x1 + leftBorder, y1);
            }
        }
        // 绘制右边框
        if (rightBorder > 0) {
            int leftColor = gbcds.getBorderColor(BorderStation.Right);
            if (leftColor != -1)
                mView.getPaint().setFillColor(new GBColor(leftColor));
            x0 = (info.mIsTrLine ? info.mXEndOffset : (mView.getLeftMargin() + mView.getTextAreaSize().mWidth)) - mr;
            y0 = y + (info.isStartOfParagraph() ? mt : 0);
            x1 = (info.mIsTrLine ? info.mXEndOffset : (mView.getLeftMargin() + mView.getTextAreaSize().mWidth)) - mr;
            y1 = y + info.Height - (info.isEndOfParagraph() ? mb : 0) + mView.getPaint().getDescent();
            if (info.mIsTrLine)
                y1 = Math.max(y1, info.mYEndMax);
            switch (gbcds.getBorderStyle(BorderStation.Right)) {
                case GBTextBorderStyleEntry.FeatureModifier.DOUBLE :
                    // 绘制双线
                    if (leftColor != -1)
                        mView.getPaint().setLineColor(new GBColor(leftColor));
                    // 外线
                    mView.getPaint().fillRectangle(x0, y0, x1 - 2, y1);
                    mView.getPaint().fillRectangle(
                            x0 - rightBorder,
                            y + (info.isStartOfParagraph() ? mt + topBorder : 0),
                            x1 - rightBorder + 2,
                            y + info.Height - (info.isEndOfParagraph() ? mb + bottomBorder : 0)
                                    + mView.getPaint().getDescent());
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.NONE :
                    // 空
                    break;
                case GBTextBorderStyleEntry.FeatureModifier.DOTTED :
                    // 绘制点线
                case GBTextBorderStyleEntry.FeatureModifier.DASHED :
                    // 绘制虚线
                default :
                    // 绘制实线
                    mView.getPaint().fillRectangle(x0, y0, x1 + rightBorder, y1);
            }
        }
        // }
    }
    /**
     * 功能描述： 获取文本对齐方式<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-7<br>
     *
     * @param info 要获取的行
     * @return
     */
    public byte getTextAlign(final GBTextLineInfo info) {
        byte align = mView.getTextStyle().getAlignment();
        GBTextStyle p = null;
        for (int i = 0; i < mView.mStyleStack.size(); i++) {
            p = mView.mStyleStack.get(i);
            if (p != null && p instanceof GBTextCssDecoratedStyle) {
                GBTextCssDecoratedStyle gbcs = (GBTextCssDecoratedStyle) p;
                if (!gbcs.isWordstyleNull()) {
                    align = gbcs.getAlignment();
                }
            }
        }
        return align;
    }
    /**
     * 功能描述： 获取首行缩进<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-9<br>
     *
     * @return
     */
    public int getFristLineIndent() {
        int fristLIndent = mView.getTextStyle().getFirstLineIndentDelta(mView.metrics());
        for (GBTextStyle p : mView.mStyleStack) {
            if (p != null && p instanceof GBTextCssDecoratedStyle) {
                GBTextCssDecoratedStyle gbcs = (GBTextCssDecoratedStyle) p;
                if (!gbcs.isWordstyleNull())
                    fristLIndent = gbcs.getFirstLineIndentDelta(mView.metrics());
            }
        }
        return fristLIndent;
    }

    /**
     * 功能描述： 获取行高和初始化边框信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-9<br>
     *
     * @param info 行信息
     * @param isFristLine 是否段落第一行
     * @return 当前行高
     */
    public int getLineHeightAndInitMP(final GBTextLineInfo info, boolean isFristLine) {
        // 获取样式的行高度设置
        int height = info.Height;
        GBTextStyle p = null;
        for (int i = 0; i < mView.mStyleStack.size(); i++) {
            p = mView.mStyleStack.get(i);
            if (p != null && p instanceof GBTextCssDecoratedStyle) {
                GBTextCssDecoratedStyle gbcs = (GBTextCssDecoratedStyle) p;
                if (!gbcs.isFontStyleNull()) {
                    height = Math.max(height,
                            (int) (mView.getPaint().getmFontSize() * ((float) gbcs.getLineSpacePercent() / 100)));
                }
                if (gbcs.isBorderStyleNull() && gbcs.isBoxStyleNull())
                    continue;
                boolean isPreDirect = info.isStartOfParagraph()
                        && isPreDirectStyle(info.ParagraphCursor, gbcs.mStyleParaIndex);
                boolean isLastEffect = info.isEndOfParagraph()
                        && isLastEffectStyle(info.ParagraphCursor, gbcs.mStyleParaIndex);
                if (!gbcs.isBorderStyleNull()) {
                    if (isPreDirect)
                        info.mTop += gbcs.getBorderWidth(BorderStation.Top, mView.metrics());
                    if (isLastEffect)
                        info.mBottom += gbcs.getBorderWidth(BorderStation.Bottom, mView.metrics());
                    info.mLeft += gbcs.getBorderWidth(BorderStation.Left, mView.metrics());
                    info.mRight += gbcs.getBorderWidth(BorderStation.Right, mView.metrics());
                }
                if (!gbcs.isBoxStyleNull()) {
                    info.mLeft += gbcs.getLeftMargin(mView.metrics());
                    info.mRight += gbcs.getRightMargin(mView.metrics());
                    info.mLeft += gbcs.getLeftPadding(mView.metrics());
                    info.mRight += gbcs.getRightPadding(mView.metrics());
                    if (isPreDirect) {
                        info.mTop += gbcs.getTopMargin(mView.metrics());
                        info.mTop += gbcs.getTopPadding(mView.metrics());
                    }
                    if (isLastEffect) {
                        info.mBottom += gbcs.getBottomMargin(mView.metrics());
                        info.mBottom += gbcs.getBottomPadding(mView.metrics());
                    }
                }
            }
        }

        if (isFristLine) {
            height += info.mTop;
        }
        return height;
    }
    /*
     * 是否直接或者嵌套有效样式
     */
    public boolean isPreDirectStyle(final GBTextParagraphCursor paraCursor, int styleParaIndex) {
        if (paraCursor.Index - 1 == styleParaIndex)
            return true;
        final GBTextParagraph para = myModel.getParagraph(paraCursor.chpFileIndex, paraCursor.Index - 1);
        if (para == null)
            return false;
        if (para.getKind() == GBTextParagraph.Kind.CSS_PARAGRAPH) {
            return paraCursor.Index - 3 == styleParaIndex;
        } else {
            return paraCursor.Index - 2 == styleParaIndex;
        }
    }
    /*
     * 是否嵌套结束样式
     */
    public boolean isLastEffectStyle(final GBTextParagraphCursor paraCursor, int styleParaIndex) {
        int[] styleIndexArr = myModel.getStyleParagraphIncluded(paraCursor.chpFileIndex, paraCursor.Index + 1);
        if (styleIndexArr == null)
            return true;
        for (int i : styleIndexArr) {
            if (i == styleParaIndex)
                return false;
        }
        return true;
    }
}
