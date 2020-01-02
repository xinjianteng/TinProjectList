package com.core.text.widget;

import com.core.domain.GBApplication;
import com.core.file.GBFile;
import com.core.object.GBColor;
import com.core.object.GBSize;
import com.core.platform.GBLibrary;
import com.core.text.iterator.GBTextParagraphCursor;
import com.core.text.model.GBHyperlinkType;
import com.core.text.model.GBTextHyperlink;
import com.core.text.model.GBTextMetrics;
import com.core.text.style.GBTextBaseStyle;
import com.core.text.style.GBTextCssDecoratedStyle;
import com.core.text.style.GBTextDecorationStyleOption;
import com.core.text.style.GBTextStyle;
import com.core.text.style.GBTextStyleCollection;
import com.core.view.GBPaint;
import com.core.view.GBView;
import com.core.view.PageEnum;
import com.core.view.PageEnum.ImgFitType;

import java.util.Stack;

abstract class GBTextViewBase extends GBView {

    final String TAG = "GBTextViewBase";
    public static enum ImageFitting {
        none, covers, all
    }
    // 当前应用的html标签配置样式
    private GBTextStyle myTextStyle;
    private int myWordHeight = -1;
    private GBTextMetrics myMetrics;

    GBTextViewBase(GBApplication application) {
        super(application);
        // resetTextStyle();
    }

    protected void resetMetrics() {
        myMetrics = null;
    }

    protected GBTextMetrics metrics() {
        if (myMetrics == null) {
            final GBTextStyleCollection collection = GBTextStyleCollection.Instance();
            final GBTextBaseStyle base = collection.getBaseStyle();
            myMetrics = new GBTextMetrics(GBLibrary.Instance().getDisplayDPI(), collection.getDefaultFontSize(),
                    base.getFontSize(),
                    // TODO: font X height
                    base.getFontSize() * 15 / 10,
                    // TODO: screen area width
                    100,
                    // TODO: screen area height
                    100);
        }
        return myMetrics;
    }
    /*
     * 获取一个字的高
     */
    final int getWordHeight() {
        if (myWordHeight == -1) {
            final GBTextStyle textStyle = myTextStyle;
            myWordHeight = (int) (mPaint.getStringHeight() * textStyle.getLineSpacePercent() / 100)
                    + textStyle.getVerticalShift();
        }
        return myWordHeight;
    }

    public abstract ImageFitting getImageFitting();

    public abstract int getLeftMargin();
    public abstract int getRightMargin();
    public abstract int getTopMargin();
    public abstract int getBottomMargin();

    public abstract GBFile getWallpaperFile();
    public abstract PageEnum.PageBgMode getWallpaperMode();
    public abstract GBColor getBackgroundColor();
    public abstract GBColor getSelectedBackgroundColor();
    public abstract GBColor getSelectedForegroundColor();
    public abstract GBColor getTextColor(GBTextHyperlink hyperlink);
    public abstract GBColor getHighlightingColor();
    public abstract GBColor getReadTextColor();
    public abstract GBColor getPageTitleColor();

    GBSize getTextAreaSize() {
        return new GBSize(getTextAreaWidth(), getTextAreaHeight());
    }

    GBSize getPaintAreaSize() {
        return new GBSize(mPaint.getWidth(), mPaint.getHeight());
    }

    int getTextAreaHeight() {
        return mPaint.getHeight() - getTopMargin() - getBottomMargin();
    }

    int getTextAreaWidth() {
        return mPaint.getWidth() - getLeftMargin() - getRightMargin();
    }
    /**
     * 功能描述：获取底部边线 <br>
     * 创建者： jack<br>
     * 创建日期：2013-5-2<br>
     *
     * @return
     */
    int getBottomLine() {
        return mPaint.getHeight() - getBottomMargin() - 1;
    }

    int getRightLine() {
        return mPaint.getWidth() - getRightMargin() - 1;
    }

    final GBTextStyle getTextStyle() {
        return myTextStyle;
    }

    final void setTextStyle(GBTextStyle style) {
        if (myTextStyle != style) {
            myTextStyle = style;
            myWordHeight = -1;
        }
        mPaint.setFont(style.getFontFamily(), style.getFontSize(metrics()), style.isBold(), style.isItalic(),
                style.isUnderline(), style.isStrikeThrough());
    }

    final void resetTextStyle() {
        setTextStyle(GBTextStyleCollection.Instance().getBaseStyle());
        mStyleChapIndex = 0;
        mStyleStack.clear();
        mStyleParaIndex = 0;
    }

    boolean isStyleChangeElement(GBTextElement element) {
        return element == GBTextElement.StyleClose || element instanceof GBTextStyleElement
                || element instanceof GBTextControlElement;
    }

    void applyStyleChangeElement(GBTextElement element) {
        if (element == GBTextElement.StyleClose) {
            applyStyleClose();
            // resetTextStyle();
        } else if (element instanceof GBTextStyleElement) {
            applyStyle((GBTextStyleElement) element);
        } else if (element instanceof GBTextControlElement) {
            applyControl((GBTextControlElement) element);
        }
    }
    /**
     * 功能描述：获取当前字体颜色<br>
     * 创建者： jack<br>
     * 创建日期：2013-10-29<br>
     *
     * @return
     */
    GBColor getTextColor() {

        boolean flag = getTextStyle().Hyperlink.Type == GBHyperlinkType.NONE;

        if (flag) {
            int color = getTextStyle().getTextColor();
            for (int i = 0; i < mStyleStack.size(); i++) {
                if (mStyleStack.get(i) != null && mStyleStack.get(i) instanceof GBTextCssDecoratedStyle) {
                    GBTextCssDecoratedStyle gbcs = (GBTextCssDecoratedStyle) mStyleStack.get(i);
                    if (!gbcs.isFontStyleNull())
                        color = gbcs.getTextColor();
                }
            }
            if (color != 0)
                return new GBColor(color);
        }
        return getTextColor(getTextStyle().Hyperlink);
    }
    /**
     * 应用样式 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-19<br>
     *
     * @param cursor
     * @param index 开始元素索引
     * @param end 结束元素索引
     */
    void applyStyleChanges(GBTextParagraphCursor cursor, int index, int end) {
        for (; index != end; ++index) {
            applyStyleChangeElement(cursor.getElement(index));
        }
    }
    /*
     * 根据标签控制元素设置样式
     */
    private void applyControl(GBTextControlElement control) {
        if (control.IsStart) {
            final GBTextDecorationStyleOption decoration = GBTextStyleCollection.Instance().getDecoration(control.Kind);
            // 判断是否超链接样式
            if (control instanceof GBTextHyperlinkControlElement) {
                setTextStyle(decoration.createDecoratedStyle(myTextStyle,
                        ((GBTextHyperlinkControlElement) control).Hyperlink));
            } else {
                if (null != decoration) {
                    setTextStyle(decoration.createDecoratedStyle(myTextStyle));
                }

            }
        } else {
            setTextStyle(mStyleStack.size() > 0 ? mStyleStack.peek() : myTextStyle.Base);
        }
    }

    // 存放css段落样式信息
    protected int mStyleChapIndex = 0; // mStyleStack 样式对应的章节索引
    protected int mStyleParaIndex = 0; // mStyleStack 样式对应的段落索引
    protected Stack<GBTextStyle> mStyleStack = new Stack<GBTextStyle>();
    // 临时存放css段落内部样式
    // protected Stack<GBTextCssDecoratedStyle> mInnerStyleStack = new
    // Stack<GBTextCssDecoratedStyle>();

    /**
     * 功能描述：设置css样式<br>
     * 创建者： jack<br>
     * 创建日期：2013-10-29<br>
     *
     * @param element 样式元素
     * @param isInner 是否段落内部样式
     */
    private void applyStyle(GBTextStyleElement element) {
        setTextStyle(mStyleStack.push(new GBTextCssDecoratedStyle(myTextStyle, ((GBTextStyleElement) element).Entry,
                element.myParaIndex)));
    }
    // 设置为默认样式
    private void applyStyleClose() {
        if (mStyleStack.size() > 0)
            mStyleStack.pop();
        setTextStyle(mStyleStack.size() > 0 ? mStyleStack.peek() : GBTextStyleCollection.Instance().getBaseStyle());
    }

    protected final PageEnum.ImgFitType getScalingType(GBTextImageElement imageElement) {
        switch (getImageFitting()) {
            default :
            case none :
                return ImgFitType.AUTO_FIT;
            case covers :
                return imageElement.IsCover ? ImgFitType.MAX_FIT : ImgFitType.AUTO_FIT;
            case all :
                return ImgFitType.MAX_FIT;
        }
    }

    GBSize mNoteSize, mAudioSize, mVideoSize, mAnimBgSize;
    /**
     *
     * 功能描述：获取元素宽<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-19<br>
     *
     * @param
     */
    final int getElementWidth(GBTextElement element, int charIndex) {
        if (element instanceof GBTextWord) {
            return getWordWidth((GBTextWord) element, charIndex);
        } else if (element instanceof GBTextImageElement) {
            final GBTextImageElement imageElement = (GBTextImageElement) element;
            final GBSize size = mPaint.getImgSize(imageElement.ImageData, imageElement.IsCover
                    ? getPaintAreaSize()
                    : getTextAreaSize(), getScalingType(imageElement));
            return size != null ? size.mWidth : 0;
        } else if (element instanceof GBNoteElement) {
            if (mNoteSize == null)
                mNoteSize = mPaint.getNoteSize(getTextAreaSize());
            return mNoteSize != null ? mNoteSize.mWidth : 0;
        } else if (element instanceof GBAudioElement) {
            if (mAudioSize == null)
                mAudioSize = mPaint.getAudioSize(getTextAreaSize());
            return mAudioSize != null ? mAudioSize.mWidth : 0;
        } else if (element instanceof GBVideoElement) {
            GBVideoElement elem = (GBVideoElement) element;
            if (elem.width > 0)
                return elem.width;
            if (mVideoSize == null)
                mVideoSize = mPaint.getVideoSize(getTextAreaSize());
            return mVideoSize != null ? mVideoSize.mWidth : 0;
        } else if (element instanceof GBAnimObjElement) {
            if (mAnimBgSize == null)
                mAnimBgSize = mPaint.getAnimBgSize(getTextAreaSize());
            return mAnimBgSize != null ? mAnimBgSize.mWidth : 0;
        } else if (element == GBTextElement.Indent) {
            return myTextStyle.getFirstLineIndentDelta(metrics());
        } else if (element instanceof GBTextFixedHSpaceElement) {
            return mPaint.getSpaceWidth() * ((GBTextFixedHSpaceElement) element).Length;
        }
        return 0;
    }
    /**
     * 功能描述： 获取一个元素的高<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-27<br>
     *
     * @param element
     * @return
     */
    final int getElementHeight(GBTextElement element) {
        if (element instanceof GBTextWord) {
            return getWordHeight();
        } else if (element instanceof GBTextImageElement) {
            final GBTextImageElement imageElement = (GBTextImageElement) element;
            final GBSize size = mPaint.getImgSize(imageElement.ImageData, imageElement.IsCover
                    ? getPaintAreaSize()
                    : getTextAreaSize(), getScalingType(imageElement));
            return (size != null ? size.mHeight : 0)
                    + Math.max(mPaint.getStringHeight() * (myTextStyle.getLineSpacePercent() - 100) / 100, 3);
        } else if (element instanceof GBNoteElement) {
            if (mNoteSize == null)
                mNoteSize = mPaint.getNoteSize(getTextAreaSize());
            return (mNoteSize != null ? mNoteSize.mHeight : 0);
        } else if (element instanceof GBAudioElement) {
            if (mAudioSize == null)
                mAudioSize = mPaint.getAudioSize(getTextAreaSize());
            return (mAudioSize != null ? mAudioSize.mHeight : 0);
            // + Math.max(mPaint.getStringHeight() *
            // (myTextStyle.getLineSpacePercent() - 100) / 100, 3);
        } else if (element instanceof GBVideoElement) {
            GBVideoElement elem = (GBVideoElement) element;
            if (elem.height > 0)
                return elem.height;
            if (mVideoSize == null)
                mVideoSize = mPaint.getVideoSize(getTextAreaSize());
            return (mVideoSize != null ? mVideoSize.mHeight : 0);
            // + Math.max(mPaint.getStringHeight() *
            // (myTextStyle.getLineSpacePercent() - 100) / 100, 3);
        } else if (element instanceof GBAnimObjElement) {
            if (mAnimBgSize == null)
                mAnimBgSize = mPaint.getAnimBgSize(getTextAreaSize());
            return (mAnimBgSize != null ? mAnimBgSize.mHeight : 0);
        }
        return 0;
    }

    final int getElementDescent(GBTextElement element) {
        return element instanceof GBTextWord ? mPaint.getDescent() : 0;
    }

    final int getWordWidth(GBTextWord word, int start) {
        return start == 0 ? word.getWidth(mPaint) : mPaint.getCharArrWidth(word.Data, word.Offset + start, word.Length
                - start);
    }

    final int getWordWidth(GBTextWord word, int start, int length) {
        return mPaint.getCharArrWidth(word.Data, word.Offset + start, length);
    }

    private char[] myWordPartArray = new char[20];

    final int getWordWidth(GBTextWord word, int start, int length, boolean addHyphenationSign) {
        if (length == -1) {
            if (start == 0) {
                return word.getWidth(mPaint);
            }
            length = word.Length - start;
        }
        if (!addHyphenationSign) {
            return mPaint.getCharArrWidth(word.Data, word.Offset + start, length);
        }
        char[] part = myWordPartArray;
        if (length + 1 > part.length) {
            part = new char[length + 1];
            myWordPartArray = part;
        }
        System.arraycopy(word.Data, word.Offset + start, part, 0, length);
        part[length] = '-';
        return mPaint.getCharArrWidth(part, 0, length + 1);
    }

    int getAreaLength(GBTextParagraphCursor paragraph, GBTextElementArea area, int toCharIndex) {
        setTextStyle(area.Style);
        final GBTextWord word = (GBTextWord) paragraph.getElement(area.ElementIndex);
        int length = toCharIndex - area.CharIndex;
        boolean selectHyphenationSign = false;
        if (length >= area.Length) {
            selectHyphenationSign = area.AddHyphenationSign;
            length = area.Length;
        }
        if (length > 0) {
            return getWordWidth(word, area.CharIndex, length, selectHyphenationSign);
        }
        return 0;
    }

    final void drawWord(int x, int y, GBTextWord word, int start, int length, boolean addHyphenationSign, GBColor color) {
        final GBPaint context = mPaint;
        context.setTextColor(color);
        if (start == 0 && length == -1) {
            drawString(x, y, word.Data, word.Offset, word.Length, word.getMark(), 0);
        } else {
            if (length == -1) {
                length = word.Length - start;
            }
            if (!addHyphenationSign) {
                drawString(x, y, word.Data, word.Offset + start, length, word.getMark(), start);
            } else {
                char[] part = myWordPartArray;
                if (length + 1 > part.length) {
                    part = new char[length + 1];
                    myWordPartArray = part;
                }
                System.arraycopy(word.Data, word.Offset + start, part, 0, length);
                part[length] = '-';
                drawString(x, y, part, 0, length + 1, word.getMark(), start);
            }
        }
    }

    private final void drawString(int x, int y, char[] str, int offset, int length, GBTextWord.Mark mark, int shift) {
        final GBPaint context = mPaint;
        if (mark == null) {
            context.drawString(x, y, str, offset, length);
        } else {
            int pos = 0;
            for (; (mark != null) && (pos < length); mark = mark.getNext()) {
                int markStart = mark.Start - shift;
                int markLen = mark.Length;

                if (markStart < pos) {
                    markLen += markStart - pos;
                    markStart = pos;
                }

                if (markLen <= 0) {
                    continue;
                }

                if (markStart > pos) {
                    int endPos = Math.min(markStart, length);
                    context.drawString(x, y, str, offset + pos, endPos - pos);
                    x += context.getCharArrWidth(str, offset + pos, endPos - pos);
                }

                if (markStart < length) {
                    context.setFillColor(getHighlightingColor());
                    int endPos = Math.min(markStart + markLen, length);
                    final int endX = x + context.getCharArrWidth(str, offset + markStart, endPos - markStart);
                    context.fillRectangle(x, y - context.getStringHeight(), endX - 1, y + context.getDescent());
                    context.drawString(x, y, str, offset + markStart, endPos - markStart);
                    x = endX;
                }
                pos = markStart + markLen;
            }

            if (pos < length) {
                context.drawString(x, y, str, offset + pos, length - pos);
            }
        }
    }
}
