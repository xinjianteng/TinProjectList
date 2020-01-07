package com.tin.projectlist.app.library.reader.parser.text.widget;

import android.graphics.Point;

import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.reader.parser.common.util.IFunction;
import com.tin.projectlist.app.library.reader.parser.common.util.MiscUtil;
import com.tin.projectlist.app.library.reader.parser.domain.GBApplication;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.object.GBColor;
import com.tin.projectlist.app.library.reader.parser.object.GBSize;
import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;
import com.tin.projectlist.app.library.reader.parser.text.hyphenation.GBTextHyphenationInfo;
import com.tin.projectlist.app.library.reader.parser.text.hyphenation.GBTextHyphenator;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextParagraphCursor;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextParagraphCursorCache;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextSelectionCursor;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextWordCursor;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextAlignmentType;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextMark;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel.PositionInfo;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextParagraph;
import com.tin.projectlist.app.library.reader.parser.text.model.impl.GBTextTrParagraphImpl;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextCssDecoratedStyle;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyle;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyleCache;
import com.tin.projectlist.app.library.reader.parser.text.style.GBTextStyleCollection;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextHighlighting.LIGHTMODEL;
import com.tin.projectlist.app.library.reader.parser.view.GBPaint;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum.PageIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 类名： GBTextView.java<br>
 * 描述： 阅读控件页面绘制实现类<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-24<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBTextView extends GBTextViewBase implements IFunction<Integer> {
    final String TAG = "GBTextView";
    public static final int MAX_SELECTION_DISTANCE = 20;

    // 滚动模式
    public interface ScrollingMode {
        int NO_OVERLAPPING = 0; // 无重叠
        int KEEP_LINES = 1; // 保存行
        int SCROLL_LINES = 2; // 滚动行
        int SCROLL_PERCENTAGE = 3; // 滚动百分比
    }

    ;

    private CssProvider mCssProvider;
    private GBTextModel myModel;

    /*
     * 行计算单位
     */
    private interface SizeUnit {
        int PIXEL_UNIT = 0; // 像素
        int LINE_UNIT = 1; // 行
    }

    ;

    private int myScrollingMode;
    private int myOverlappingValue;

    private GBTextPage myPreviousPage = new GBTextPage();
    public GBTextPage myCurrentPage = new GBTextPage();
    private GBTextPage myNextPage = new GBTextPage();

    private final HashMap<GBTextLineInfo, GBTextLineInfo> myLineInfoCache = new HashMap<GBTextLineInfo, GBTextLineInfo>();
    // 文本选择区域记录
    private GBTextRegion.Soul mySelectedRegionSoul;
    private boolean myHighlightSelectedRegion = true;
    // 当前选择区域
    private GBTextSelection mySelection;
    private List<GBTextHighlighting> myHighlightingList;
    private List<GBTextAnnotation> myAnnotationList;

    public GBTextView(GBApplication application) {
        super(application);
        mySelection = new GBTextSelection(this);
        myHighlightingList = new ArrayList<GBTextHighlighting>();
        myAnnotationList = new ArrayList<GBTextAnnotation>();
    }

    public synchronized void setModel(GBTextModel model) {
        GBTextParagraphCursorCache.clear();
        mCssProvider = new CssProvider(this, model);
        myModel = model;
        myCurrentPage.reset();
        myPreviousPage.reset();
        myNextPage.reset();
        isPaintLoading = false;
        isLockScroll = false; // 是否禁止翻页
        // 等待刷新的章节
        mRefreashChpIndex = -1;
        mPaintStatus = null;

        if (myModel != null) {
            for (int i = 0; i < myModel.getChapterSize(); i++) {
                if (!mApplication.isLoadBookChp(i))
                    continue;
                final int paragraphsNumber = myModel.getParagraphsNumber(i);
                if (paragraphsNumber > 0) {
                    myCurrentPage.moveStartCursor(GBTextParagraphCursor.cursor(myModel, i, 0));
                    break;
                }
            }
        }
        mApplication.getViewImp().reset();
    }

    public GBTextModel getModel() {
        return myModel;
    }

    /**
     * 功能描述： 获取当前页面文本开始位置<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-28<br>
     *
     * @return
     */
    public GBTextWordCursor getStartCursor() {
        if (myCurrentPage.StartCursor.isNull()) {
            preparePaintInfo(myCurrentPage, false);
        }

        return myCurrentPage.StartCursor;
    }

    public GBTextWordCursor getStartCursorFromUser() {
        if (myCurrentPage.StartCursor.isNull()) {
            preparePaintInfo(myCurrentPage, false);
        }
        MiscUtil.convertInternalPositionToChpInWordNum(myModel, myCurrentPage.StartCursor);
        return myCurrentPage.StartCursor;
    }

    public GBTextWordCursor getEndCursor() {
        if (myCurrentPage.EndCursor.isNull()) {
            preparePaintInfo(myCurrentPage, false);
        }
        return myCurrentPage.EndCursor;
    }

    /**
     * 功能描述：根据书签跳转<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-19<br>
     *
     * @param
     */
    private synchronized void gotoMark(GBTextMark mark) {
        if (mark == null) {
            return;
        }

        myPreviousPage.reset();
        myNextPage.reset();
        boolean doRepaint = false;
        if (myCurrentPage.StartCursor.isNull()) {
            doRepaint = true;
            preparePaintInfo(myCurrentPage, true);
        }
        if (myCurrentPage.StartCursor.isNull()) {
            return;
        }
        if (myCurrentPage.StartCursor.getParagraphIndex() != mark.ParagraphIndex
                || myCurrentPage.StartCursor.getMark().compareTo(mark) > 0) {
            doRepaint = true;
            gotoPosition(mark.ChpFileIndex, mark.ParagraphIndex, 0, 0);
            preparePaintInfo(myCurrentPage, true);
        }
        if (myCurrentPage.EndCursor.isNull()) {
            preparePaintInfo(myCurrentPage, true);
        }
        while (mark.compareTo(myCurrentPage.EndCursor.getMark()) > 0) {
            doRepaint = true;
            scrollPage(true, ScrollingMode.NO_OVERLAPPING, 0);
            preparePaintInfo(myCurrentPage, true);
        }
        if (doRepaint) {
            if (myCurrentPage.StartCursor.isNull()) {
                preparePaintInfo(myCurrentPage, true);
            }
            mApplication.getViewImp().reset();
            mApplication.getViewImp().repaint();
        }
    }

    public synchronized int search(final String text, boolean ignoreCase, boolean wholeText, boolean backward,
                                   boolean thisSectionOnly) {
        if (text.length() == 0) {
            return 0;
        }
        int startIndex = 0;
        int endIndex = myModel.getParagraphsNumber(myModel.getChapterSize() - 1);
        if (thisSectionOnly) {
            // TODO: implement
        }
        int count = myModel.search(text, 0, startIndex, myModel.getChapterSize() - 1, endIndex, ignoreCase);
        myPreviousPage.reset();
        myNextPage.reset();
        if (!myCurrentPage.StartCursor.isNull()) {
            rebuildPaintInfo();
            if (count > 0) {
                GBTextMark mark = myCurrentPage.StartCursor.getMark();
                gotoMark(wholeText ? (backward ? myModel.getLastMark() : myModel.getFirstMark()) : (backward ? myModel
                        .getPreviousMark(mark) : myModel.getNextMark(mark)));
            }
            mApplication.getViewImp().reset();
            mApplication.getViewImp().repaint();
        }
        return count;
    }

    public boolean canFindNext() {
        final GBTextWordCursor end = myCurrentPage.EndCursor;
        return !end.isNull() && (myModel != null) && (myModel.getNextMark(end.getMark()) != null);
    }

    public synchronized void findNext() {
        final GBTextWordCursor end = myCurrentPage.EndCursor;
        if (!end.isNull()) {
            gotoMark(myModel.getNextMark(end.getMark()));
        }
    }

    public boolean canFindPrevious() {
        final GBTextWordCursor start = myCurrentPage.StartCursor;
        return !start.isNull() && (myModel != null) && (myModel.getPreviousMark(start.getMark()) != null);
    }

    public synchronized void findPrevious() {
        final GBTextWordCursor start = myCurrentPage.StartCursor;
        if (!start.isNull()) {
            gotoMark(myModel.getPreviousMark(start.getMark()));
        }
    }

    public void clearFindResults() {
        if (!findResultsAreEmpty()) {
            myModel.removeAllMarks();
            rebuildPaintInfo();
            mApplication.getViewImp().reset();
            mApplication.getViewImp().repaint();
        }
    }

    public boolean findResultsAreEmpty() {
        return (myModel == null) || myModel.getMarks().isEmpty();
    }

    @Override
    public synchronized void onScrollEnd(PageIndex pageIndex) {
        if (isPaintLoading) {
            isLockScroll = true;
            return;
        }
        isLockScroll = false;
        switch (pageIndex) {
            case CURRENT:
                break;
            case PREVIOUS: {
                final GBTextPage swap = myNextPage;
                myNextPage = myCurrentPage;
                myCurrentPage = myPreviousPage;
                myPreviousPage = swap;
                myPreviousPage.reset();
                if (myCurrentPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
                    preparePaintInfo(myNextPage, false);
                    myCurrentPage.EndCursor.setCursor(myNextPage.StartCursor);
                    myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
                } else if (!myCurrentPage.EndCursor.isNull() && !myNextPage.StartCursor.isNull()
                        && !myCurrentPage.EndCursor.samePositionAs(myNextPage.StartCursor)) {
                    myNextPage.reset();
                    myNextPage.StartCursor.setCursor(myCurrentPage.EndCursor);
                    myNextPage.PaintState = PaintStateEnum.START_IS_KNOWN;
                    mApplication.getViewImp().reset();
                }
                break;
            }
            case NEXT: {
                final GBTextPage swap = myPreviousPage;
                myPreviousPage = myCurrentPage;
                myCurrentPage = myNextPage;
                myNextPage = swap;
                myNextPage.reset();
                switch (myCurrentPage.PaintState) {
                    case PaintStateEnum.NOTHING_TO_PAINT:
                        preparePaintInfo(myPreviousPage, false);
                        myCurrentPage.StartCursor.setCursor(myPreviousPage.EndCursor);
                        myCurrentPage.PaintState = PaintStateEnum.START_IS_KNOWN;
                        break;
                    case PaintStateEnum.READY:
                        myNextPage.StartCursor.setCursor(myCurrentPage.EndCursor);
                        myNextPage.PaintState = PaintStateEnum.START_IS_KNOWN;
                        break;
                }
                break;
            }
        }
    }

    /*
     * 装载高亮，批注和笔记信息
     */
    public void highlight(int id, GBTextPosition start, GBTextPosition end, String text) {
        GBTextHighlighting highlighting = new GBTextHighlighting();
        highlighting.setup(id, start, end, text);
        myHighlightingList.add(highlighting);
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
    }

    public void annotation(int id, GBTextPosition start, GBTextPosition end, String content, String text) {
        GBTextAnnotation ann = new GBTextAnnotation();
        ann.setup(id, start, end, content, text);
        myAnnotationList.add(ann);
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
    }

    public void updateAnnotation(GBTextAnnotation annotation) {
        for (GBTextAnnotation ann : myAnnotationList) {
            if (ann.getmId() == annotation.mId) {
                myAnnotationList.remove(ann);
                myAnnotationList.add(annotation);
                break;
            }
        }
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
    }

    /*
     * 设置高亮，批注和笔记信息
     */
    public void setupHighligth(List<GBTextHighlighting> list) {
        myHighlightingList.clear();
        myHighlightingList.addAll(list);
    }

    public void setupAnnotation(List<GBTextAnnotation> list) {
        myAnnotationList.clear();
        myAnnotationList.addAll(list);
    }

    /*
     * 判断触点区域是否在高亮，批注和笔记区域
     */
    protected GBTextHighlighting inHighlightingOverlap(int x, int y) {
        return myCurrentPage.inHighlightingOverlap(x, y, mPaint.getWidth());
    }

    /*
     * 判断触点区域是否在高亮，批注和笔记区域
     */
    protected GBTextAnnotation inAnnotationOverlap(int x, int y) {
        return myCurrentPage.inAnnotationOverlap(x, y, mPaint.getWidth());
    }

    public void removeHighlighting(int id) {
        for (GBTextHighlighting lighting : myCurrentPage.myHighlightingList) {
            if (lighting.getmId() == id) {
                myCurrentPage.myHighlightingList.remove(lighting);
                myHighlightingList.remove(lighting);
                break;
            }
        }
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
    }

    public void removeAnnotation(int id) {
        for (GBTextAnnotation ann : myCurrentPage.myAnnotationList) {
            if (ann.getmId() == id) {
                myCurrentPage.myAnnotationList.remove(ann);
                myAnnotationList.remove(ann);
                break;
            }
        }
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
    }

    /**
     * 功能描述： 移动选择拖把到指定坐标<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-3<br>
     *
     * @param cursor
     * @param x
     * @param y
     */
    protected void moveSelectionCursorTo(GBTextSelectionCursor cursor, int x, int y) {
        // y -= GBTextSelectionCursor.getHeight() / 2 +
        // GBTextSelectionCursor.getAccent() / 2;
        mySelection.setCursorInMovement(cursor, x, y);
        mySelection.expandTo(x, y);
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
    }

    protected void releaseSelectionCursor() {
        mySelection.stop();
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
    }

    /*
     * 获取文本选择游标
     */
    protected GBTextSelectionCursor getSelectionCursorInMovement() {
        return mySelection.getCursorInMovement();
    }

    /**
     * 功能描述： 获取文本选择器拖把坐标位置<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-10<br>
     *
     * @param page   当前页
     * @param cursor 要获取的拖把
     * @param isLeft 双翻页时（是否左页）
     * @return
     */
    private GBTextSelection.Point getSelectionCursorPoint(GBTextPage page, GBTextSelectionCursor cursor, boolean isLeft) {
        if (cursor == GBTextSelectionCursor.None) {
            return null;
        }
        // 如果正在移动则获取移动坐标
        if (cursor == mySelection.getCursorInMovement()) {
            return mySelection.getCursorInMovementPoint(isLeft);
        }

        if (cursor == GBTextSelectionCursor.Left) {
            if (mySelection.hasAPartBeforePage(page)) {
                return null;
            }
            final GBTextElementArea selectionStartArea = mySelection.getStartArea(page, isLeft);
            if (selectionStartArea != null) {
                // 添加图片拖把定位计算
                int end = selectionStartArea.YStart;
                return new GBTextSelection.Point(selectionStartArea.XStart, end);
            }
        } else {
            if (mySelection.hasAPartAfterPage(page)) {
                return null;
            }
            final GBTextElementArea selectionEndArea = mySelection.getEndArea(page, isLeft);
            if (selectionEndArea != null) {
                return new GBTextSelection.Point(selectionEndArea.XEnd, selectionEndArea.YEnd);
            }
        }
        return null;
    }

    /*
     * 计算触点与拖把的距离
     */
    private int distanceToCursor(int x, int y, GBTextSelection.Point cursorPoint) {
        if (cursorPoint == null) {
            return Integer.MAX_VALUE;
        }

        final int dX, dY;

        final int w = GBTextSelectionCursor.getWidth() / 2;
        if (x < cursorPoint.X - w) {
            dX = cursorPoint.X - w - x;
        } else if (x > cursorPoint.X + w) {
            dX = x - cursorPoint.X - w;
        } else {
            dX = 0;
        }

        final int h = GBTextSelectionCursor.getHeight();
        if (y < cursorPoint.Y) {
            dY = cursorPoint.Y - y;
        } else if (y > cursorPoint.Y + h) {
            dY = y - cursorPoint.Y - h;
        } else {
            dY = 0;
        }

        return Math.max(dX, dY);
    }

    /*
     * 获取文本选择拖把
     * @param x 点击坐标x
     * @param y 点击坐标y
     */
    protected GBTextSelectionCursor findSelectionCursor(int x, int y) {
        return findSelectionCursor(x, y, Integer.MAX_VALUE);
    }

    /*
     * 获取文本选择拖把
     */
    protected GBTextSelectionCursor findSelectionCursor(int x, int y, int maxDistance) {
        if (mySelection.isEmpty()) {
            return GBTextSelectionCursor.None;
        }
        final boolean isDouble = GBLibrary.Instance().DoublePageOption.getValue();
        final int leftDistance = distanceToCursor(
                (isDouble && x > mPaint.getWidth()) ? x - mPaint.getWidth() : x,
                y,
                getSelectionCursorPoint(myCurrentPage, GBTextSelectionCursor.Left, isDouble
                        ? x < mPaint.getWidth()
                        : true));
        final int rightDistance = distanceToCursor(
                (isDouble && x > mPaint.getWidth()) ? x - mPaint.getWidth() : x,
                y,
                getSelectionCursorPoint(myCurrentPage, GBTextSelectionCursor.Right, isDouble
                        ? x < mPaint.getWidth()
                        : true));

        if (rightDistance < leftDistance) {
            return rightDistance <= maxDistance ? GBTextSelectionCursor.Right : GBTextSelectionCursor.None;
        } else {
            return leftDistance <= maxDistance ? GBTextSelectionCursor.Left : GBTextSelectionCursor.None;
        }
    }

    /**
     * 功能描述： 绘制图片拖把 add by jack<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-10<br>
     *
     * @param context
     * @param x
     * @param y
     */
    protected abstract void drawSelectionCursorInternal(GBPaint context, int x, int y, boolean isLeft);

    // 绘制批注标志
    protected abstract void drawNotes(GBPaint context, int x, int y);

    // 绘制批注标志
    protected abstract void drawNotesBG(GBPaint context, int XStart, int YStart, int XEnd, int YEnd);

    // 绘制音频背景
    protected abstract void drawAudioBG(GBPaint context, int startX, int startY, int endX, int endY);

    // 绘制视频背景
    protected abstract void drawVideoBG(GBPaint context, int startX, int startY, int endX, int endY);

    // 绘制视频背景
    protected abstract void drawAnimBG(GBPaint context, int startX, int startY, int endX, int endY);

    @Override
    public synchronized void initPage(GBPaint context, PageIndex pageIndex) {
        mPaint = context;
        preparePaintInfo(getPage(pageIndex), false);
    }

    @Override
    public synchronized void paint(GBPaint context, PageIndex pageIndex) {
        mPaint = context;
        final GBFile wallpaper = getWallpaperFile();// 读取阅读背景图片
        if (wallpaper != null) {// 如果非空就使用背景图
            context.drawWallPage(wallpaper, getWallpaperMode());
        } else {// 否则使用背景颜色
            context.drawSingleColor(getBackgroundColor());
        }

        if (myModel == null || myModel.getTotalParagraphsNumber() == 0) {// 若mode为空
            // 或所得的段数为0就返回
            return;
        }

        if (isPaintLoading) {
//            paintLoading();
            resetTextStyle();
            String wait = "图书加载失败,请重新打开";
            mPaint.setmFontSize(30 * metrics().DPI / 160);
            mPaint.setTextColor(getTextColor());
            // 绘制加载中
            GBSize size = getPaintAreaSize();
            mPaint.drawString((size.mWidth - mPaint.getStrWidth(wait)) / 2, (size.mHeight - mPaint.getStringHeight()) / 2,
                    wait);
            return;
        } else if (mPaintStatus != null) {
            // 重绘信息获取
            if (mPaintStatus.mIsCurl) {
                switch (mPaintStatus.mPageIndex) {
                    case PREVIOUS:
                        myCurrentPage.EndCursor.setCursor(mPaintStatus.mCursor);
                        myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
                        break;
                    case NEXT:
                        myCurrentPage.StartCursor.setCursor(mPaintStatus.mCursor);
                        myCurrentPage.PaintState = PaintStateEnum.START_IS_KNOWN;
                        break;
                    default:
                        break;
                }
                myNextPage.reset();
                myPreviousPage.reset();
                mPaintStatus = null;
            } else {
                if (mPaintStatus.mIsStart) {
                    myCurrentPage.moveStartCursor(mPaintStatus.mChpFileIndex, mPaintStatus.mParagraphIndex,
                            mPaintStatus.mWordIndex, mPaintStatus.mCharIndex);
                    myCurrentPage.PaintState = PaintStateEnum.START_IS_KNOWN;
                } else {
                    myCurrentPage.moveEndCursor(mPaintStatus.mChpFileIndex, mPaintStatus.mParagraphIndex,
                            mPaintStatus.mWordIndex, mPaintStatus.mCharIndex);
                    myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
                }
                mPaintStatus = null;
            }
        }

        GBTextPage page;
        switch (pageIndex) {
            default:
            case CURRENT:// 当前页
                page = myCurrentPage;
                break;
            case PREVIOUS:// 上一页
                page = myPreviousPage;
                if (myPreviousPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
                    preparePaintInfo(myCurrentPage, true);
                    // 将当前页第一个字设置为上一页的最后一个字
                    myPreviousPage.EndCursor.setCursor(myCurrentPage.StartCursor);
                    myPreviousPage.PaintState = PaintStateEnum.END_IS_KNOWN;
                }
                break;
            case NEXT:// 下一页
                page = myNextPage;
                if (myNextPage.PaintState == PaintStateEnum.NOTHING_TO_PAINT) {
                    preparePaintInfo(myCurrentPage, true);
                    // 将当前页的就最后一个字设置为下一页的第一个字
                    myNextPage.StartCursor.setCursor(myCurrentPage.EndCursor);
                    myNextPage.PaintState = PaintStateEnum.START_IS_KNOWN;
                }
        }

        page.TextElementMap.clear();
        page.tempElementIndex = -1;
        page.tempReginIndex = -1;
        // 清除高亮信息
        page.myHighlightingList.clear();
        page.myAnnotationList.clear();
        // 生成行元素
        preparePaintInfo(page, true);

        if (isPaintLoading) {
            // 绘制加载中
            paintLoading();
            return;
        }

        if (page.StartCursor.isNull() || page.EndCursor.isNull()) {
            return;
        }
        final ArrayList<GBTextLineInfo> lineInfos = page.LineInfos;
        final int[] labels = new int[lineInfos.size() + 1];
        int y = getTopMargin();
        int index = 0;
        // 处理tr段落的信息
        int trYStart = -1; // tr顶边距记录
        int trIndex = -1; // 单元格索引
        int trYEnd = -1;
        int trParaIndex = -1;
        // 计算行中的元素区域
        GBTextLineInfo info = null;
        for (int i = 0; i < lineInfos.size(); i++) {
            info = lineInfos.get(i);
            reloadStyleInfo(info.ParagraphCursor, info.RealStartElementIndex);
            // tr段落记录
            if (trParaIndex != -1 && trParaIndex != info.ParagraphCursor.Index) {
                trYStart = -1;
                trIndex = -1;
                y = Math.max(y, trYEnd);
                trYEnd = -1;
                trParaIndex = -1;
            }
            if (info.mIsTrLine) {
                trParaIndex = info.ParagraphCursor.Index;
                if (trYStart == -1)
                    trYStart = y;
                if (trIndex < info.mTdIndex) {
                    trYEnd = Math.max(y, trYEnd);
                    y = trYStart;
                    trIndex = info.mTdIndex;
                }
            }
            y += (info.isStartOfParagraph() ? info.mTop : 0);
            prepareTextLine(page, info, y, true);
            y += info.Height + info.Descent + info.VSpaceAfter;
            labels[++index] = page.TextElementMap.size();
            if (index == page.tempIndex)
                break;
        }
        page.tempElementIndex = page.TextElementMap.size();
        page.tempReginIndex = page.TextElementMap.getRegionSize();

        paintTitle(page);

        // 绘制行元素
        y = getTopMargin();
        index = 0;

        trYStart = -1; // tr顶边距记录
        trIndex = -1; // 单元格索引
        trYEnd = -1;
        trParaIndex = -1;
        for (int i = 0; i < lineInfos.size(); i++) {
            info = lineInfos.get(i);
            reloadStyleInfo(info.ParagraphCursor, info.RealStartElementIndex);

            // tr段落记录
            if (trParaIndex != -1 && trParaIndex != info.ParagraphCursor.Index) {
                trYStart = -1;
                trIndex = -1;
                y = Math.max(y, trYEnd);
                trYEnd = -1;
                trParaIndex = -1;
            }
            if (info.mIsTrLine) {
                trParaIndex = info.ParagraphCursor.Index;
                if (trYStart == -1)
                    trYStart = y;
                if (trIndex < info.mTdIndex) {
                    trYEnd = Math.max(y, trYEnd);
                    y = trYStart;
                    trIndex = info.mTdIndex;
                }
                if (info.mMaxheight > 0)
                    info.mYEndMax = trYStart + info.mMaxheight;
            }
            y += (info.isStartOfParagraph() ? info.mTop : 0);
            drawTextLine(page, info, labels[index], labels[index + 1], y, true);
            y += info.Height + info.Descent + info.VSpaceAfter;
            ++index;
            if (index == page.tempIndex)
                break;
        }

        final GBTextRegion selectedElementRegion = getSelectedRegion(page);
        if (selectedElementRegion != null && myHighlightSelectedRegion) {
            selectedElementRegion.draw(context);
        }

        // if (isDrawImgSelectCouser()) {
        GBTextSelection.Point p = getSelectionCursorPoint(page, GBTextSelectionCursor.Left, true);
        if (p != null)
            drawSelectionCursorInternal(context, p.X, p.Y, true);
        p = getSelectionCursorPoint(page, GBTextSelectionCursor.Right, true);
        if (p != null)
            drawSelectionCursorInternal(context, p.X, p.Y, false);
        // } else {
        // // 绘制选择拖把
        // drawSelectionCursor(context, getSelectionCursorPoint(page,
        // GBTextSelectionCursor.Left, true), true);
        // drawSelectionCursor(context, getSelectionCursorPoint(page,
        // GBTextSelectionCursor.Right, true), false);
        // }
    }

    @Override
    public synchronized void paintRight(GBPaint context, PageIndex pageIndex) {
        mPaint = context;
        final GBFile wallpaper = getWallpaperFile();// 读取阅读背景图片
        if (wallpaper != null) {// 如果非空就使用背景图
            context.drawWallPage(wallpaper, getWallpaperMode());
        } else {// 否则使用背景颜色
            context.drawSingleColor(getBackgroundColor());
        }

        if (isPaintLoading) {
            // 绘制加载中
            paintLoading();
            return;
        }
        GBTextPage page = getPage(pageIndex);

        if (page.StartCursor.isNull() || page.EndCursor.isNull() || page.tempCursor.isNull() || page.tempIndex == -1) {
            return;
        }
        // 计算生成行中元素区域信息
        final ArrayList<GBTextLineInfo> lineInfos = page.LineInfos;
        final int[] labels = new int[lineInfos.size() + 1];
        labels[page.tempIndex] = page.TextElementMap.size();
        int y = getTopMargin();
        // 处理tr段落的信息
        int trYStart = -1; // tr顶边距记录
        int trIndex = -1; // 单元格索引
        int trYEnd = -1;
        int trParaIndex = -1;

        GBTextLineInfo info = null;
        for (int i = page.tempIndex; i < lineInfos.size(); i++) {
            info = lineInfos.get(i);
            reloadStyleInfo(info.ParagraphCursor, info.RealStartElementIndex);
            // tr段落记录
            if (trParaIndex != -1 && trParaIndex != info.ParagraphCursor.Index) {
                trYStart = -1;
                trIndex = -1;
                y = Math.max(y, trYEnd);
                trYEnd = -1;
                trParaIndex = -1;
            }
            if (info.mIsTrLine) {
                trParaIndex = info.ParagraphCursor.Index;
                if (trYStart == -1)
                    trYStart = y;
                if (trIndex < info.mTdIndex) {
                    trYEnd = Math.max(y, trYEnd);
                    y = trYStart;
                    trIndex = info.mTdIndex;
                }
            }
            y += (info.isStartOfParagraph() ? info.mTop : 0);
            prepareTextLine(page, info, y, false);
            y += info.Height + info.Descent + info.VSpaceAfter;
            labels[i + 1] = page.TextElementMap.size();
        }
        // 绘制行元素
        y = getTopMargin();

        trYStart = -1; // tr顶边距记录
        trIndex = -1; // 单元格索引
        trYEnd = -1;
        trParaIndex = -1;
        for (int i = page.tempIndex; i < lineInfos.size(); i++) {
            info = lineInfos.get(i);
            reloadStyleInfo(info.ParagraphCursor, info.RealStartElementIndex);
            // tr段落记录
            if (trParaIndex != -1 && trParaIndex != info.ParagraphCursor.Index) {
                trYStart = -1;
                trIndex = -1;
                y = Math.max(y, trYEnd);
                trYEnd = -1;
                trParaIndex = -1;
            }
            if (info.mIsTrLine) {
                trParaIndex = info.ParagraphCursor.Index;
                if (trYStart == -1)
                    trYStart = y;
                if (trIndex < info.mTdIndex) {
                    trYEnd = Math.max(y, trYEnd);
                    y = trYStart;
                    trIndex = info.mTdIndex;
                }
                if (info.mMaxheight > 0)
                    info.mYEndMax = trYStart + info.mMaxheight;
            }
            y += (info.isStartOfParagraph() ? info.mTop : 0);
            drawTextLine(page, info, labels[i], labels[i + 1], y, false);
            y += info.Height + info.Descent + info.VSpaceAfter;
        }

        final GBTextRegion selectedElementRegion = getSelectedRegion(page);
        if (selectedElementRegion != null && myHighlightSelectedRegion) {
            selectedElementRegion.draw(context);
        }

        // if (isDrawImgSelectCouser()) {
        GBTextSelection.Point p = getSelectionCursorPoint(page, GBTextSelectionCursor.Left, false);
        if (p != null)
            drawSelectionCursorInternal(context, p.X, p.Y, true);
        p = getSelectionCursorPoint(page, GBTextSelectionCursor.Right, false);
        if (p != null)
            drawSelectionCursorInternal(context, p.X, p.Y, false);
        // } else {
        // // 绘制选择拖把
        // drawSelectionCursor(context, getSelectionCursorPoint(page,
        // GBTextSelectionCursor.Left, false), true);
        // drawSelectionCursor(context, getSelectionCursorPoint(page,
        // GBTextSelectionCursor.Right, false), false);
        // }
    }

    @Override
    public void paintLoading(GBPaint paint) {
        mPaint = paint;
        final GBFile wallpaper = getWallpaperFile();// 读取阅读背景图片
        if (wallpaper != null) {// 如果非空就使用背景图
            paint.drawWallPage(wallpaper, getWallpaperMode());
        } else {// 否则使用背景颜色
            paint.drawSingleColor(getBackgroundColor());
        }
        paintLoading();
        return;
    }

    /**
     * 功能描述： 渲染加载中<br>
     * 创建者： jack<br>
     * 创建日期：2015-1-20<br>
     *
     * @param
     */
    private void paintLoading() {
        resetTextStyle();
        String wait = "加载中,请稍候...";
        mPaint.setmFontSize(30 * metrics().DPI / 160);
        mPaint.setTextColor(getTextColor());
        // 绘制加载中
        GBSize size = getPaintAreaSize();
        mPaint.drawString((size.mWidth - mPaint.getStrWidth(wait)) / 2, (size.mHeight - mPaint.getStringHeight()) / 2,
                wait);
    }

    /**
     * 功能描述： 渲染书名和目录标题<br>
     * 创建者： jack<br>
     * 创建日期：2015-1-20<br>
     *
     * @param
     */
    private void paintTitle(GBTextPage page) {
        if (isShowTitle()) {
            resetTextStyle();
            float dpi = (float) metrics().DPI / 160;
            mPaint.setmFontSize(14 * dpi);
            mPaint.setTextColor(getPageTitleColor());
            String chapName = getChapterName(page);
            int nameWidth = mPaint.getStrWidth(getBookName());
            int chapWidth = mPaint.getStrWidth(chapName);
            int y = (int) (20 * dpi + 0.5f); // 内容高度：字体大小+0.5f
            if (nameWidth + chapWidth < getTextAreaWidth())
                mPaint.drawString(getLeftMargin(), y, getBookName());
            int x = Math.max(getLeftMargin(), getTextAreaWidth() + getLeftMargin() - chapWidth);
            mPaint.drawString(x, y, chapName);
        }
    }

    protected abstract boolean isShowTitle();

    protected abstract String getBookName();

    protected abstract String getChapterName(GBTextPage page);

    /**
     * 功能描述： 重新加载样式<br>
     * 重新加载样式加载时间点有三个：1，刚开始计算页面内容生成行信息（GBTextLineInfo）时。<br>
     * 2，根据行信息计算生成元素区域信息（GBTextElementArea）时。<br>
     * 3，根据元素区域信息进行渲染时。<br>
     * 创建者： jack<br>
     * 创建日期：2013-11-4<br>
     */
    private void reloadStyleInfo(GBTextParagraphCursor paragraphCursor, int endElementIndex) {
        // 处理分页后段的内部样式失效问题 add by jack
        // if (mStyleChapIndex == paragraphCursor.chpFileIndex &&
        // mStyleParaIndex == paragraphCursor.Index)
        // return;
        resetTextStyle();
        mCssProvider.loadCssStyle(paragraphCursor.chpFileIndex, paragraphCursor.Index);
        if (paragraphCursor.kind != GBTextParagraph.Kind.CSS_PARAGRAPH && endElementIndex > 0)
            applyStyleChanges(paragraphCursor, 0, endElementIndex);// 应用样式
    }

    private GBTextPage getPage(PageIndex pageIndex) {
        switch (pageIndex) {
            default:
            case CURRENT:
                return myCurrentPage;
            case PREVIOUS:
                return myPreviousPage;
            case NEXT:
                return myNextPage;
        }
    }

    public static final int SCROLLBAR_HIDE = 0;
    public static final int SCROLLBAR_SHOW = 1;
    public static final int SCROLLBAR_SHOW_AS_PROGRESS = 2;

    public abstract int scrollbarType();

    public final boolean isScrollbarShow() {
        return scrollbarType() == SCROLLBAR_SHOW || scrollbarType() == SCROLLBAR_SHOW_AS_PROGRESS;
    }

    /**
     * 功能描述： 获取上一个段落长度<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-5<br>
     *
     * @param chpFileIndex   章文件索引
     * @param paragraphIndex 段落索引
     * @return
     */
    protected final synchronized int sizeOfTextBeforeParagraph(int chpFileIndex, int paragraphIndex) {
        if (chpFileIndex < 0 || paragraphIndex < 0)
            return 0;
        if (paragraphIndex == 0 && chpFileIndex > 0)
            return myModel != null ? myModel.getTextLength(chpFileIndex - 1,
                    myModel.getParagraphsNumber(chpFileIndex - 1)) : 0;
        else if (paragraphIndex == 0 && chpFileIndex == 0)
            return 0;
        else
            return myModel != null ? myModel.getTextLength(chpFileIndex, paragraphIndex - 1) : 0;
    }

    /**
     * 功能描述：获取图书总字符数 <br>
     * 创建者： jack<br>
     * 创建日期：2013-12-31<br>
     *
     * @return
     */
    protected final synchronized int sizeOfFullText() {
        if (myModel == null || myModel.getTotalParagraphsNumber() == 0 || !myModel.isLoadBookOver()) {
            return 0;
        }
        return myModel.getTextLength(myModel.getChapterSize() - 1,
                myModel.getParagraphsNumber(myModel.getChapterSize() - 1) - 1);
    }

    /**
     * 功能描述： 获取阅读页的字符位置<br>
     * 创建者： jack<br>
     * 创建日期：2013-12-31<br>
     *
     * @param pageIndex         阅读页索引
     * @param startNotEndOfPage true 页开始位置 false 页结束位置
     * @return
     */
    private final synchronized int getCurrentCharNumber(PageIndex pageIndex, boolean startNotEndOfPage) {
        if (myModel == null || myModel.getTotalParagraphsNumber() == 0) {
            return 0;
        }
        final GBTextPage page = getPage(pageIndex);
        if (page.StartCursor.isNull())
            return 0;
        preparePaintInfo(page, false);

        // if (page.PaintState == PaintStateEnum.WAITING)
        // return 0;

        if (startNotEndOfPage) {
            return Math.max(0, sizeOfTextBeforeCursor(page.StartCursor));
        } else {
            int end = sizeOfTextBeforeCursor(page.EndCursor);
            if (end == -1) {
                end = myModel.getTextLength(myModel.getChapterSize() - 1,
                        myModel.getParagraphsNumber(myModel.getChapterSize() - 1) - 1) - 1;
            }
            return Math.max(1, end);
        }
    }

    @Override
    public final synchronized int getScrollbarFullSize() {
        return sizeOfFullText();
    }

    @Override
    public final synchronized int getScrollbarThumbPosition(PageIndex pageIndex) {
        return scrollbarType() == SCROLLBAR_SHOW_AS_PROGRESS ? 0 : getCurrentCharNumber(pageIndex, true);
    }

    @Override
    public final synchronized int getScrollbarThumbLength(PageIndex pageIndex) {
        int start = scrollbarType() == SCROLLBAR_SHOW_AS_PROGRESS ? 0 : getCurrentCharNumber(pageIndex, true);
        int end = getCurrentCharNumber(pageIndex, false);
        return Math.max(1, end - start);
    }

    private int sizeOfTextBeforeCursor(GBTextWordCursor wordCursor) {
        final GBTextParagraphCursor paragraphCursor = wordCursor.getParagraphCursor();
        if (paragraphCursor == null) {
            return -1;
        }
        final int paragraphIndex = paragraphCursor.Index;
        final int chpFileIndex = paragraphCursor.chpFileIndex;
        int sizeOfText = myModel.getTextLength(chpFileIndex, paragraphIndex - 1);
        final int paragraphLength = paragraphCursor.getParagraphLength();
        if (paragraphLength > 0) {
            sizeOfText += (myModel.getTextLength(chpFileIndex, paragraphIndex) - sizeOfText)
                    * wordCursor.getElementIndex() / paragraphLength;
        }
        return sizeOfText;
    }

    // Can be called only when (myModel.getParagraphsNumber() != 0)

    /**
     * 功能描述： 获取平均每页的字符数<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-8<br>
     *
     * @return
     */
    private synchronized float computeCharsPerPage() {
        if (myModel == null || myModel.getTotalParagraphsNumber() == 0 || !myModel.isLoadBookOver()) {
            return 1;
        }
        setTextStyle(GBTextStyleCollection.Instance().getBaseStyle());

        final int textWidth = getTextAreaWidth();
        final int textHeight = getTextAreaHeight();

        final int num = myModel.getTotalParagraphsNumber();
        final int totalTextSize = sizeOfFullText();// myModel.getTextLength(num
        // - 1);
        final float charsPerParagraph = ((float) totalTextSize) / num;

        final float charWidth = computeCharWidth();

        final int indentWidth = getElementWidth(GBTextElement.Indent, 0);
        final float effectiveWidth = textWidth - (indentWidth + 0.5f * textWidth) / charsPerParagraph;
        float charsPerLine = Math.min(effectiveWidth / charWidth, charsPerParagraph * 1.2f);

        final int strHeight = getWordHeight() + mPaint.getDescent();
        final int effectiveHeight = (int) (textHeight - (getTextStyle().getSpaceBefore() + getTextStyle()
                .getSpaceAfter()) / charsPerParagraph);
        final int linesPerPage = effectiveHeight / strHeight;

        return charsPerLine * linesPerPage;
    }

    /**
     * 功能描述： 根据阅读字符数目计算页码数<br>
     * 创建者： jack<br>
     * 创建日期：2013-12-31<br>
     *
     * @param textSize
     * @return
     */
    private synchronized int computeTextPageNumber(int textSize) {
        if (myModel == null || myModel.getTotalParagraphsNumber() == 0 || !myModel.isLoadBookOver()) {
            return 1;
        }

        final float factor = 1.0f / computeCharsPerPage();
        final float pages = textSize * factor;
        return Math.max((int) (pages + 1.0f - 0.5f * factor), 1);
    }

    /**
     * 获取当前阅读进度所在章节页码
     *
     * @author yangn
     * @return返回cursor所描述位置所在页码
     */
    public synchronized int getCurrnetPageNumInChp() {

        return getChpInPageNum(getStartCursor().getChpFileIndex(), getStartCursor().getParagraphIndex());
    }

    /**
     * 获取某章总页码
     *
     * @param //cursor
     * @return 返回cursor所描述章总页码
     * @author yangn
     */
    public synchronized int getChpTotalPageNum() {
        if (myModel == null || myModel.getTotalParagraphsNumber() == 0) {
            return 1;
        }
        return getChpInPageNum(myCurrentPage.StartCursor.getChpFileIndex(),
                myModel.getParagraphsNumber(myCurrentPage.StartCursor.getChpFileIndex()));
    }

    /**
     * 计算某章内某段页码数
     *
     * @param chpFileIndex 章节索引
     * @param //所在段
     * @return 返回 chpFileIndex所描述章节从页数
     * @author yangn
     */
    public synchronized int getChpInPageNum(int chpFileIndex, int endIndex) {
        if (myModel == null || myModel.getTotalParagraphsNumber() == 0) {
            return 1;
        }
        int start = myModel.getTextLength(chpFileIndex, 0);
        int end = myModel.getTextLength(chpFileIndex, endIndex);
        int realTextSize = end - start;
        return computeTextPageNumber(realTextSize);
    }

    private static final char[] ourDefaultLetters = "System developers have used modeling languages for decades to specify, visualize, construct, and document systems. The Unified Modeling Language (UML) is one of those languages. UML makes it possible for team members to collaborate by providing a common language that applies to a multitude of different systems. Essentially, it enables you to communicate solutions in a consistent, tool-supported language."
            .toCharArray();

    private final char[] myLettersBuffer = new char[512];
    private int myLettersBufferLength = 0;
    private GBTextModel myLettersModel = null;
    private float myCharWidth = -1f;

    /**
     * 功能描述： 计算字符宽度<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-5<br>
     *
     * @return
     */
    private final float computeCharWidth() {
        if (myLettersModel != myModel) {
            myLettersModel = myModel;
            myLettersBufferLength = 0;
            myCharWidth = -1f;

            int chpFileIndex = myCurrentPage.StartCursor.getChpFileIndex() - 1;
            chpFileIndex = chpFileIndex >= 0 ? chpFileIndex : 0;

            int paragraph = 0;

            while (chpFileIndex < myCurrentPage.StartCursor.getChpFileIndex() + 1
                    && paragraph < myModel.getParagraphsNumber(chpFileIndex)
                    && myLettersBufferLength < myLettersBuffer.length) {
                if (!mApplication.isLoadBookChp(chpFileIndex)) {
                    paragraph = 0;
                    chpFileIndex++;
                    continue;
                }
                GBTextParagraph.EntryIterator it = myModel.getParagraph(chpFileIndex, paragraph++).iterator();
                while (it.hasNext() && myLettersBufferLength < myLettersBuffer.length) {
                    it.next();
                    if (it.getType() == GBTextParagraph.Entry.TEXT) {
                        final int len = Math.min(it.getTextLength(), myLettersBuffer.length - myLettersBufferLength);
                        System.arraycopy(it.getTextData(), it.getTextOffset(), myLettersBuffer, myLettersBufferLength,
                                len);
                        myLettersBufferLength += len;
                    }
                }
                if (paragraph >= myModel.getParagraphsNumber(chpFileIndex)) {
                    paragraph = 0;
                    chpFileIndex++;
                }
            }

            if (myLettersBufferLength == 0) {
                myLettersBufferLength = Math.min(myLettersBuffer.length, ourDefaultLetters.length);
                System.arraycopy(ourDefaultLetters, 0, myLettersBuffer, 0, myLettersBufferLength);
            }
        }

        if (myCharWidth < 0f) {
            myCharWidth = computeCharWidth(myLettersBuffer, myLettersBufferLength);
        }
        return myCharWidth;
    }

    private final float computeCharWidth(char[] pattern, int length) {
        return mPaint.getCharArrWidth(pattern, 0, length) / ((float) length);
    }

    public static class PagePosition {
        public final int Current;
        public final int Total;

        PagePosition(int current, int total) {
            Current = current;
            Total = total;
        }
    }

    /**
     * 获取阅读进度百分比
     *
     * @return 阅读进度百分比
     * @author yangn
     */
    public final synchronized double getReadProgressPercent() {
        int current = computeTextPageNumber(getCurrentCharNumber(PageIndex.CURRENT, false));
        int sizeOfFullText = sizeOfFullText();
        if (sizeOfFullText == 0) {
            return 0;
        }
        int total = computeTextPageNumber(sizeOfFullText);
        double percent = (float) current / total * 100;
        return percent;
    }

    /**
     * 功能描述：获取总数的阅读页码<br>
     * 创建者： jack<br>
     * 创建日期：2013-12-31<br>
     *
     * @return
     */
    public final synchronized PagePosition pagePosition() {
        if (myModel == null || !myModel.isLoadBookOver())
            return new PagePosition(0, 0);
        try {
            int current = computeTextPageNumber(getCurrentCharNumber(PageIndex.CURRENT, false));
            int total = computeTextPageNumber(sizeOfFullText());

            if (total > 3) {
                return new PagePosition(current, total);
            }

            preparePaintInfo(myCurrentPage, false);
            GBTextWordCursor cursor = myCurrentPage.StartCursor;
            if (cursor == null || cursor.isNull()) {
                return new PagePosition(current, total);
            }

            if (cursor.isStartOfText()) {
                current = 1;
            } else {
                GBTextWordCursor prevCursor = myPreviousPage.StartCursor;
                if (prevCursor == null || prevCursor.isNull()) {
                    preparePaintInfo(myPreviousPage, false);
                    prevCursor = myPreviousPage.StartCursor;
                }
                if (prevCursor != null && !prevCursor.isNull()) {
                    current = prevCursor.isStartOfText() ? 2 : 3;
                }
            }

            total = current;
            cursor = myCurrentPage.EndCursor;
            if (cursor == null || cursor.isNull()) {
                return new PagePosition(current, total);
            }
            if (!cursor.isEndOfText()) {
                GBTextWordCursor nextCursor = myNextPage.EndCursor;
                if (nextCursor == null || nextCursor.isNull()) {
                    preparePaintInfo(myNextPage, false);
                    nextCursor = myNextPage.EndCursor;
                }
                if (nextCursor != null) {
                    total += nextCursor.isEndOfText() ? 1 : 2;
                }
            }

            return new PagePosition(current, total);
        } catch (Exception ex) {
            return new PagePosition(-1, 0);
        }
    }

    @Override
    public int getTotalPage() {
        if (myModel == null || !myModel.isLoadBookOver())
            return 0;
        else
            return computeTextPageNumber(sizeOfFullText());
    }

    /**
     * 功能描述：条转页 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-19<br>
     *
     * @param page 被跳转页
     */
    public final synchronized void gotoPage(int page) {
        if (myModel == null || myModel.getTotalParagraphsNumber() == 0) {
            return;
        }
        final float factor = computeCharsPerPage();
        final float textSize = page * factor;

        int intTextSize = (int) textSize;
        PositionInfo paragraphIndex = myModel.findParagraphByTextLength(intTextSize);

        if ((paragraphIndex.mChpFileIndex > 0 || paragraphIndex.mParagraphIndex > 0)
                && myModel.getTextLength(paragraphIndex.mChpFileIndex, paragraphIndex.mParagraphIndex) > intTextSize) {
            paragraphIndex = myModel.preParagraph(paragraphIndex);
        }

        intTextSize = myModel.getTextLength(paragraphIndex.mChpFileIndex, paragraphIndex.mParagraphIndex);
        PositionInfo pre = myModel.preParagraph(paragraphIndex);
        int sizeOfTextBefore = myModel.getTextLength(pre.mChpFileIndex, pre.mParagraphIndex);

        while ((paragraphIndex.mChpFileIndex > 0 || paragraphIndex.mParagraphIndex > 0)
                && intTextSize == sizeOfTextBefore) {
            paragraphIndex = pre;
            intTextSize = sizeOfTextBefore;
            pre = myModel.preParagraph(paragraphIndex);
            sizeOfTextBefore = myModel.getTextLength(pre.mChpFileIndex, pre.mParagraphIndex);
        }

        final int paragraphLength = intTextSize - sizeOfTextBefore;

        final int wordIndex;
        if (paragraphLength == 0) {
            wordIndex = 0;
        } else {
            preparePaintInfo(myCurrentPage, false);
            if (mApplication.isLoadBookChp(paragraphIndex.mChpFileIndex)) {
                final GBTextWordCursor cursor = new GBTextWordCursor(myCurrentPage.EndCursor);
                cursor.moveToParagraph(paragraphIndex.mChpFileIndex, paragraphIndex.mParagraphIndex);
                wordIndex = cursor.getParagraphCursor().getParagraphLength();
            } else {
                wordIndex = 0;
            }
        }

        gotoPositionByEnd(paragraphIndex.mChpFileIndex, paragraphIndex.mParagraphIndex, wordIndex, 0);
    }

    /**
     * 回到第一页 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-19<br>
     *
     * @param
     */
    public void gotoHome() {
        final GBTextWordCursor cursor = getStartCursor();
        if (!cursor.isNull() && cursor.isStartOfParagraph() && cursor.getParagraphIndex() == 0
                && cursor.getChpFileIndex() == 0) {
            return;
        }
        gotoPosition(0, 0, 0, 0);
        preparePaintInfo();
    }

    /**
     * 功能描述： 绘制一个背景效果<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-23<br>
     *
     * @param highligting 高亮文本信息
     * @param color       背景色
     * @param page        当前页面
     * @param info        行信息
     * @param from        行第一个字在该页开始下标
     * @param to          行最后一个字在该页开始下标
     * @param y           顶边距
     */
    private void drawBackgroung(GBTextAbstractHighlighting highligting, GBColor color, GBTextPage page,
                                GBTextLineInfo info, int from, int to, int y, boolean isLeft) {
        if (!highligting.isEmpty() && from != to) {
            final GBTextElementArea fromArea = page.TextElementMap.get(from);
            final GBTextElementArea toArea = page.TextElementMap.get(to - 1);
            // 适配其他平台进度
            if (highligting.getStartPosition().getElementIndex() == -1
                    || highligting.getStartPosition().getCharIndex() == -1) {
                int[] SPosi = MiscUtil.convertChpInWordNumToInternalPosition(myModel, highligting.getStartPosition());
                int[] EPosi = MiscUtil.convertChpInWordNumToInternalPosition(myModel, highligting.getEndPosition());
                highligting.setup(new GBTextFixedPosition(highligting.getStartPosition().getChpFileIndex(), SPosi[0],
                        SPosi[1], SPosi[2]), new GBTextFixedPosition(highligting.getEndPosition().getChpFileIndex(),
                        EPosi[0], EPosi[1], EPosi[2]));
            }

            final GBTextElementArea selectionStartArea = highligting.getStartArea(page);
            final GBTextElementArea selectionEndArea = highligting.getEndArea(page);
            if (selectionStartArea != null && selectionEndArea != null && selectionStartArea.compareTo(toArea) <= 0
                    && selectionEndArea.compareTo(fromArea) >= 0) {

                // 计算文本行区域
                final int top = y + 1;
                int left, right, bottom = top + info.Height - (info.isEndOfParagraph() ? info.mBottom : 0)
                        + info.Descent;
                if (selectionStartArea.compareTo(fromArea) < 0) {
                    left = info.mIsTrLine ? info.mXStartOffset : getLeftMargin() + info.mLeft + info.LeftIndent;
                } else {
                    left = selectionStartArea.XStart;
                }
                if (selectionEndArea.compareTo(toArea) > 0) {
                    right = toArea.XEnd; // : getRightLine();
                    bottom += info.VSpaceAfter;
                } else {
                    right = selectionEndArea.XEnd;
                }
                mPaint.setFillColor(color);
                // 记录当前页中的高亮信息
                if (highligting instanceof GBTextAnnotation) {
                    final GBTextAnnotation ann = (GBTextAnnotation) highligting;
                    setHighLightModel(page, ann);
                    page.myAnnotationList.add(ann);
                    // 绘制批注下划线&& selectionEndArea.XEnd == right&&
                    // selectionEndArea.YEnd == bottom
                    mPaint.fillRectangle(left, bottom - 1, right, bottom + 1);
                    if (ann.getmContent() != null && !ann.getmContent().equals("")) {
                        drawNotes(mPaint, selectionEndArea.XEnd, selectionEndArea.YEnd);
                    }
                } else if (highligting instanceof GBTextHighlighting) {
                    final GBTextHighlighting high = (GBTextHighlighting) highligting;
                    setHighLightModel(page, high);
                    page.myHighlightingList.add((GBTextHighlighting) highligting);
                    // 绘制背景色
                    mPaint.fillRectangle(left, top + 3, right, bottom - 1);
                } else {
                    mPaint.fillRectangle(left, top + 3, right, bottom - 1);
                }
            }
        }
    }

    private void setHighLightModel(GBTextPage page, GBTextHighlighting highlighting) {
        if (GBLibrary.Instance().DoublePageOption.getValue()) {
            final GBTextElementArea elem = page.TextElementMap.get(page.tempElementIndex - 1);
            if (elem.compareTo(highlighting.getStartPosition()) <= 0) {
                highlighting.setmLightModel(LIGHTMODEL.RIGHT);
            } else if (elem.compareTo(highlighting.getEndPosition()) >= 0) {
                highlighting.setmLightModel(LIGHTMODEL.LEFT);
            } else {
                highlighting.setmLightModel(LIGHTMODEL.BRIDGE);
            }
        } else {
            highlighting.setmLightModel(LIGHTMODEL.LEFT);
        }
    }

    private static final char[] SPACE = new char[]{' '};

    /**
     * 功能描述： 绘制一行文本<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-23<br>
     *
     * @param page 当前页信息
     * @param info 行内容信息
     * @param from 行第一个字在该页开始下标
     * @param to   行最后一个字在该页开始下标
     * @param y    顶边距
     */
    private void drawTextLine(GBTextPage page, GBTextLineInfo info, int from, int to, int y, boolean isLeft) {
        // L.i("drawTextLine", "is:" + info.Height + "--" + info.toString() +
        // "---" + y);
        if (info.isRealLine) {
            // hr 横线绘制
            mPaint.setFillColor(getTextColor());
            mPaint.fillRectangle(getLeftMargin(), y + 1, getRightLine(), y + info.Height + 1);
            return;
        }
        mCssProvider.drawCssBgBorder(page, info, 0, y);
        // 绘制语音阅读行背景
        if (page.mReadLine != null && info == page.mReadLine) {
            mPaint.setFillColor(getReadTextColor());
            mPaint.fillRectangle(getLeftMargin(), y + 1, getRightLine(), y + info.Height - info.mBottom + info.Descent
                    - 1);
        }
        // 绘制文本选中色
        drawBackgroung(mySelection, getSelectedBackgroundColor(), page, info, from, to, y, isLeft);
        // 根据高亮位置信息绘制高亮背景
        for (int i = 0; i < myHighlightingList.size(); i++) {
            drawBackgroung(myHighlightingList.get(i), getSelectedBackgroundColor(), page, info, from, to, y, isLeft);
        }

        // tr行文字大小适配
        if (info.mIsTrLine && info.mTrFontSize != -1)
            mPaint.setmFontSize(info.mTrFontSize);

        final GBPaint context = mPaint;
        final GBTextParagraphCursor paragraph = info.ParagraphCursor;
        int index = from;
        final int endElementIndex = info.EndElementIndex;
        int charIndex = info.RealStartCharIndex;

        int wordIndex = info.RealStartElementIndex;
        for (; wordIndex != endElementIndex && index < to; ++wordIndex, charIndex = 0) {
            final GBTextElement element = paragraph.getElement(wordIndex);// 获取第一个元素
            final GBTextElementArea area = page.TextElementMap.get(index);// get
            // 添加内部样式
            if (isStyleChangeElement(element)) {
                applyStyleChangeElement(element);
                // 检查绘制背景
                if (mStyleStack.size() > 0 && mStyleStack.peek() instanceof GBTextCssDecoratedStyle) {
                    GBTextCssDecoratedStyle css = (GBTextCssDecoratedStyle) mStyleStack.peek();
                    if (!css.isBackgroundStyleNull() || !css.isBorderStyleNull()) {
                        mCssProvider.drawCssBgBorder(page, info, area.XStart, y);
                    }
                }
                continue;
            }

            if (element == area.Element) {
                ++index;
                final int areaX = area.XStart;
                final int areaY = area.YEnd - getElementDescent(element) - getTextStyle().getVerticalShift();
                if (element instanceof GBTextWord) {
                    drawWord(areaX, areaY, (GBTextWord) element, charIndex, -1, false, mySelection.isAreaSelected(area)
                            ? getSelectedForegroundColor()
                            : getTextColor());

                    // getTextColor(getTextStyle().Hyperlink)

                } else if (element instanceof GBTextImageElement) {
                    final GBTextImageElement imageElement = (GBTextImageElement) element;
                    if (imageElement.IsCover) {
                        context.drawSingleColor(context.getAverageColor(imageElement.ImageData, getTextAreaSize(),
                                getScalingType(imageElement)));
                    }
                    context.drawImage(areaX, areaY, imageElement.ImageData, imageElement.IsCover
                            ? getPaintAreaSize()
                            : getTextAreaSize(), getScalingType(imageElement));
                } else if (element == GBTextElement.HSpace) {
                    final int cw = context.getSpaceWidth();
                    /*
                     * context.setFillColor(getHighlightingColor());
                     * context.fillRectangle( area.XStart, areaY -
                     * context.getStringHeight(), area.XEnd - 1, areaY +
                     * context.getDescent() );
                     */
                    for (int len = 0; len < area.XEnd - area.XStart; len += cw) {
                        context.drawString(areaX + len, areaY, SPACE, 0, 1);
                    }
                } else if (element instanceof GBAudioElement) {
                    ((GBAudioElement) element).isLeft = isLeft;
                    drawAudioBG(mPaint, area.XStart, area.YStart, area.XEnd, area.YEnd);
                } else if (element instanceof GBNoteElement) {
//                	((GBNoteElement) element).isLeft = isLeft;
//                	drawNotes(mPaint, areaX, areaY);
                    drawNotesBG(mPaint, area.XStart, area.YStart, areaX, areaY);
                } else if (element instanceof GBVideoElement) {
                    ((GBVideoElement) element).isLeft = isLeft;
                    drawVideoBG(mPaint, area.XStart, area.YStart, area.XEnd, area.YEnd);
                } else if (element instanceof GBAnimObjElement) {
                    ((GBAnimObjElement) element).isLeft = isLeft;
                    drawAnimBG(mPaint, area.XStart, area.YStart, area.XEnd, area.YEnd);
                }
            }
        }
        if (index != to) {
            GBTextElementArea area = page.TextElementMap.get(index++);
            if (area.ChangeStyle) {
                setTextStyle(area.Style);
            }
            final int start = info.StartElementIndex == info.EndElementIndex ? info.StartCharIndex : 0;
            final int len = info.EndCharIndex - start;
            final GBTextWord word = (GBTextWord) paragraph.getElement(info.EndElementIndex);
            drawWord(area.XStart, area.YEnd - context.getDescent() - getTextStyle().getVerticalShift(), word, start,
                    len, area.AddHyphenationSign, mySelection.isAreaSelected(area)
                            ? getSelectedForegroundColor()
                            : getTextColor());
        }

        // 末尾样式移除处理
        for (; wordIndex != endElementIndex; ++wordIndex, charIndex = 0) {
            final GBTextElement element = paragraph.getElement(wordIndex);// 获取第一个元素
            // 添加内部样式支持
            if (isStyleChangeElement(element)) {
                applyStyleChangeElement(element);
                continue;
            }
        }
        // 绘制批注
        for (int j = 0; j < myAnnotationList.size(); j++) {
            drawBackgroung(myAnnotationList.get(j), getHighlightingColor(), page, info, from, to, y, isLeft);
        }
    }

    /**
     * 功能描述： 生成页面中的行信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-11<br>
     *
     * @param page           页
     * @param start          该页第一个文字
     * @param result         该页最后一个文字
     * @param isNeedRefreash 是否需要页面重绘
     */
    private void buildInfos(GBTextPage page, GBTextWordCursor start, GBTextWordCursor result, boolean isNeedRefreash) {
        if (isPaintLoading)
            return;
        // L.i("GBTextview", "BuildInfo" + start.getChpFileIndex() + "--" +
        // start.getParagraphIndex() + "--"
        // + start.getParagraphCursor().toString());
        resetTextStyle();// 设置为默认样式

        page.tempIndex = -1;
        page.tempCursor.reset();

        result.setCursor(start);
        int textAreaHeight = getTextAreaHeight();
        page.LineInfos.clear();
        int counter = 0;
        boolean isDouble = GBLibrary.Instance().DoublePageOption.getValue();
        do {

            // 获取文字所在段信息
            GBTextParagraphCursor paragraphCursor = result.getParagraphCursor();
            // 获取当前文字所在元素索引
            int wordOfElementIndex = result.getElementIndex();
            // 给该元素应用样式
            reloadStyleInfo(paragraphCursor, wordOfElementIndex);

            if (paragraphCursor.isTreeParagraph()) {
                if (result.isEndOfParagraph())
                    continue;
                // tr特殊段处理
                int trHeight = processTrTextLine(paragraphCursor);
                if (trHeight <= 0) {
                    // 如果为空表格行TODO
                    result.moveToParagraphEnd();
                    continue;
                }
                if (textAreaHeight < getTextAreaHeight() && textAreaHeight < trHeight) {
                    // 当页面剩余空间不够绘制tr行时，结束生成当前页
                    if (!isDouble) {
                        break;
                    } else {
                        isDouble = false;
                        textAreaHeight = getTextAreaHeight();
                        page.tempCursor.setCursor(paragraphCursor);
                        page.tempCursor.moveToParagraph(paragraphCursor.chpFileIndex, paragraphCursor.Index);
                        page.tempCursor.moveTo(0, 0);
                        page.tempIndex = page.LineInfos.size();

                        result.moveToParagraphEnd();
                        textAreaHeight -= trHeight + mTrTextLine.get(mTrTextLine.size() - 1).Descent;
                        page.LineInfos.addAll(mTrTextLine);
                        counter++;
                        continue;
                    }
                } else if (textAreaHeight == getTextAreaHeight() && textAreaHeight <= trHeight) {
                    // 空白页刚好绘制tr行
                    page.LineInfos.addAll(mTrTextLine);
                    result.moveToParagraphEnd();
                    if (isDouble) {
                        isDouble = false;
                        page.tempCursor.setCursor(paragraphCursor);
                        page.tempCursor.moveToParagraph(paragraphCursor.chpFileIndex, paragraphCursor.Index);
                        page.tempCursor.moveTo(0, 0);
                        page.tempIndex = page.LineInfos.size();
                        counter++;
                        continue;
                    } else {
                        result.nextParagraph(this, isNeedRefreash);
                        break;
                    }
                } else {
                    page.LineInfos.addAll(mTrTextLine);
                    textAreaHeight -= trHeight + mTrTextLine.get(mTrTextLine.size() - 1).Descent;
                    result.moveTo(paragraphCursor.getParagraphLength(), 0);
                    counter++;
                }

            } else {
                // 正常处理段落
                GBTextLineInfo info = new GBTextLineInfo(paragraphCursor, wordOfElementIndex, result.getCharIndex(),
                        getTextStyle());
                final int currentParagraphElementSize = info.ParagraphCursorLength;
                // 遍历行所有元素
                while (info.EndElementIndex != currentParagraphElementSize) {
                    //
                    info = processTextLine(paragraphCursor, info.EndElementIndex, info.EndCharIndex,
                            currentParagraphElementSize);
                    textAreaHeight -= info.mTop + info.Height + info.Descent;// 计算当前行屏幕y
                    if (textAreaHeight < 0 && counter > 0) {
                        // 当多行填充满一页时
                        if (!isDouble) {
                            break;
                        } else {
                            isDouble = false;
                            textAreaHeight = getTextAreaHeight();
                            page.tempCursor.setCursor(info.ParagraphCursor);
                            page.tempCursor.moveToParagraph(info.ParagraphCursor.chpFileIndex,
                                    info.ParagraphCursor.Index);
                            page.tempCursor.moveTo(info.EndElementIndex, info.EndCharIndex);
                            page.tempIndex = page.LineInfos.size();

                            result.moveTo(info.EndElementIndex, info.EndCharIndex);// 更新最后一个文字位置
                            textAreaHeight -= info.Height + info.Descent;
                            page.LineInfos.add(info);
                            if (info.IsVisible)
                                counter++;
                            continue;
                        }
                    }
                    textAreaHeight -= info.VSpaceAfter;
                    result.moveTo(info.EndElementIndex, info.EndCharIndex);// 更新最后一个文字位置
                    page.LineInfos.add(info);
                    if (textAreaHeight < 0) {
                        // 当一行填充满一页时
                        if (!isDouble) {
                            break;
                        } else {
                            isDouble = false;
                            textAreaHeight = getTextAreaHeight();
                            page.tempCursor.setCursor(info.ParagraphCursor);
                            page.tempCursor.moveToParagraph(info.ParagraphCursor.chpFileIndex,
                                    info.ParagraphCursor.Index);
                            page.tempCursor.moveTo(info.EndElementIndex, info.EndCharIndex);
                            page.tempIndex = page.LineInfos.size();
                            counter = 0;
                            continue;
                        }
                    }
                    if (info.IsVisible)
                        counter++;
                }
            }

        } while (result.isEndOfParagraph()
                && result.nextParagraph(this, isNeedRefreash)
                && (isSlipByChapter() ? !result.getParagraphCursor().isEndOfSection() : !result.getParagraphCursor()
                .isTxtCatalog() || textAreaHeight == getTextAreaHeight()) && (textAreaHeight >= 0));
        resetTextStyle();
    }

    /*
     * 是否自动断字
     */
    private boolean isHyphenationPossible() {
        return GBTextStyleCollection.Instance().getBaseStyle().AutoHyphenationOption.getValue()
                && getTextStyle().allowHyphenations();
    }

    /**
     * 功能描述： 计算生成行内容<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-22<br>
     *
     * @param paragraphCursor             段指针
     * @param startElementIndex           段开始元素索引
     * @param startCharIndex              元素开始字符索引
     * @param currentParagraphElementSize 该段元素总长度
     */
    private GBTextLineInfo processTextLine(GBTextParagraphCursor paragraphCursor, final int startElementIndex,
                                           final int startCharIndex, final int currentParagraphElementSize) {
        final GBPaint context = mPaint;
        // 生成新行
        final GBTextLineInfo info = new GBTextLineInfo(paragraphCursor, startElementIndex, startCharIndex,
                getTextStyle());

        final GBTextLineInfo cachedInfo = myLineInfoCache.get(info);
        if (cachedInfo != null) {// 有缓存返回缓存行
            applyStyleChanges(paragraphCursor, startElementIndex, cachedInfo.EndElementIndex);
            return cachedInfo;
        }

        int currentElementIndex = startElementIndex;// 开始元素索引
        int currentCharIndex = startCharIndex;// 开始字符索引
        final boolean isFirstLine = startElementIndex == 0 && startCharIndex == 0;

        // 判断是否是第一行
        if (isFirstLine) {
            // 获取当前元素
            GBTextElement element = paragraphCursor.getElement(currentElementIndex);
            // 1若当前元素为样式元素或标签元素则为元素添加样式信息 然后获取下一个元素 继续第1步
            while (isStyleChangeElement(element)) {
                // hr 标签处理
                if (element instanceof GBTextControlElement && ((GBTextControlElement) element).Kind == 48) {
                    info.Height = 1;
                    info.isRealLine = true;
                }
                applyStyleChangeElement(element);
                ++currentElementIndex;
                currentCharIndex = 0;
                if (currentElementIndex == currentParagraphElementSize) {
                    break;
                }
                element = paragraphCursor.getElement(currentElementIndex);
            }
            // useCssStyle(paragraphCursor.Index);
            info.StartStyle = getTextStyle();
            info.RealStartElementIndex = currentElementIndex;
            info.RealStartCharIndex = currentCharIndex;
            if (paragraphCursor.wordNum > 0)
                info.mTop = getTextStyle().getParaSpace();
        }

        GBTextStyle storedStyle = getTextStyle();
        // 获取行的左边距
        // info.LeftIndent = storedStyle.getLeftIndent();
        // int textMorePad = info.LeftIndent;
        if (isFirstLine) {
            info.LeftIndent += mCssProvider.getFristLineIndent();// getTextStyle().getFirstLineIndentDelta();
        }

        info.Width = info.LeftIndent;

        // 记录行位置跳出
        if (info.RealStartElementIndex == currentParagraphElementSize) {
            info.EndElementIndex = info.RealStartElementIndex;
            info.EndCharIndex = info.RealStartCharIndex;
            return info;
        }
        // 注入的版权页忽略样式
        boolean isAddCssHeight = (paragraphCursor.chpFileIndex == 1
                ? (isFirstLine && !myModel.isHaveCopyRight())
                : isFirstLine);
        int newWidth = info.Width;
        int h = mCssProvider.getLineHeightAndInitMP(info, isFirstLine);
        int newHeight = isAddCssHeight ? h : info.Height;//
        // add by jack 添加左边框
        // if (isFirstLine)
        // info.LeftIndent += info.mLeft;
        newWidth += info.mLeft;

        int newDescent = info.Descent;
        int maxWidth = getTextAreaWidth() - info.mRight;// getRightindent()
        // -
        // info.mBorderRight;
        // -
        // 4;//
        // 获取一行最大宽度（行左边距和左边框已经初始化）
        boolean wordOccurred = false;
        boolean isVisible = false;
        int lastSpaceWidth = 0;// 最后一个空格宽度
        int internalSpaceCounter = 0;// 空格数量
        boolean removeLastSpace = false;

        int wordCount = 0;
        do {
            GBTextElement element = paragraphCursor.getElement(currentElementIndex);// 获取下一个元素
            newWidth += getElementWidth(element, currentCharIndex);// 计算宽
            // 计算高
            newHeight = Math.max(newHeight, getElementHeight(element));
            newDescent = Math.max(newDescent, getElementDescent(element)); // 计算获取下边距
            if (element == GBTextElement.HSpace) {// 空格元素
                if (wordOccurred) {
                    wordOccurred = false;
                    internalSpaceCounter++;
                    lastSpaceWidth = context.getSpaceWidth();
                    newWidth += lastSpaceWidth;
                }
            } else if (element instanceof GBTextWord) {// 文字元素
                wordOccurred = true;
                isVisible = true;
                wordCount++;
            } else if (element instanceof GBTextImageElement || element instanceof GBAudioElement
                    || element instanceof GBNoteElement || element instanceof GBVideoElement) {
                wordOccurred = true;
                isVisible = true;
                wordCount++;
            } else if (isStyleChangeElement(element) && currentElementIndex != startElementIndex) {// 样式元素
                applyStyleChangeElement(element);
            }
            if (newWidth > maxWidth) {// 当前行宽度大于屏幕最大宽度时跳出
                if (info.EndElementIndex != startElementIndex || element instanceof GBTextWord) {
                    --wordCount;
                    info.mEveragePadding = (wordCount > 1 && maxWidth > info.Width) ? (maxWidth - info.Width)
                            / (wordCount - 1) : 0;
                    if (maxWidth > info.Width && !getTextStyle().isUnderline())
                        info.mMorePadding = (maxWidth - info.Width) - info.mEveragePadding * (wordCount - 1);
                    break;
                }
            }
            GBTextElement previousElement = element;// 备份当前元素
            ++currentElementIndex;
            currentCharIndex = 0;
            boolean allowBreak = currentElementIndex == currentParagraphElementSize;// 下一元素不是当前段的最后一个元素则获取下一元素
            // 不允许跳转说明当前元素不是最后一个元素
            if (!allowBreak) {
                element = paragraphCursor.getElement(currentElementIndex);
                // 根据下一个元素是否文本来判断是否跳出
                allowBreak = ((!(element instanceof GBTextWord) || previousElement instanceof GBTextWord)
                        && !(element instanceof GBTextImageElement) && !(element instanceof GBTextControlElement));
            }
            if (allowBreak) {
                info.IsVisible = isVisible;
                info.Width = newWidth;
                if (info.Height < newHeight) {
                    info.Height = newHeight;
                }
                if (info.Descent < newDescent) {
                    info.Descent = newDescent;
                }
                // 记录当前位置
                info.EndElementIndex = currentElementIndex;
                info.EndCharIndex = currentCharIndex;
                info.SpaceCounter = internalSpaceCounter;
                // 记录是否删除空格
                removeLastSpace = !wordOccurred && (internalSpaceCounter > 0);
            }
        } while (currentElementIndex != currentParagraphElementSize);
        storedStyle = getTextStyle();
        // 断字处理
        if (currentElementIndex != currentParagraphElementSize
                && (isHyphenationPossible() || info.EndElementIndex == startElementIndex)) {
            GBTextElement element = paragraphCursor.getElement(currentElementIndex);
            if (element instanceof GBTextWord) {
                final GBTextWord word = (GBTextWord) element;
                newWidth -= getWordWidth(word, currentCharIndex);
                int spaceLeft = maxWidth - newWidth;
                if ((word.Length > 3 && spaceLeft > 2 * context.getSpaceWidth())
                        || info.EndElementIndex == startElementIndex) {
                    GBTextHyphenationInfo hyphenationInfo = GBTextHyphenator.Instance().getInfo(word);
                    // 断字位置
                    int hyphenationPosition = word.Length - 1;
                    int subwordWidth = 0;
                    for (; hyphenationPosition > currentCharIndex; hyphenationPosition--) {
                        if (hyphenationInfo.isHyphenationPossible(hyphenationPosition)) {
                            subwordWidth = getWordWidth(word, currentCharIndex, hyphenationPosition - currentCharIndex,
                                    word.Data[word.Offset + hyphenationPosition - 1] != '-');
                            if (subwordWidth <= spaceLeft) {
                                break;
                            }
                        }
                    }
                    if (hyphenationPosition == currentCharIndex && info.EndElementIndex == startElementIndex) {
                        hyphenationPosition = word.Length == currentCharIndex + 1 ? word.Length : word.Length - 1;
                        subwordWidth = getWordWidth(word, currentCharIndex, word.Length - currentCharIndex, false);
                        for (; hyphenationPosition > currentCharIndex + 1; hyphenationPosition--) {
                            subwordWidth = getWordWidth(word, currentCharIndex, hyphenationPosition - currentCharIndex,
                                    word.Data[word.Offset + hyphenationPosition - 1] != '-');
                            if (subwordWidth <= spaceLeft) {
                                break;
                            }
                        }
                    }
                    if (hyphenationPosition > currentCharIndex) {
                        info.IsVisible = true;
                        info.Width = newWidth + subwordWidth;
                        if (info.Height < newHeight) {
                            info.Height = newHeight;
                        }
                        if (info.Descent < newDescent) {
                            info.Descent = newDescent;
                        }
                        info.EndElementIndex = currentElementIndex;
                        info.EndCharIndex = hyphenationPosition;
                        info.SpaceCounter = internalSpaceCounter;
                        storedStyle = getTextStyle();
                        removeLastSpace = false;
                    }
                }
            }
        }

        if (removeLastSpace) {
            info.Width -= lastSpaceWidth;
            info.SpaceCounter--;
        }

        /******* 添加边距样式支持 *******/
        if (info.isEndOfParagraph() && info.Height > 0) {
            if (info.ParagraphCursor.kind == GBTextParagraph.Kind.TEXT_PARAGRAPH && info.mBottom < 0) {
                /*
                 * 添加段之间间距(浏览器内核渲染时， p段落标签之间默认存在和字体等高的底边距，
                 * 所以存在使用负的底边剧的样式来缩小段之间的间距。故做此判断)
                 */
                info.mBottom += mPaint.getStringHeight();
            }
            info.Height += info.mBottom;
        }
        /**************/

        setTextStyle(storedStyle);
        int sp = info.StartStyle.getSpaceBefore();
        if (isAddCssHeight && sp > 0) {
            info.Height += sp;
        }
        if (info.isEndOfParagraph()) {
            info.VSpaceAfter = getTextStyle().getSpaceAfter();
        }

        if (info.EndElementIndex != currentParagraphElementSize
                || currentParagraphElementSize == info.ParagraphCursorLength) {
            myLineInfoCache.put(info, info);
        }
        // mPreLine = info;
        // L.i(TAG, "info:" + info.toString() + "---:" + info.Height);
        return info;
    }

    // 表格行生成的行元素集合
    List<GBTextLineInfo> mTrTextLine = new ArrayList<GBTextLineInfo>();

    /**
     * 功能描述：将表格行生成行元素<br>
     * 创建者： jack<br>
     * 创建日期：2013-11-22<br>
     *
     * @param paragraphCursor tr段落
     * @return
     */
    private int processTrTextLine(GBTextParagraphCursor paragraphCursor) {
        if (!paragraphCursor.isTreeParagraph())
            return 0;
        int paraTHeight = 0; // 单元格最高的行高
        if (!mTrTextLine.isEmpty() && mTrTextLine.get(0).ParagraphCursor.equals(paragraphCursor)) {
            int tempHeight = 0;
            int tempTdIndex = 0;
            for (GBTextLineInfo info : mTrTextLine) {
                if (tempTdIndex != info.mTdIndex) {
                    tempTdIndex = info.mTdIndex;
                    paraTHeight = Math.max(paraTHeight, tempHeight);
                    tempHeight = 0;
                }
                tempHeight += info.Height + info.Descent + info.VSpaceAfter;
            }
            paraTHeight = Math.max(paraTHeight, tempHeight);
            return paraTHeight;
        }

        mTrTextLine.clear();

        final GBTextTrParagraphImpl para = (GBTextTrParagraphImpl) paragraphCursor.mParagraph;
        // 获取每个单元格宽度
        final int tdWidth = para.mChildNumber > 0 ? getTextAreaWidth() / para.mChildNumber : getTextAreaWidth();
        float fontSize = -1;
        if (mPaint.getmFontSize() * 2 > tdWidth) {
            // 需要缩放字体进行适配
            fontSize = (float) tdWidth / 3;
            mPaint.setmFontSize(fontSize);
        }
        int xStart = getLeftMargin();
        int elementIndex = 0;
        int charIndex = 0;
        int tdIndex = 0;
        int[] tdLineLastIndex = new int[para.mChildNumber];
        boolean mIsTdFirsiLine = true;
        // 生成行
        int tempTH = 0; // 每个单元格累计行高
        do {
            elementIndex = para.mIndex.get(tdIndex);
            int endElementIndex = tdIndex < para.mChildNumber - 1 ? para.mIndex.get(tdIndex + 1) : paragraphCursor
                    .getParagraphLength();
            tempTH = 0;
            do {

                int newWidth = xStart + tdIndex * tdWidth; // 当前行宽
                int maxWidth = newWidth + tdWidth;// 最大行宽

                GBTextLineInfo info = new GBTextLineInfo(paragraphCursor, elementIndex, charIndex, getTextStyle());
                info.mIsTrLine = true;
                info.mTrFontSize = fontSize;
                info.mTdIndex = tdIndex;
                info.mXStartOffset = newWidth;
                info.mXEndOffset = maxWidth;

                int newHeight = mCssProvider.getLineHeightAndInitMP(info, mIsTdFirsiLine);
                newWidth += info.mLeft;
                boolean isBreak = false;
                int counter = 0;
                do {
                    GBTextElement element = paragraphCursor.getElement(elementIndex);
                    newWidth += getElementWidth(element, charIndex);// 计算宽
                    newHeight = Math.max(newHeight, getElementHeight(element)); // 计算高
                    info.Descent = Math.max(info.Descent, getElementDescent(element)); // 计算获取下边距
                    isBreak = counter > 0 && (elementIndex >= endElementIndex || newWidth >= maxWidth);
                    if (isBreak) {

                        info.IsVisible = true;
                        info.Width = newWidth;
                        info.Height = newHeight;
                        // 记录当前位置
                        info.EndElementIndex = elementIndex;
                        info.EndCharIndex = 0;
                        info.SpaceCounter = 0;
                        tempTH += info.Height + info.Descent;
                        mTrTextLine.add(info);
                        tempTH += info.VSpaceAfter;
                        // L.i(TAG, "td:" + tdIndex + "info:" +
                        // info.toString());
                        break;
                    }
                    counter++;
                    elementIndex++;
                } while (!isBreak);
                mIsTdFirsiLine = false;
            } while (elementIndex < endElementIndex && tempTH < getTextAreaHeight());
            if (tempTH > getTextAreaHeight()) {
                GBTextLineInfo line = mTrTextLine.get(mTrTextLine.size() - 1);
                tempTH -= (line.Height + line.Descent + line.VSpaceAfter);
                mTrTextLine.remove(line);
            }
            paraTHeight = Math.max(paraTHeight, tempTH);
            tdLineLastIndex[tdIndex] = mTrTextLine.size() - 1;
            tdIndex++;
            mIsTdFirsiLine = true;
        }
        while (elementIndex < paragraphCursor.getParagraphLength() && tdIndex < para.mChildNumber);
        for (int i = 0; i < tdLineLastIndex.length; i++) {
            mTrTextLine.get(tdLineLastIndex[i]).mMaxheight = paraTHeight;
            if (i == tdLineLastIndex.length - 1)
                mTrTextLine.get(tdLineLastIndex[i]).mIsTrEnd = true;
        }
        // resetTextStyle();
        return paraTHeight;
    }

    /**
     * 功能描述： 根据行信息计算每个元素在页面所占的区域信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-28<br>
     *
     * @param page
     * @param info
     * @param y    上边距
     */
    private void prepareTextLine(GBTextPage page, GBTextLineInfo info, int y, boolean isLeft) {
        if (info.isEndOfParagraph())
            y = Math.min(y + info.Height - info.mBottom, getBottomLine());
        else
            y = Math.min(y + info.Height, getBottomLine());

        final GBPaint context = mPaint;
        final GBTextParagraphCursor paragraphCursor = info.ParagraphCursor;

        int spaceCounter = info.SpaceCounter;
        int fullCorrection = 0;
        final boolean endOfParagraph = info.isEndOfParagraph();
        boolean wordOccurred = false;
        boolean changeStyle = true;

        int x = info.mIsTrLine ? info.mXStartOffset : getLeftMargin() + info.LeftIndent + info.mLeft;
        final int maxWidth = info.mIsTrLine ? info.mXEndOffset : getTextAreaWidth();
        if (info.mIsTrLine && info.mTrFontSize != -1)
            mPaint.setmFontSize(info.mTrFontSize);
        // 根据对齐模式计算左边距离
        switch (mCssProvider.getTextAlign(info)) { // getTextStyle().getAlignment()
            case GBTextAlignmentType.ALIGN_RIGHT:
                x += Math.max(0, maxWidth - getTextStyle().getRightIndent() - info.Width);
                break;
            case GBTextAlignmentType.ALIGN_CENTER:
                x += Math.max(0, (maxWidth - getTextStyle().getRightIndent() - info.Width) / 2);
                break;
            case GBTextAlignmentType.ALIGN_JUSTIFY:
                if (!endOfParagraph
                        && (paragraphCursor.getElement(info.EndElementIndex) != GBTextElement.AfterParagraph)) {
                    fullCorrection = maxWidth - getTextStyle().getRightIndent() - info.Width;
                }
                break;
            case GBTextAlignmentType.ALIGN_LEFT:
            case GBTextAlignmentType.ALIGN_UNDEFINED:
                break;
        }

        final GBTextParagraphCursor paragraph = info.ParagraphCursor;
        final int paragraphIndex = paragraph.Index;
        final int chpFileIndex = paragraph.chpFileIndex;
        final int endElementIndex = info.EndElementIndex;
        int charIndex = info.RealStartCharIndex;
        GBTextElementArea spaceElement = null;

        int offset = 0;
        int morePadding = info.mMorePadding;
        boolean isAddMorepadd = false;
        for (int wordIndex = info.RealStartElementIndex; wordIndex != endElementIndex; ++wordIndex, charIndex = 0) {
            final GBTextElement element = paragraph.getElement(wordIndex);
            if (isAddMorepadd) {
                offset = info.mEveragePadding;
            }
            final int width = getElementWidth(element, charIndex);
            if (element == GBTextElement.HSpace) {
                offset = 0;
                if (wordOccurred && (spaceCounter > 0)) {
                    // final int correction = fullCorrection / spaceCounter;
                    final int spaceLength = context.getSpaceWidth();// +
                    // correction;
                    if (getTextStyle().isUnderline()) {
                        spaceElement = new GBTextElementArea(chpFileIndex, paragraphIndex, wordIndex, 0, 0, // length
                                true, // is last in element
                                false, // add hyphenation sign
                                false, // changed style
                                getTextStyle(), element, x, x + +spaceLength, y, y, isLeft);
                    } else {
                        spaceElement = null;
                    }
                    x += spaceLength;
                    // fullCorrection -= correction;
                    wordOccurred = false;
                    --spaceCounter;
                }
            } else if (element instanceof GBTextWord || element instanceof GBTextImageElement
                    || element instanceof GBAudioElement || element instanceof GBNoteElement || element instanceof GBAnimObjElement) {
                offset += (morePadding > 0 && isAddMorepadd) ? 1 : 0;
                isAddMorepadd = true;
                morePadding--;

                final int height = getElementHeight(element);
                final int descent = getElementDescent(element);
                final int length = element instanceof GBTextWord ? ((GBTextWord) element).Length : 0;
                if (spaceElement != null) {
                    page.TextElementMap.add(spaceElement);
                    spaceElement = null;
                }

                if (element instanceof GBTextImageElement && ((GBTextImageElement) element).IsCover) {
                    GBTextElementArea gBTextElementArea = new GBTextElementArea(chpFileIndex, paragraphIndex,
                            wordIndex, charIndex, length - charIndex, true, false, changeStyle, getTextStyle(),
                            element, 0, mPaint.getWidth(), Math.max(0, (mPaint.getHeight() - height) / 2), Math.min(
                            mPaint.getHeight(), (mPaint.getHeight() - height) / 2 + height), isLeft);
                    page.TextElementMap.add(gBTextElementArea);
                } else {
                    GBTextElementArea gBTextElementArea = new GBTextElementArea(chpFileIndex, paragraphIndex,
                            wordIndex, charIndex, length - charIndex, true, false, changeStyle, getTextStyle(),
                            element, x + offset, Math.min(mPaint.getWidth() - 1, x + offset + width - 1), y - height
                            + 1, y + descent, isLeft);
                    if (element instanceof GBNoteElement) {
                        int i = 0;
                        i++;
                    }
                    page.TextElementMap.add(gBTextElementArea);
                }
                changeStyle = false;
                wordOccurred = true;
            } else if (element instanceof GBVideoElement) {
                int xpadding = getTextAreaWidth() - width;
//                final int xStart = getLeftMargin();
//                final int xEnd = getRightMargin();
                final int xStart = xpadding > 0 ? getLeftMargin() + xpadding / 2 : getLeftMargin();
                final int xEnd = xpadding > 0 ? xStart + width : mPaint.getWidth() - getRightMargin();
                final int yStart = y - getElementHeight(element) + 1;
                final int yEnd = y + getElementDescent(element);
                GBTextElementArea gBTextElementArea = new GBTextElementArea(chpFileIndex, paragraphIndex, wordIndex,
                        charIndex, -charIndex, true, false, changeStyle, getTextStyle(), element, xStart, xEnd, yStart,
                        yEnd, isLeft);
                page.TextElementMap.add(gBTextElementArea);
            } else if (isStyleChangeElement(element)) {
                offset = 0;
                applyStyleChangeElement(element);
                changeStyle = true;
            } else {
                offset = 0;
            }
            x += width + offset;
        }
        if (!endOfParagraph) {
            final int len = info.EndCharIndex;
            if (len > 0) {
                final int wordIndex = info.EndElementIndex;
                final GBTextWord word = (GBTextWord) paragraph.getElement(wordIndex);
                final boolean addHyphenationSign = word.Data[word.Offset + len - 1] != '-';
                final int width = getWordWidth(word, 0, len, addHyphenationSign);
                final int height = getElementHeight(word);
                final int descent = context.getDescent();
                page.TextElementMap.add(new GBTextElementArea(chpFileIndex, paragraphIndex, wordIndex, 0, len, false, // is
                        // last
                        // in
                        // element
                        addHyphenationSign, changeStyle, getTextStyle(), word, x, x + width - 1, y - height + 1, y
                        + descent, isLeft));
            }
        }
    }

    public synchronized final void scrollPage(boolean forward, int scrollingMode, int value) {
        preparePaintInfo(myCurrentPage, true);
        myPreviousPage.reset();
        myNextPage.reset();
        if (myCurrentPage.PaintState == PaintStateEnum.READY) {
            myCurrentPage.PaintState = forward ? PaintStateEnum.TO_SCROLL_FORWARD : PaintStateEnum.TO_SCROLL_BACKWARD;
            myScrollingMode = scrollingMode;
            myOverlappingValue = value;
        }
    }

    /*
     * if (paragraph.getKind()== GBTextParagraph.Kind.TEXT_PARAGRAPH ||
     * paragraph.getKind() == GBTextParagraph.Kind.TREE_PARAGRAPH) { //
     * elementIndex = 0; for (GBTextParagraph.EntryIterator it =
     * paragraph.iterator(); it.hasNext();) { it.next(); if (it.getType() ==
     * GBTextParagraph.Entry.TEXT || paragraph.getKind() ==
     * GBTextParagraph.Kind.TREE_PARAGRAPH) { wordSize += it.getTextLength(); //
     * wordSize += myModel.getTextLength(position.getChpFileIndex(),
     * paragraphIndex); if (position.getChpInCharIndex() <= wordSize) {
     * elementIndex = it.getTextLength() - ((int) (wordSize -
     * position.getChpInCharIndex())); flag = false; break; } } } }
     */

    /**
     * 功能描述：根据位置跳转 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-19<br>
     *
     * @param //要跳转的位置信息
     */
    public final synchronized void gotoPosition(GBTextPosition position) {
        if (position != null) {
            if (position.getElementIndex() == -1 || position.getCharIndex() == -1) {

                // 获取当前章中包含的文本字符长度

                // long totalChpTextSize = startAndEndTextOffset[1];
                // // 获取当前章中总段数

                int[] positionInfo = MiscUtil.convertChpInWordNumToInternalPosition(myModel, position);
                LogUtils.e(TAG, "position.getChpFileIndex()" + position.getChpFileIndex() + "paragraphIndex" + positionInfo[0]
                        + "elementIndex" + positionInfo[1] + "charIndex" + positionInfo[2]);
                gotoPosition(position.getChpFileIndex(), positionInfo[0], positionInfo[1], positionInfo[2]);

                // TO DO
            } else {
                gotoPosition(position.getChpFileIndex(), position.getParagraphIndex(), position.getElementIndex(),
                        position.getCharIndex());
            }

        }
    }

    /**
     * 功能描述：根据位置跳转<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-19<br>
     *
     * @param paragraphIndex 段索引
     * @param wordIndex
     */
    public final synchronized void gotoPosition(int chpFileIndex, int paragraphIndex, int wordIndex, int charIndex) {
        if (myModel != null && myModel.getTotalParagraphsNumber() > 0) {
            if (isReadRangeEnd(chpFileIndex, paragraphIndex)) {
                showOutRangeDialog();
                return;
            }
            if (!mApplication.isLoadBookChp(chpFileIndex)) {
                mRefreashChpIndex = chpFileIndex;
                mPaintStatus = new RePaintStatus(false, null, chpFileIndex, paragraphIndex, wordIndex, charIndex, true);
                mApplication.openBookByChapFileIndex(chpFileIndex, this);
                isPaintLoading = true;
                isLockScroll = true;
                myPreviousPage.reset();
                myNextPage.reset();
                return;
            }

            mApplication.getViewImp().reset();
            myCurrentPage.moveStartCursor(chpFileIndex, paragraphIndex, wordIndex, charIndex);
            myPreviousPage.reset();
            myNextPage.reset();
            preparePaintInfo(myCurrentPage, true);
            if (myCurrentPage.isEmptyPage()) {
                scrollPage(true, ScrollingMode.NO_OVERLAPPING, 0);
            }
        }
    }

    private final synchronized void gotoPositionByEnd(int chpFileIndex, int paragraphIndex, int wordIndex, int charIndex) {
        if (myModel != null && myModel.getTotalParagraphsNumber() > 0) {

            if (isReadRangeEnd(chpFileIndex, paragraphIndex)) {
                showOutRangeDialog();
                return;
            }

            if (!mApplication.isLoadBookChp(chpFileIndex)) {
                mRefreashChpIndex = chpFileIndex;
                mPaintStatus = new RePaintStatus(false, null, chpFileIndex, paragraphIndex, wordIndex, charIndex, false);
                mApplication.openBookByChapFileIndex(chpFileIndex, this);
                isPaintLoading = true;
                isLockScroll = true;
                myPreviousPage.reset();
                myNextPage.reset();
                return;
            }

            myCurrentPage.moveEndCursor(chpFileIndex, paragraphIndex, wordIndex, charIndex);
            myPreviousPage.reset();
            myNextPage.reset();
            preparePaintInfo(myCurrentPage, true);
            if (myCurrentPage.isEmptyPage()) {
                scrollPage(false, ScrollingMode.NO_OVERLAPPING, 0);
            }
        }
    }

    protected synchronized void preparePaintInfo() {
        myPreviousPage.reset();
        myNextPage.reset();
        preparePaintInfo(myCurrentPage, true);
    }

    /**
     * 初始化Page页面的开始结束文字标示
     *
     * @param //page被刷新页
     * @param isNeedRefreash 是否需要页面重绘
     */
    private synchronized void preparePaintInfo(GBTextPage page, boolean isNeedRefreash) {
        int newWidth = getTextAreaWidth();// 获取 宽
        int newHeight = getTextAreaHeight();// 获取高
        if (newWidth != page.OldWidth || newHeight != page.OldHeight) {
            page.OldWidth = newWidth;
            page.OldHeight = newHeight;
            if (page.PaintState != PaintStateEnum.NOTHING_TO_PAINT) {
                page.LineInfos.clear();
                if (page == myPreviousPage) {
                    if (!page.EndCursor.isNull()) {
                        page.StartCursor.reset();
                        page.PaintState = PaintStateEnum.END_IS_KNOWN;
                    } else if (!page.StartCursor.isNull()) {
                        page.EndCursor.reset();
                        page.PaintState = PaintStateEnum.START_IS_KNOWN;
                    }
                } else {
                    if (!page.StartCursor.isNull()) {
                        page.EndCursor.reset();
                        page.PaintState = PaintStateEnum.START_IS_KNOWN;
                    } else if (!page.EndCursor.isNull()) {
                        page.StartCursor.reset();
                        page.PaintState = PaintStateEnum.END_IS_KNOWN;
                    }
                }
            }
        }

        if (page.PaintState == PaintStateEnum.NOTHING_TO_PAINT || page.PaintState == PaintStateEnum.READY) {
            return;
        }
        final int oldState = page.PaintState;
        // 获取缓存行信息
        final HashMap<GBTextLineInfo, GBTextLineInfo> cache = myLineInfoCache;
        for (GBTextLineInfo info : page.LineInfos) {
            cache.put(info, info);
        }

        switch (page.PaintState) {
            default:
                break;
            case PaintStateEnum.TO_SCROLL_FORWARD:
                if (!page.EndCursor.getParagraphCursor().isLast() || !page.EndCursor.isEndOfParagraph()) {
                    final GBTextWordCursor startCursor = new GBTextWordCursor();
                    switch (myScrollingMode) {
                        case ScrollingMode.NO_OVERLAPPING:
                            break;
                        case ScrollingMode.KEEP_LINES:
                            page.findLineFromEnd(startCursor, myOverlappingValue);
                            break;
                        case ScrollingMode.SCROLL_LINES:
                            page.findLineFromStart(startCursor, myOverlappingValue);
                            if (startCursor.isEndOfParagraph()) {
                                startCursor.nextParagraph(this, isNeedRefreash);
                            }
                            break;
                        case ScrollingMode.SCROLL_PERCENTAGE:
                            page.findPercentFromStart(startCursor, getTextAreaHeight(), myOverlappingValue);
                            break;
                    }

                    if (!startCursor.isNull() && startCursor.samePositionAs(page.StartCursor)) {
                        page.findLineFromStart(startCursor, 1);
                    }

                    if (!startCursor.isNull()) {
                        final GBTextWordCursor endCursor = new GBTextWordCursor();
                        // 生成行信息
                        buildInfos(page, startCursor, endCursor, isNeedRefreash);
                        if (!page.isEmptyPage()
                                && (myScrollingMode != ScrollingMode.KEEP_LINES || !endCursor
                                .samePositionAs(page.EndCursor))) {
                            page.StartCursor.setCursor(startCursor);
                            page.EndCursor.setCursor(endCursor);
                            break;
                        }
                    }

                    page.StartCursor.setCursor(page.EndCursor);
                    buildInfos(page, page.StartCursor, page.EndCursor, isNeedRefreash);
                }
                break;
            case PaintStateEnum.TO_SCROLL_BACKWARD:
                if (!page.StartCursor.getParagraphCursor().isFirst() || !page.StartCursor.isStartOfParagraph()) {
                    switch (myScrollingMode) {
                        case ScrollingMode.NO_OVERLAPPING:
                            page.StartCursor.setCursor(findStart(page.StartCursor, SizeUnit.PIXEL_UNIT,
                                    getTextAreaHeight(), isNeedRefreash));
                            break;
                        case ScrollingMode.KEEP_LINES: {
                            GBTextWordCursor endCursor = new GBTextWordCursor();
                            page.findLineFromStart(endCursor, myOverlappingValue);
                            if (!endCursor.isNull() && endCursor.samePositionAs(page.EndCursor)) {
                                page.findLineFromEnd(endCursor, 1);
                            }
                            if (!endCursor.isNull()) {
                                GBTextWordCursor startCursor = findStart(endCursor, SizeUnit.PIXEL_UNIT,
                                        getTextAreaHeight(), isNeedRefreash);
                                if (startCursor.samePositionAs(page.StartCursor)) {
                                    page.StartCursor.setCursor(findStart(page.StartCursor, SizeUnit.PIXEL_UNIT,
                                            getTextAreaHeight(), isNeedRefreash));
                                } else {
                                    page.StartCursor.setCursor(startCursor);
                                }
                            } else {
                                page.StartCursor.setCursor(findStart(page.StartCursor, SizeUnit.PIXEL_UNIT,
                                        getTextAreaHeight(), isNeedRefreash));
                            }
                            break;
                        }
                        case ScrollingMode.SCROLL_LINES:
                            page.StartCursor.setCursor(findStart(page.StartCursor, SizeUnit.LINE_UNIT,
                                    myOverlappingValue, isNeedRefreash));
                            break;
                        case ScrollingMode.SCROLL_PERCENTAGE:
                            page.StartCursor.setCursor(findStart(page.StartCursor, SizeUnit.PIXEL_UNIT,
                                    getTextAreaHeight() * myOverlappingValue / 100, isNeedRefreash));
                            break;
                    }
                    buildInfos(page, page.StartCursor, page.EndCursor, isNeedRefreash);
                    // if (page.PaintState == PaintStateEnum.WAITING)
                    // break;
                    if (page.isEmptyPage()) {
                        page.StartCursor.setCursor(findStart(page.StartCursor, SizeUnit.LINE_UNIT, 1, isNeedRefreash));
                        buildInfos(page, page.StartCursor, page.EndCursor, isNeedRefreash);
                    }
                }
                break;
            case PaintStateEnum.START_IS_KNOWN:
                buildInfos(page, page.StartCursor, page.EndCursor, isNeedRefreash);
                break;
            case PaintStateEnum.END_IS_KNOWN:
                page.StartCursor.setCursor(findStart(page.EndCursor, SizeUnit.PIXEL_UNIT, getTextAreaHeight(),
                        isNeedRefreash));
                buildInfos(page, page.StartCursor, page.EndCursor, isNeedRefreash);
                break;
        }

        page.PaintState = PaintStateEnum.READY;
        // TODO: cache?
        myLineInfoCache.clear();

        if (page == myCurrentPage) {
            if (oldState != PaintStateEnum.START_IS_KNOWN) {
                myPreviousPage.reset();
            }
            if (oldState != PaintStateEnum.END_IS_KNOWN) {
                myNextPage.reset();
            }
        }
    }

    @Override
    public void clearCaches() {
        resetMetrics();
        rebuildPaintInfo();
        mApplication.getViewImp().reset();
        myCharWidth = -1;
        mStyleStack.clear();
        mTrTextLine.clear();
        myLineInfoCache.clear();
        GBTextStyleCache.clear();
    }

    protected void rebuildPaintInfo() {
        myPreviousPage.reset();
        myNextPage.reset();
        GBTextParagraphCursorCache.clear();
        if (myCurrentPage.PaintState != PaintStateEnum.NOTHING_TO_PAINT) {
            myCurrentPage.LineInfos.clear();
            if (!myCurrentPage.StartCursor.isNull()) {
                myCurrentPage.StartCursor.rebuild();
                myCurrentPage.EndCursor.reset();
                myCurrentPage.PaintState = PaintStateEnum.START_IS_KNOWN;
            } else if (!myCurrentPage.EndCursor.isNull()) {
                myCurrentPage.EndCursor.rebuild();
                myCurrentPage.StartCursor.reset();
                myCurrentPage.PaintState = PaintStateEnum.END_IS_KNOWN;
            }
        }
        myLineInfoCache.clear();
    }

    /**
     * 功能描述： 获取行高度<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-5<br>
     *
     * @param info 计算行
     * @param unit 计算模式
     * @return
     */
    private int infoSize(GBTextLineInfo info, int unit) {
        return (unit == SizeUnit.PIXEL_UNIT) ? ((info.isStartOfParagraph() ? info.mTop : 0) + info.Height
                + info.Descent + info.VSpaceAfter) : (info.IsVisible ? 1 : 0);
    }

    /**
     * 功能描述： 根据指定位置计算段落所占高度<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-5<br>
     *
     * @param cursor                计算的开始位置
     * @param beforeCurrentPosition 是否向上计算
     * @param unit                  计算单位
     * @return
     */
    private int paragraphSize(GBTextWordCursor cursor, boolean beforeCurrentPosition, int unit) {
        final GBTextParagraphCursor paragraphCursor = cursor.getParagraphCursor();
        if (paragraphCursor == null) {
            return 0;
        }
        final int endElementIndex = beforeCurrentPosition ? cursor.getElementIndex() : paragraphCursor
                .getParagraphLength();

        reloadStyleInfo(paragraphCursor, 0);

        int size = 0;
        if (paragraphCursor.isTreeParagraph()) {
            size = processTrTextLine(paragraphCursor);
        } else {
            int wordIndex = 0;
            int charIndex = 0;
            while (wordIndex != endElementIndex) {
                GBTextLineInfo info = processTextLine(paragraphCursor, wordIndex, charIndex, endElementIndex);
                wordIndex = info.EndElementIndex;
                charIndex = info.EndCharIndex;
                size += infoSize(info, unit);
            }
        }
        return size;
    }

    /**
     * 功能描述：将文本元素指针从段落开始位置移动指定页面高度<br>
     * 创建者： jack<br>
     * 创建日期：2013-11-27<br>
     *
     * @param cursor
     * @param unit
     * @param size
     */
    private void skip(GBTextWordCursor cursor, int unit, int size) {
        final GBTextParagraphCursor paragraphCursor = cursor.getParagraphCursor();
        if (paragraphCursor == null) {
            return;
        }
        if (paragraphCursor.isTreeParagraph()) {
            if (size < mPaint.getStringHeight())
                cursor.moveToParagraphStart();
            else
                cursor.moveToParagraphEnd();
        } else {
            final int endElementIndex = paragraphCursor.getParagraphLength();
            while (!cursor.isEndOfParagraph() && (size > 0)) {
                GBTextLineInfo info = processTextLine(paragraphCursor, cursor.getElementIndex(), cursor.getCharIndex(),
                        endElementIndex);
                cursor.moveTo(info.EndElementIndex, info.EndCharIndex);
                size -= infoSize(info, unit);
            }
        }
    }

    /**
     * 功能描述：根据结束标示查找一页开始标示 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param end  页面的结束标示
     * @param unit 计算单位
     * @param size 显示区域高度
     */
    private GBTextWordCursor findStart(GBTextWordCursor end, int unit, int size, boolean isNeedRefreash) {
        // 处理章节接点位置，开始索引是上一章的结束坐标
        if (end.getParagraphCursor().isEndOfSection()) {
            final boolean flag = GBApplication.Instance().isLoadBookChp(end.getParagraphCursor().chpFileIndex);
            if (!flag) {
                mRefreashChpIndex = end.getParagraphCursor().chpFileIndex;
                if (isNeedRefreash) {
                    mPaintStatus = new RePaintStatus(true, PageIndex.PREVIOUS, end);
                    isPaintLoading = true;
                }
                mApplication.openBookByChapFileIndex(end.getParagraphCursor().chpFileIndex, this);
                return null;
            }
        }

        boolean isDouble = GBLibrary.Instance().DoublePageOption.getValue();
        if (isDouble) {
            size *= 2;
        }
        final GBTextWordCursor start = new GBTextWordCursor(end);
        if (start.getParagraphCursor().isTreeParagraph() && !start.isEndOfParagraph()) {
            start.moveToParagraphStart();
        } else {
            size -= paragraphSize(start, true, unit);
        }
        boolean positionChanged = !start.isStartOfParagraph();
        start.moveToParagraphStart();
        while (size > 0) {
            if (positionChanged && start.getParagraphCursor().isEndOfSection()) {
                break;
            }
            if (!start.previousParagraph(this, true)) {
                // break;
                return null;
            }
            // 判断如果是章节开始位置结束查找
            if (start.getParagraphCursor().Index == 0)
                break;
            if (!start.getParagraphCursor().isEndOfSection()) {
                positionChanged = true;
            }
            size -= paragraphSize(start, false, unit);
            if (!isSlipByChapter() && start.getParagraphCursor().isTxtCatalog())
                break;
        }
        if (size < 0)
            skip(start, unit, -size);

        if (!start.getParagraphCursor().isTreeParagraph() && unit == SizeUnit.PIXEL_UNIT) {
            boolean sameStart = start.samePositionAs(end);
            if (!sameStart && start.isEndOfParagraph() && end.isStartOfParagraph()) {
                GBTextWordCursor startCopy = start;
                startCopy.nextParagraph(this, false);
                sameStart = startCopy.samePositionAs(end);
            }
            if (sameStart) {
                start.setCursor(findStart(end, SizeUnit.LINE_UNIT, 1, isNeedRefreash));
            }
        }
        mTrTextLine.clear();
        return start;
    }

    // TODO 如果使用，修改双页模式支持
    protected GBTextElementArea getElementByCoordinates(int x, int y) {
        return myCurrentPage.TextElementMap.binarySearch(x, y);
    }

    @Override
    public boolean onMove(int x, int y) {
        return false;
    }

    @Override
    public boolean onRelease(int x, int y) {
        return false;
    }

    public void hideSelectedRegionBorder() {
        myHighlightSelectedRegion = false;
        mApplication.getViewImp().reset();
    }

    /**
     * 功能描述： <br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param page
     * @return
     */
    private GBTextRegion getSelectedRegion(GBTextPage page) {
        return page.TextElementMap.getRegion(mySelectedRegionSoul);
    }

    /*
     * 获取选中区域
     */
    public GBTextRegion getSelectedRegion() {
        return getSelectedRegion(myCurrentPage);
    }

    protected GBTextRegion findRegion(int x, int y, GBTextRegion.Filter filter) {
        final boolean isLeft = GBLibrary.Instance().DoublePageOption.getValue() ? (x < getTextAreaWidth()) : true;
        return findRegion(x, y, Integer.MAX_VALUE - 1, filter, isLeft, myCurrentPage.tempReginIndex);
    }

    /**
     * 功能描述： 获取当前选择的文本区域<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-30<br>
     * 修改日期：2013-9-3<br>
     * 修改备注：增加双翻页支持，增加参数：isLeft(是否左页),offset（分页下标）,
     *
     * @param x           相对触点x
     * @param y           触点y
     * @param maxDistance
     * @param filter      内容过滤器
     * @param isLeft      是否左页
     * @param offset      区域元素分页下标
     * @return
     */
    protected GBTextRegion findRegion(int x, int y, int maxDistance, GBTextRegion.Filter filter, boolean isLeft,
                                      int offset) {
        return myCurrentPage.TextElementMap.findRegion(x, y, maxDistance, filter, isLeft, offset);
    }

    /**
     * 功能描述： 获取当前选择的文本区域<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-3<br>
     *
     * @param x           触点x
     * @param y           触点y
     * @param maxDistance
     * @param filter      内容过滤器
     * @return
     */
    protected GBTextRegion findRegion(int x, int y, int maxDistance, GBTextRegion.Filter filter) {
        final boolean isLeft = GBLibrary.Instance().DoublePageOption.getValue() ? (x < getTextAreaWidth()) : true;
        return myCurrentPage.TextElementMap.findRegion(isLeft ? x : x - getTextAreaWidth(), y, maxDistance, filter,
                isLeft, myCurrentPage.tempReginIndex);
    }

    /**
     * 功能描述： 设置选中区域信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param region 选中区域信息
     */
    public void selectRegion(GBTextRegion region) {
        final GBTextRegion.Soul soul = region != null ? region.getSoul() : null;
        if (soul == null || !soul.equals(mySelectedRegionSoul)) {
            myHighlightSelectedRegion = true;
        }
        mySelectedRegionSoul = soul;
    }

    protected boolean initSelection(int x, int y) {
        y -= GBTextSelectionCursor.getHeight() / 2 + GBTextSelectionCursor.getAccent() / 2;
        if (!mySelection.start(x, y)) {
            return false;
        }
        mApplication.getViewImp().reset();
        mApplication.getViewImp().repaint();
        return true;
    }

    /*
     * 清除选中信息
     */
    public void clearSelection() {
        if (mySelection.clear()) {
            mApplication.getViewImp().reset();
            mApplication.getViewImp().repaint();
        }
    }

    /*
     * 获取当前页选中的开始坐标
     */
    public Point getSelectionStartPoint() {
        if (mySelection.isEmpty()) {
            return null;
        }
        final GBTextElementArea selectionStartArea = mySelection.getStartArea(myCurrentPage);
        if (selectionStartArea != null) {
            return new Point(selectionStartArea.mIsLeftPage ? selectionStartArea.XStart : selectionStartArea.XStart
                    + getTextAreaWidth(), selectionStartArea.YStart);
        }
        if (mySelection.hasAPartBeforePage(myCurrentPage)) {
            final GBTextElementArea firstArea = myCurrentPage.TextElementMap.getFirstArea();
            return firstArea != null ? new Point(firstArea.XStart, firstArea.YStart) : null;
        } else {
            final GBTextElementArea lastArea = myCurrentPage.TextElementMap.getLastArea();
            return lastArea != null ? new Point(lastArea.XEnd, lastArea.YEnd) : null;
        }
    }

    /*
     * 获取当前页选中的结束坐标
     */
    public Point getSelectionEndPoint() {
        if (mySelection.isEmpty()) {
            return null;
        }
        final GBTextElementArea selectionEndArea = mySelection.getEndArea(myCurrentPage);
        if (selectionEndArea != null) {
            return new Point(selectionEndArea.mIsLeftPage ? selectionEndArea.XEnd : selectionEndArea.XEnd
                    + getTextAreaWidth(), selectionEndArea.YEnd);
        }
        if (mySelection.hasAPartAfterPage(myCurrentPage)) {
            final GBTextElementArea lastArea = myCurrentPage.TextElementMap.getLastArea();
            return lastArea != null ? new Point(lastArea.XEnd, lastArea.YEnd) : null;
        } else {
            final GBTextElementArea firstArea = myCurrentPage.TextElementMap.getFirstArea();
            return firstArea != null ? new Point(firstArea.XStart, firstArea.YStart) : null;
        }
    }

    public GBTextPosition getSelectionStartPosition() {
        return mySelection.getStartPosition();
    }

    public GBTextPosition getSelectionEndPosition() {
        return mySelection.getEndPosition();
    }

    public boolean isSelectionEmpty() {
        return mySelection.isEmpty();
    }

    public void resetRegionPointer() {
        mySelectedRegionSoul = null;
        myHighlightSelectedRegion = true;
    }

    public GBTextRegion nextRegion(PageEnum.DirectType direction, GBTextRegion.Filter filter) {
        return myCurrentPage.TextElementMap.nextRegion(getSelectedRegion(), direction, filter);
    }

    @Override
    public boolean isCanScroll(PageEnum.PageIndex index) {
        if (isPaintLoading && isLockScroll)
            return false;
        switch (index) {
            default:
                return true;
            case NEXT: {
                final GBTextWordCursor cursor = getEndCursor();
                return cursor != null && !cursor.isNull()
                        && (!cursor.isEndOfParagraph() || !cursor.getParagraphCursor().isLast());
            }
            case PREVIOUS: {
                final GBTextWordCursor cursor = getStartCursor();
                return cursor != null && !cursor.isNull()
                        && (!cursor.isStartOfParagraph() || !cursor.getParagraphCursor().isFirst());
            }
        }
    }

    @Override
    public boolean isLoading() {
        return isPaintLoading;
    }

    // add by jack

    /**
     * 功能描述：是否使用css样式 <br>
     * 创建者： jack<br>
     * 创建日期：2013-5-29<br>
     *
     * @return
     */
    public abstract boolean isUseCssStyleImp();

    @Override
    public void callback(Integer result) {
        LogUtils.i("@@@", "result:" + result.intValue() + "--" + isPaintLoading + "---:" + mRefreashChpIndex);
        if (result != -1 && isPaintLoading && result.intValue() == mRefreashChpIndex) {
            // 空白章节处理
            // if (myModel.getParagraphsNumber(result.intValue()) <= 0) {
            // if (mPaintStatus.mIsCurl) {
            // // 翻页时出现空白章节
            // if (mPaintStatus.mPageIndex == PageIndex.NEXT &&
            // result.intValue() < myModel.getChapterSize() - 1) {
            // mPaintStatus.mCursor.moveToParagraph(result.intValue() + 1, 0);
            // mRefreashChpIndex = result.intValue() + 1;
            // mApplication.openBookByChapFileIndex(result.intValue() + 1,
            // this);
            // return;
            // } else if (mPaintStatus.mPageIndex == PageIndex.PREVIOUS &&
            // result > 0) {
            // mPaintStatus.mCursor.moveToParagraph(result.intValue() - 1, 0);
            // mRefreashChpIndex = result.intValue() - 1;
            // mApplication.openBookByChapFileIndex(result.intValue() - 1,
            // this);
            // return;
            // }
            // } else if (result.intValue() < myModel.getChapterSize() - 1) {
            // // 跳转时出现空白章
            // mPaintStatus.mChpFileIndex = result.intValue() + 1;
            // mRefreashChpIndex = result.intValue() + 1;
            // mApplication.openBookByChapFileIndex(result.intValue() + 1,
            // this);
            // return;
            // }
            // }

            // 跳转时预加载章节
            if (!mPaintStatus.mIsCurl)
                mApplication.openBookByChapFileIndex(result.intValue() + 1, null);
            mRefreashChpIndex = -1;
            isPaintLoading = false;
            mApplication.getViewImp().repaintOnThread(true);
            loadChapOver(result);
        }
        isLockScroll = false;
    }

    protected abstract void loadChapOver(int id);

    @Override
    public boolean preChapter() {
        if (isPaintLoading)
            return false;
        final int chpindex = myCurrentPage.StartCursor.getChpFileIndex();
        if (chpindex > 0) {
            gotoPosition(chpindex - 1, 0, 0, 0);
            mApplication.getViewImp().reset();
            mApplication.getViewImp().repaint();
        } else
            return false;
        return true;
    }

    @Override
    public boolean nextChapter() {
        if (isPaintLoading)
            return false;
        final int chpindex = myCurrentPage.StartCursor.getChpFileIndex();
        if (chpindex < myModel.getChapterSize() - 1) {
            gotoPosition(chpindex + 1, 0, 0, 0);
            mApplication.getViewImp().reset();
            mApplication.getViewImp().repaint();
        } else
            return false;
        return true;
    }

    /*******************************
     * 加载中刷新功能支持
     ***********************************/
    // 是否加载中
    public boolean isPaintLoading = false;
    private boolean isLockScroll = false; // 是否禁止翻页
    // 等待刷新的章节
    public int mRefreashChpIndex = -1;
    public RePaintStatus mPaintStatus;
    /*******************************
     * 加载中刷新功能支持
     ***********************************/

    // 试读业务支持
    private GBTextModel.PositionInfo mReadRangePosition;
    private int mReadRange = -1;

    @Override
    public boolean isReadRangeEnd() {
        return isReadRangeEnd(getEndCursor().getChpFileIndex(), getEndCursor().getParagraphIndex());
    }

    private boolean isReadRangeEnd(int chpFileIndex, int paraIndex) {
        if (mReadRange == -1)
            return false;
        if (mReadRangePosition == null) {
            int page = getTotalPage() * mReadRange / 100;
            final float factor = computeCharsPerPage();
            final float textSize = page * factor;

            int intTextSize = (int) textSize;
            mReadRangePosition = myModel.findParagraphByTextLength(intTextSize);
        }
        return mReadRangePosition.mChpFileIndex < chpFileIndex
                || (mReadRangePosition.mChpFileIndex == chpFileIndex && mReadRangePosition.mParagraphIndex <= paraIndex);
    }

    @Override
    public void setReadRange(int range) {
        mReadRange = range;
    }

    protected abstract void showOutRangeDialog();
}
