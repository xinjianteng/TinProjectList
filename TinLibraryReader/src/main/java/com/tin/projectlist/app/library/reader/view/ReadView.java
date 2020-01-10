package com.tin.projectlist.app.library.reader.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.object.GBColor;
import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextSelectionCursor;
import com.tin.projectlist.app.library.reader.parser.text.model.GBHyperlinkType;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextHyperlink;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBAnimObjRegionSoul;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBAudioRegionSoul;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBNoteRegionSoul;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextAnnotation;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextElementArea;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextFixedPosition;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextHighlighting;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextHyperlinkRegionSoul;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextImageRegionSoul;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPage;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextRegion;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextWordRegionSoul;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBVideoRegionSoul;
import com.tin.projectlist.app.library.reader.parser.view.GBPaint;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum.DirectType;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum.PageIndex;
import com.tin.projectlist.app.library.reader.controller.ActionCode;
import com.tin.projectlist.app.library.reader.controller.ColorProfile;
import com.tin.projectlist.app.library.reader.controller.MoveCursorAction;
import com.tin.projectlist.app.library.reader.controller.ReaderApplication;
import com.tin.projectlist.app.library.reader.controller.ScrollingPreferences;
import com.tin.projectlist.app.library.reader.controller.TapZoneMap;
import com.tin.projectlist.app.library.reader.controller.TextBuildTraverser;
import com.tin.projectlist.app.library.reader.controller.WordCountTraverser;
import com.tin.projectlist.app.library.reader.view.widget.GBAndroidPaintContext;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextView;

import java.util.List;

/**
 * 类名： ReadView.java#FBView<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class ReadView extends GBTextView {
    private ReaderApplication myReader;

    public ReadView(ReaderApplication reader) {
        super(reader);
        myReader = reader;
    }

    public void setModel(GBTextModel model) {
        super.setModel(model);
        // if (myFooter != null) {
        // myFooter.resetTOCMarks();
        // }
    }

    private int myStartY;
    private boolean myIsBrightnessAdjustmentInProgress;
    private int myStartBrightness;

    /*
     * TapZoneMap 点击区域配置结合
     */
    private TapZoneMap myZoneMap;

    /*
     * 获取热点集合
     */
    private TapZoneMap getZoneMap() {
        final ScrollingPreferences prefs = ScrollingPreferences.Instance();
        String id = prefs.TapZoneMapOption.getValue();
        // 获取热区配置文件，如果为空则根据水平配置获取
        if ("".equals(id)) {
            id = ScrollingPreferences.Instance().HorizontalOption.getValue() ? "right_to_left" : "up";
        }

        if (myZoneMap == null || !id.equals(myZoneMap.Name)) {
            myZoneMap = TapZoneMap.zoneMap(id);
        }
        return myZoneMap;
    }

    /**
     * 单击事件处理
     */
    public boolean onSingleClick(int x, int y) {

        if (super.onSingleClick(x, y)) {
            return true;
        }

        if (!isSelectionEmpty() && findSelectionCursor(x, y, MAX_SELECTION_DISTANCE) == GBTextSelectionCursor.None) {
            // 如果处于选中模式 则取消选中状态
            myReader.runAction(ActionCode.SELECTION_HIDE_PANEL);
            myReader.runAction(ActionCode.SELECTION_CLEAR);
            return true;
        }

        if (myReader.getActivePopup() != null) {
            myReader.hideActivePopup();
            return true;
        }
        // 判断是否点击高亮，批注和笔记区域
        GBTextHighlighting highlighting = inHighlightingOverlap(x, y);
        if (highlighting != null) {
            final GBTextElementArea start = highlighting.getStartArea(myCurrentPage);
            final GBTextElementArea end = highlighting.getEndArea(myCurrentPage);
            myReader.runAction(ActionCode.SELECTION_OPTIONSHOW_PANEL, new Point(start.mIsLeftPage
                    ? start.XStart
                    : start.XStart + mPaint.getWidth(), start.YStart), new Point(end.mIsLeftPage ? end.XEnd : end.XEnd
                    + mPaint.getWidth(), end.YEnd), highlighting);
            return true;
        }
        GBTextAnnotation ann = inAnnotationOverlap(x, y);
        if (ann != null) {
            final GBTextElementArea start = ann.getStartArea(myCurrentPage);
            final GBTextElementArea end = ann.getEndArea(myCurrentPage);
            if (ann.getIsNoteClick()) {
                myReader.runAction(ActionCode.SELECTION_ANNOTATION_NOTE,
                        end.mIsLeftPage ? end.XEnd : end.XEnd + mPaint.getWidth(), end.YEnd, ann);
            } else {
                myReader.runAction(ActionCode.SELECTION_OPTIONSHOW_PANEL, new Point(start.mIsLeftPage
                        ? start.XStart
                        : start.XStart + mPaint.getWidth(), start.YStart), new Point(end.mIsLeftPage
                        ? end.XEnd
                        : end.XEnd + mPaint.getWidth(), end.YEnd), ann);
            }
            return true;
        }
        // 交互动画音视频双击播放
        final GBTextRegion regionAnim = findRegion(x, y, MAX_SELECTION_DISTANCE, GBTextRegion.AnyRegionFilter);
        if (regionAnim != null
                && (regionAnim.getSoul() instanceof GBAudioRegionSoul || regionAnim.getSoul() instanceof GBNoteRegionSoul
                || regionAnim.getSoul() instanceof GBVideoRegionSoul || regionAnim.getSoul() instanceof GBAnimObjRegionSoul)) {
            myReader.runAction(ActionCode.SHOW_MEDIA_VIEW, regionAnim.getSoul());
            return true;
        }

        final GBTextRegion region = findRegion(x, y, MAX_SELECTION_DISTANCE, GBTextRegion.HyperlinkFilter);
        if (region != null) {
            selectRegion(region);
            myReader.getViewImp().reset();
            myReader.getViewImp().repaint();
            myReader.runAction(ActionCode.PROCESS_HYPERLINK);
            return true;
        }

        myReader.runAction(
                getZoneMap().getActionByCoordinates(x, y,
                        GBLibrary.Instance().DoublePageOption.getValue() ? mPaint.getWidth() * 2 : mPaint.getWidth(),
                        mPaint.getHeight(),
                        isDoubleClickSupported() ? TapZoneMap.Tap.singleNotDoubleTap : TapZoneMap.Tap.singleTap), x, y);

        return true;
    }
    @Override
    public boolean isDoubleClickSupported() {
        return myReader.EnableDoubleTapOption.getValue();
    }

    @Override
    public boolean onDoubleClick(int x, int y) {
        if (super.onDoubleClick(x, y)) {
            return true;
        }
        myReader.runAction(
                getZoneMap().getActionByCoordinates(x, y, mPaint.getWidth(), mPaint.getHeight(),
                        TapZoneMap.Tap.doubleTap), x, y);
        return true;
    }

    public boolean onPress(int x, int y) {
        if (super.onPress(x, y)) {
            return true;
        }

        final GBTextSelectionCursor cursor = findSelectionCursor(x, y, MAX_SELECTION_DISTANCE);
        if (cursor != GBTextSelectionCursor.None) {
            myReader.runAction(ActionCode.SELECTION_HIDE_PANEL);
            moveSelectionCursorTo(cursor, x, y);
            return true;
        } else if (!isSelectionEmpty()) {
            // 如果处于选中模式 则取消选中状态
            myReader.runAction(ActionCode.SELECTION_HIDE_PANEL);
            myReader.runAction(ActionCode.SELECTION_CLEAR);
            return true;
        }
        if (myReader.getActivePopup() != null) {
            myReader.hideActivePopup();
            // return true;
        }

        if (myReader.AllowScreenBrightnessAdjustmentOption.getValue() && x < mPaint.getWidth() / 10) {
            myIsBrightnessAdjustmentInProgress = true;
            myStartY = y;
            myStartBrightness = GBLibrary.Instance().getScreenBrightness();
            return true;
        }

        fingerScroll(x, y);
        return true;
    }
    private boolean isFlickScrollingEnabled() {
        final ScrollingPreferences.FingerScrolling fingerScrolling = ScrollingPreferences.Instance().FingerScrollingOption
                .getValue();
        return fingerScrolling == ScrollingPreferences.FingerScrolling.byFlick
                || fingerScrolling == ScrollingPreferences.FingerScrolling.byTapAndFlick;
    }

    private void fingerScroll(int x, int y) {
        if (!isFlickScrollingEnabled()) {
            return;
        }

        final boolean horizontal = ScrollingPreferences.Instance().HorizontalOption.getValue();
        final PageEnum.DirectType direction = horizontal ? DirectType.RTOL : DirectType.UP;
        myReader.getViewImp().fingerScroll(x, y, direction);
    }
    // 是否文本选择模式
    @Override
    public boolean isTxtSelectModel() {
        return getSelectionCursorInMovement() != GBTextSelectionCursor.None;
    }

    public boolean onMove(int x, int y) {
        if (super.onMove(x, y)) {
            return true;
        }

        final GBTextSelectionCursor cursor = getSelectionCursorInMovement();
        if (cursor != GBTextSelectionCursor.None) {
            moveSelectionCursorTo(cursor, x, y);
            return true;
        }

        synchronized (this) {
            if (myIsBrightnessAdjustmentInProgress) {
                if (x >= mPaint.getWidth() / 5) {
                    myIsBrightnessAdjustmentInProgress = false;
                    fingerScroll(x, y);
                } else {
                    final int delta = (myStartBrightness + 30) * (myStartY - y) / mPaint.getHeight();
                    GBLibrary.Instance().setScreenBrightness(myStartBrightness + delta);
                    return true;
                }
            }

            if (isFlickScrollingEnabled()) {
                myReader.getViewImp().clickScroll(x, y);
            }
        }
        return true;
    }

    public boolean onRelease(int x, int y) {
        if (super.onRelease(x, y)) {
            return true;
        }

        final GBTextSelectionCursor cursor = getSelectionCursorInMovement();
        if (cursor != GBTextSelectionCursor.None) {
            releaseSelectionCursor();
            return true;
        }

        if (myIsBrightnessAdjustmentInProgress) {
            myIsBrightnessAdjustmentInProgress = false;
            return true;
        }

        if (isFlickScrollingEnabled()) {
            myReader.getViewImp().startScroll(x, y, ScrollingPreferences.Instance().AnimationSpeedOption.getValue());
            return true;
        }

        return true;
    }
    /*
     * (non-Javadoc)
     * @see com.core.view.GBView#onLongPress(int, int) 长按事件处理
     */
    public boolean onLongPress(int x, int y) {
        if (super.onLongPress(x, y)) {
            return true;
        }

        final GBTextRegion region = findRegion(x, y, MAX_SELECTION_DISTANCE, GBTextRegion.AnyRegionFilter);
        if (region != null) {
            final GBTextRegion.Soul soul = region.getSoul();
            boolean doSelectRegion = false;
            if (soul instanceof GBTextWordRegionSoul) {
                switch (myReader.WordTappingActionOption.getValue()) {
                    case startSelecting :
                        myReader.runAction(ActionCode.SELECTION_HIDE_PANEL);
                        initSelection(x, y);
                        final GBTextSelectionCursor cursor = findSelectionCursor(x, y);
                        if (cursor != GBTextSelectionCursor.None) {
                            moveSelectionCursorTo(cursor, x, y);
                        }
                        return true;
                    case selectSingleWord :
                    case openDictionary :
                        doSelectRegion = true;
                        break;
                }
            } else if (soul instanceof GBTextImageRegionSoul || soul instanceof GBAudioRegionSoul || soul instanceof GBNoteRegionSoul
                    || soul instanceof GBVideoRegionSoul) {
                doSelectRegion = myReader.ImageTappingActionOption.getValue() != ReaderApplication.ImageTappingAction.doNothing;
            } else if (soul instanceof GBTextHyperlinkRegionSoul) {
                doSelectRegion = true;
            }

            if (doSelectRegion) {
                selectRegion(region);
                myReader.getViewImp().reset();
                myReader.getViewImp().repaint();
                return true;
            }
        }

        return false;
    }
    /*
     * 长按之后的移动操作 (non-Javadoc)
     * @see com.core.view.GBView#onMoveAfterLongPress(int, int)
     */
    public boolean onMoveAfterLongPress(int x, int y) {
        if (super.onMoveAfterLongPress(x, y)) {
            return true;
        }

        final GBTextSelectionCursor cursor = getSelectionCursorInMovement();
        if (cursor != GBTextSelectionCursor.None) {
            moveSelectionCursorTo(cursor, x, y);
            return true;
        }

        GBTextRegion region = getSelectedRegion();
        if (region != null) {
            GBTextRegion.Soul soul = region.getSoul();
            if (soul instanceof GBTextHyperlinkRegionSoul || soul instanceof GBTextWordRegionSoul) {
                if (myReader.WordTappingActionOption.getValue() != ReaderApplication.WordTappingAction.doNothing) {
                    region = findRegion(x, y, MAX_SELECTION_DISTANCE, GBTextRegion.AnyRegionFilter);
                    if (region != null) {
                        soul = region.getSoul();
                        if (soul instanceof GBTextHyperlinkRegionSoul || soul instanceof GBTextWordRegionSoul) {
                            selectRegion(region);
                            myReader.getViewImp().reset();
                            myReader.getViewImp().repaint();
                        }
                    }
                }
            }
        }
        return true;
    }
    /*
     * 长按之后的抬起操作 (non-Javadoc)
     * @see com.core.view.GBView#onReleaseAfterLongPress(int, int)
     */
    public boolean onReleaseAfterLongPress(int x, int y) {
        if (super.onReleaseAfterLongPress(x, y)) {
            return true;
        }

        final GBTextSelectionCursor cursor = getSelectionCursorInMovement();
        if (cursor != GBTextSelectionCursor.None) {
            releaseSelectionCursor();
            return true;
        }

        final GBTextRegion region = getSelectedRegion();
        if (region != null) {
            final GBTextRegion.Soul soul = region.getSoul();

            boolean doRunAction = false;
            if (soul instanceof GBTextWordRegionSoul) {
                doRunAction = myReader.WordTappingActionOption.getValue() == ReaderApplication.WordTappingAction.openDictionary;
            } else if (soul instanceof GBTextImageRegionSoul || soul instanceof GBAudioRegionSoul || soul instanceof GBNoteRegionSoul
                    || soul instanceof GBVideoRegionSoul) {
                doRunAction = myReader.ImageTappingActionOption.getValue() == ReaderApplication.ImageTappingAction.openImageView;
            }

            if (doRunAction) {
                myReader.runAction(ActionCode.PROCESS_HYPERLINK);
                return true;
            }
        }

        return false;
    }
    /*
     * (non-Javadoc)
     * @see com.core.view.GBView#onTrackballRotated(int, int) 轨迹球响应事件处理
     */
    public boolean onTrackballRotated(int diffX, int diffY) {
        if (diffX == 0 && diffY == 0) {
            return true;
        }

        final PageEnum.DirectType direction = (diffY != 0) ? (diffY > 0
                ? PageEnum.DirectType.DOWN
                : PageEnum.DirectType.UP) : (diffX > 0 ? PageEnum.DirectType.LTOR : PageEnum.DirectType.RTOL);

        new MoveCursorAction(myReader, direction).run();
        return true;
    }

    @Override
    public ImageFitting getImageFitting() {
        return myReader.FitImagesToScreenOption.getValue();
    }

    @Override
    public int getLeftMargin() {
        return myReader.LeftMarginOption.getValue() * metrics().DPI / 160;
    }

    @Override
    public int getRightMargin() {
        return myReader.RightMarginOption.getValue() * metrics().DPI / 160;
    }

    @Override
    public int getTopMargin() {
        return myReader.TopMarginOption.getValue() * metrics().DPI / 160;
    }

    @Override
    public int getBottomMargin() {
        return myReader.BottomMarginOption.getValue() * metrics().DPI / 160;
    }

    @Override
    public GBFile getWallpaperFile() {
        final String filePath = myReader.getColorProfile().WallpaperOption.getValue();
        if ("".equals(filePath)) {
            return null;
        }

        final GBFile file = GBFile.createFileByPath(filePath);
        if (file == null || !file.exists()) {
            return null;
        }
        return file;
    }

    @Override
    public PageEnum.PageBgMode getWallpaperMode() {
        return getWallpaperFile() instanceof GBFile && !myReader.getColorProfile().WallpageModelOption.getValue()
                ? PageEnum.PageBgMode.STRETCH
                : PageEnum.PageBgMode.TILE;
    }
    @Override
    public GBColor getBackgroundColor() {
        return myReader.getColorProfile().BackgroundOption.getValue();
    }

    @Override
    public GBColor getSelectedBackgroundColor() {
        return myReader.getColorProfile().SelectionBackgroundOption.getValue();
    }

    @Override
    public GBColor getSelectedForegroundColor() {
        return myReader.getColorProfile().SelectionForegroundOption.getValue();
    }

    @Override
    public GBColor getTextColor(GBTextHyperlink hyperlink) {
        final ColorProfile profile = myReader.getColorProfile();
        switch (hyperlink.Type) {
            default :
            case GBHyperlinkType.NONE :
                return profile.RegularTextOption.getValue();
            case GBHyperlinkType.INTERNAL :
                // 获取超链接对象的颜色 判断链接是否点击过
                return myReader.Collection.isHyperlinkVisited(myReader.Model.Book, hyperlink.Id)
                        ? profile.VisitedHyperlinkTextOption.getValue()
                        : profile.HyperlinkTextOption.getValue();

            case GBHyperlinkType.EXTERNAL :
                return profile.HyperlinkTextOption.getValue();
        }
    }

    @Override
    public GBColor getHighlightingColor() {
        return myReader.getColorProfile().HighlightingOption.getValue();
    }
    @Override
    public GBColor getReadTextColor() {
        return myReader.getColorProfile().ReadTextColorOption.getValue();
    }

    @Override
    public GBColor getPageTitleColor() {
        return myReader.getColorProfile().PageTiltleTextOption.getValue();
    }

    // private class Footer implements FooterArea {
    // private Runnable UpdateTask = new Runnable() {
    // public void run() {
    // myReader.getViewImp().repaint();
    // }
    // };
    //
    // private ArrayList<TOCTree> myTOCMarks;
    //
    // public int getHeight() {
    // return myReader.FooterHeightOption.getValue();
    // }
    //
    // public synchronized void resetTOCMarks() {
    // myTOCMarks = null;
    // }
    //
    // private final int MAX_TOC_MARKS_NUMBER = 100;
    // private synchronized void updateTOCMarks(BookModel model) {
    // myTOCMarks = new ArrayList<TOCTree>();
    // TOCTree toc = model.TOCTree;
    // if (toc == null) {
    // return;
    // }
    // int maxLevel = Integer.MAX_VALUE;
    // if (toc.getSize() >= MAX_TOC_MARKS_NUMBER) {
    // final int[] sizes = new int[10];
    // for (TOCTree tocItem : toc) {
    // if (tocItem.Level < 10) {
    // ++sizes[tocItem.Level];
    // }
    // }
    // for (int i = 1; i < sizes.length; ++i) {
    // sizes[i] += sizes[i - 1];
    // }
    // for (maxLevel = sizes.length - 1; maxLevel >= 0; --maxLevel) {
    // if (sizes[maxLevel] < MAX_TOC_MARKS_NUMBER) {
    // break;
    // }
    // }
    // }
    // for (TOCTree tocItem : toc.allSubTrees(maxLevel)) {
    // myTOCMarks.add(tocItem);
    // }
    // }
    //
    // public synchronized void paint(ZLPaintContext context) {
    // final GBFile wallpaper = getWallpaperFile();
    // if (wallpaper != null) {
    // context.clear(wallpaper, getWallpaperMode());
    // } else {
    // context.clear(getBackgroundColor());
    // }
    //
    // final FBReaderApp reader = myReader;
    // if (reader == null) {
    // return;
    // }
    // final BookModel model = reader.Model;
    // if (model == null) {
    // return;
    // }
    //
    // //final GBColor bgColor = getBackgroundColor();
    // // TODO: separate color option for footer color
    // final GBColor fgColor = getTextColor(GBTextHyperlink.NO_LINK);
    // final GBColor fillColor =
    // reader.getColorProfile().FooterFillOption.getValue();
    //
    // final int left = getLeftMargin();
    // final int right = context.getWidth() - getRightMargin();
    // final int height = getHeight();
    // final int lineWidth = height <= 10 ? 1 : 2;
    // final int delta = height <= 10 ? 0 : 1;
    // context.setFont(
    // reader.FooterFontOption.getValue(),
    // height <= 10 ? height + 3 : height + 1,
    // height > 10, false, false, false
    // );
    //
    // final PagePosition pagePosition = ReadView.this.pagePosition();
    //
    // final StringBuilder info = new StringBuilder();
    // //获取显示的页码
    // if (reader.FooterShowProgressOption.getValue()) {
    // info.append(pagePosition.Current);
    // info.append("/");
    // info.append(pagePosition.Total);
    // }
    // if (reader.FooterShowBatteryOption.getValue()) {
    // if (info.length() > 0) {
    // info.append(" ");
    // }
    // info.append(reader.getBatteryLevel());
    // info.append("%");
    // }
    // if (reader.FooterShowClockOption.getValue()) {
    // if (info.length() > 0) {
    // info.append(" ");
    // }
    // info.append(ZLibrary.Instance().getCurrentTimeString());
    // }
    // final String infoString = info.toString();
    //
    // final int infoWidth = context.getStringWidth(infoString);
    //
    // // draw info text
    // context.setTextColor(fgColor);
    // context.drawString(right - infoWidth, height - delta, infoString);
    //
    // // draw gauge
    // final int gaugeRight = right - (infoWidth == 0 ? 0 : infoWidth + 10);
    // myGaugeWidth = gaugeRight - left - 2 * lineWidth;
    //
    // context.setLineColor(fgColor);
    // context.setLineWidth(lineWidth);
    // context.drawLine(left, lineWidth, left, height - lineWidth);
    // context.drawLine(left, height - lineWidth, gaugeRight, height -
    // lineWidth);
    // context.drawLine(gaugeRight, height - lineWidth, gaugeRight, lineWidth);
    // context.drawLine(gaugeRight, lineWidth, left, lineWidth);
    //
    // final int gaugeInternalRight =
    // left + lineWidth + (int)(1.0 * myGaugeWidth * pagePosition.Current /
    // pagePosition.Total);
    //
    // context.setFillColor(fillColor);
    // context.fillRectangle(left + 1, height - 2 * lineWidth,
    // gaugeInternalRight, lineWidth + 1);
    //
    // if (reader.FooterShowTOCMarksOption.getValue()) {
    // if (myTOCMarks == null) {
    // updateTOCMarks(model);
    // }
    // final int fullLength = sizeOfFullText();
    // for (TOCTree tocItem : myTOCMarks) {
    // TOCTree.Reference reference = tocItem.getReference();
    // if (reference != null) {
    // final int refCoord = sizeOfTextBeforeParagraph(reference.ParagraphIndex);
    // final int xCoord =
    // left + 2 * lineWidth + (int)(1.0 * myGaugeWidth * refCoord / fullLength);
    // context.drawLine(xCoord, height - lineWidth, xCoord, lineWidth);
    // }
    // }
    // }
    // }

    // TODO: remove
    int myGaugeWidth = 1;
    /*
     * public int getGaugeWidth() { return myGaugeWidth; }
     */

    /*
     * public void setProgress(int x) { // set progress according to tap
     * coordinate int gaugeWidth = getGaugeWidth(); float progress = 1.0f *
     * Math.min(x, gaugeWidth) / gaugeWidth; int page = (int)(progress *
     * computePageNumber()); if (page <= 1) { gotoHome(); } else {
     * gotoPage(page); } myReader.getViewImp().reset();
     * myReader.getViewImp().repaint(); }
     */
    // }

    // private Footer myFooter;

    // @Override
    // public Footer getFooterArea() {
    // if (myReader.ScrollbarTypeOption.getValue() == SCROLLBAR_SHOW_AS_FOOTER)
    // {
    // if (myFooter == null) {
    // myFooter = new Footer();
    // myReader.addTimerTask(myFooter.UpdateTask, 15000);
    // }
    // } else {
    // if (myFooter != null) {
    // myReader.removeTimerTask(myFooter.UpdateTask);
    // myFooter = null;
    // }
    // }
    // return myFooter;
    // }

    @Override
    protected void releaseSelectionCursor() {
        super.releaseSelectionCursor();
        if (getCountOfSelectedWords() > 0) {
            myReader.runAction(ActionCode.SELECTION_SHOW_PANEL);
        }
    }
    /**
     * 功能描述：获取选中文字<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @return
     */
    public String getSelectedText() {
        final TextBuildTraverser traverser = new TextBuildTraverser(this);
        if (!isSelectionEmpty()) {
            traverser.traverse(getSelectionStartPosition(), getSelectionEndPosition());
        }
        return traverser.getText();
    }
    /**
     * 功能描述： 获取选中文字的数量<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @return
     */
    public int getCountOfSelectedWords() {
        final WordCountTraverser traverser = new WordCountTraverser(this);
        if (!isSelectionEmpty()) {
            traverser.traverse(getSelectionStartPosition(), getSelectionEndPosition());
        }
        return traverser.getCount();
    }

    public static final int SCROLLBAR_SHOW_AS_FOOTER = 3;

    @Override
    public int scrollbarType() {
        // return myReader.ScrollbarTypeOption.getValue();
        return 0;
    }

    @Override
    public PageEnum.Anim getAnimType() {
        return ScrollingPreferences.Instance().AnimationOption.getValue();
    }

    @Override
    protected void drawSelectionCursorInternal(GBPaint arg0, int arg1, int arg2, boolean isLeft) {
        if (arg0 instanceof GBAndroidPaintContext) {
            final Bitmap bm = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance())
                    .getActivity().getResources(), isLeft
                    ? com.geeboo.R.drawable.rollbox_up
                    : com.geeboo.R.drawable.rollbox_down);
            arg1 -= bm.getWidth() / 2;
            if (isLeft) {
                arg2 = arg2 - bm.getHeight()  ;
            } else {

            }
            ((GBAndroidPaintContext) arg0).drawImage(arg1, arg2, bm);
        }
    }

    @Override
    protected void drawNotes(GBPaint context, int x, int y) {
        final Bitmap bit = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity()
                .getResources(), R.drawable.notes);
        ((GBAndroidPaintContext) context).drawImage(x+15 - bit.getWidth() / 2 + 5, y-10 - bit.getHeight() / 2, bit);
    }

    @Override
    protected void drawNotesBG(GBPaint context, int XStart, int YStart, int XEnd, int YEnd) {
        final Bitmap bit = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity()
                .getResources(), R.drawable.note_tip);
        ((GBAndroidPaintContext) context).drawImage(XEnd - bit.getWidth() / 2 + 10, YEnd - bit.getHeight() / 2 - 5, bit);
//        ((GBAndroidPaintContext) context).drawImage(XStart, YStart, XStart - bit.getWidth() / 2 + 5, XStart - bit.getHeight() / 2, bit);
    }

    @Override
    protected void drawAudioBG(GBPaint context, int XStart, int YStart, int XEnd, int YEnd) {
        final Bitmap bit = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity()
                .getResources(), R.drawable.audio_bg);
        ((GBAndroidPaintContext) context).drawImage(XStart, YStart, XEnd, YEnd, bit);
    }
    @Override
    protected void drawVideoBG(GBPaint context, int XStart, int YStart, int XEnd, int YEnd) {
        final Bitmap bit = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity()
                .getResources(), R.drawable.video_bg);
        L.i("ReadView", "=========>" + XStart + "," + YStart + "," + XEnd + "," + YEnd);
        ((GBAndroidPaintContext) context).drawImage(XStart, YStart, XEnd, YEnd, bit);
    }
    @Override
    protected void drawAnimBG(GBPaint context, int XStart, int YStart, int XEnd, int YEnd) {
        final Bitmap bit = BitmapFactory.decodeResource(((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity()
                .getResources(), R.drawable.anim_bg);
        ((GBAndroidPaintContext) context).drawImage(XStart, YStart, XEnd, YEnd, bit);
    }

    @Override
    public boolean isUseCssStyleImp() {
        return GBLibrary.Instance().UseCssOption.getValue();
    }

    /**
     *
     * 功能描述： 获取页面标签<br>
     * modify by yangn<br>
     * 创建日期：2014-1-14<br>
     *
     * @param
     */
    public List<GBTextFixedPosition> getBookPageMarkList() {
        return mApplication.getPageBookmark(myCurrentPage.StartCursor, myCurrentPage.EndCursor);
    }

    @Override
    public synchronized void onScrollEnd(PageIndex pageIndex) {
        super.onScrollEnd(pageIndex);
        if (mScrollEndLisenter != null)
            mScrollEndLisenter.onscrollend();
    }
    @Override
    protected void loadChapOver(int id) {
        if (mScrollEndLisenter != null)
            mScrollEndLisenter.onLoadEnd();
    }
    // 翻页结束监听
    private onScrollEndLisenter mScrollEndLisenter;
    public void setmScrollEndLisenter(onScrollEndLisenter mScrollEndLisenter) {
        this.mScrollEndLisenter = mScrollEndLisenter;
    }

    public interface onScrollEndLisenter {
        public void onscrollend();
        public void onLoadEnd();
    }
    // 显示试读控制框
    @Override
    protected void showOutRangeDialog() {
        mApplication.runAction(ActionCode.SHOW_READ_OUTOFRANGE);
    }
    // 是否按章节切分页面（txt）
    private boolean mIsSlipByChapter = true;
    public void setmIsSlipByChapter(boolean isSlipByChapter) {
        this.mIsSlipByChapter = isSlipByChapter;
    }
    @Override
    public boolean isSlipByChapter() {
        return mIsSlipByChapter;
    }

    @Override
    public boolean nextChapter() {
        if (mIsSlipByChapter)
            return super.nextChapter();
        else {
            TOCTree toc = myReader.getTxtTocTree(true);
            if (toc != null) {
                gotoPosition(toc.getReference().ChpFileIndex, toc.getReference().ParagraphIndex, 0, 0);
                return true;
            }
            return false;
        }
    }
    @Override
    public boolean preChapter() {
        if (mIsSlipByChapter)
            return super.preChapter();
        else {
            TOCTree toc = myReader.getTxtTocTree(false);
            if (toc != null) {
                gotoPosition(toc.getReference().ChpFileIndex, toc.getReference().ParagraphIndex, 0, 0);
                return true;
            }
            return false;
        }
    }
    @Override
    protected boolean isShowTitle() {
        return myReader.isShowTitle == 1;
    }
    @Override
    protected String getBookName() {
        return "《" + myReader.getBookTitle() + "》";
    }

    @Override
    protected String getChapterName(GBTextPage page) {
        final TOCTree tree = myReader.getTOCElementByPage(page);
        myReader.chapterName = (tree == null ? "" : tree.getText());
        return myReader.chapterName;
    }
}
