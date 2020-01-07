package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.platform.GBLibrary;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextSelectionCursor;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextRegion.Soul;
/**
 * 类名： GBTextSelection.java<br>
 * 描述： 文本选择信息封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class GBTextSelection implements GBTextAbstractHighlighting {
    static class Point {
        int X;
        int Y;

        Point(int x, int y) {
            X = x;
            Y = y;
        }
    }

    private class SoulPosition {
        GBTextRegion.Soul soul;
        boolean isLeft;
        public SoulPosition(Soul soul, boolean isLeft) {
            super();
            this.soul = soul;
            this.isLeft = isLeft;
        }
    }

    private final GBTextView myView; // 当前视图控件

    private SoulPosition myLeftMostRegionSoul; // 选中文本的起始点信息
    private SoulPosition myRightMostRegionSoul; // 选中文本的结束点信息
    // 当前移动锚点，坐标
    private GBTextSelectionCursor myCursorInMovement = GBTextSelectionCursor.None;
    private final Point myCursorInMovementPoint = new Point(-1, -1);

    private Scroller myScroller;

    GBTextSelection(GBTextView view) {
        myView = view;
    }

    public boolean isEmpty() {
        return myLeftMostRegionSoul == null;
    }

    public boolean clear() {
        if (isEmpty()) {
            return false;
        }

        stop();
        myLeftMostRegionSoul = null;
        myRightMostRegionSoul = null;
        myCursorInMovement = GBTextSelectionCursor.None;
        return true;
    }
    /**
     * 功能描述： 设置文本选择移动坐标<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-10<br>
     *
     * @param cursor
     * @param x
     * @param y
     */
    void setCursorInMovement(GBTextSelectionCursor cursor, int x, int y) {
        myCursorInMovement = cursor;

        final boolean isLeft = GBLibrary.Instance().DoublePageOption.getValue()
                ? (x < myView.getTextAreaWidth())
                : true;

        myCursorInMovementPoint.X = isLeft ? x : x - myView.getTextAreaWidth();
        myCursorInMovementPoint.Y = y;
    }

    GBTextSelectionCursor getCursorInMovement() {
        return myCursorInMovement;
    }
    /**
     * 功能描述： 获取正在移动中的拖把坐标<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-3<br>
     *
     * @param isLeft 是否左侧页面（单页即左页）
     * @return
     */
    Point getCursorInMovementPoint(boolean isLeft) {
        if (myCursorInMovement == GBTextSelectionCursor.Left && myLeftMostRegionSoul.isLeft == isLeft)
            return myCursorInMovementPoint;
        if (myCursorInMovement == GBTextSelectionCursor.Right && myRightMostRegionSoul.isLeft == isLeft)
            return myCursorInMovementPoint;
        return null;
    }
    /*
     * 初始化选中起始点
     */
    boolean start(int x, int y) {
        clear();
        final boolean isLeft = GBLibrary.Instance().DoublePageOption.getValue()
                ? (x < myView.getTextAreaWidth())
                : true;

        final GBTextRegion region = myView.findRegion(isLeft ? x : x - myView.getTextAreaWidth(), y,
                GBTextView.MAX_SELECTION_DISTANCE, GBTextRegion.AnyRegionFilter, isLeft,
                myView.myCurrentPage.tempReginIndex);
        if (region == null) {
            return false;
        }
        myRightMostRegionSoul = new SoulPosition(region.getSoul(), isLeft);
        myLeftMostRegionSoul = new SoulPosition(region.getSoul(), isLeft);

        return true;
    }
    void stop() {
        myCursorInMovement = GBTextSelectionCursor.None;
        if (myScroller != null) {
            myScroller.stop();
            myScroller = null;
        }
    }
    /*
     * 设置拖把是否在左页
     */
    private void setReginSoulIsleft(int x) {
        SoulPosition sp = null;

        if (myCursorInMovement == GBTextSelectionCursor.Right) {
            sp = myRightMostRegionSoul;
        } else if (myCursorInMovement == GBTextSelectionCursor.Left) {
            sp = myLeftMostRegionSoul;
        } else {
            return;
        }
        final boolean isDoublePage = GBLibrary.Instance().DoublePageOption.getValue();
        sp.isLeft = isDoublePage ? (x < myView.getTextAreaWidth()) : true;
    }

    /**
     * 功能描述：扩大选择区域到指定位置<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-10<br>
     *
     * @param x 位置x坐标点
     * @param y 位置y坐标点
     */
    void expandTo(int x, int y) {
        if (isEmpty()) {
            return;
        }
        // 是否双翻页
        final boolean isDoublePage = GBLibrary.Instance().DoublePageOption.getValue();

        final GBTextElementAreaVector vector = myView.myCurrentPage.TextElementMap;

        if (isDoublePage) {
            final GBTextElementArea area = myView.myCurrentPage.TextElementMap
                    .get(myView.myCurrentPage.tempElementIndex - 1);
            // L.i("GBTextSelection", x + "-- getTextWidth:" +
            // myView.getTextAreaWidth());
            if (x < myView.getTextAreaWidth() - myView.getRightMargin()) {

                final GBTextElementArea firstArea = vector.getFirstArea();
                expandPageTo(x, y, firstArea, area, true, false);
            } else if (x > myView.getTextAreaWidth() + myView.getLeftMargin()) {
                final GBTextElementArea lastArea = vector.getLastArea();
                expandPageTo(x - myView.getTextAreaWidth(), y, area, lastArea, false, true);
            }
            setReginSoulIsleft(x);
        } else {
            final GBTextElementArea firstArea = vector.getFirstArea();
            final GBTextElementArea lastArea = vector.getLastArea();
            expandPageTo(x, y, firstArea, lastArea, true, true);
        }
    }

    /*
     * 左侧页面选中扩充
     */
    private void expandPageTo(int x, int y, GBTextElementArea firstArea, GBTextElementArea lastArea,
                              boolean isTopScroll, boolean isBottomScroll) {
        // L.i("GBTextSelection", "isTopScroll:" + isTopScroll +
        // "--isbottomScroll:" + isBottomScroll);
        if (firstArea != null && y < firstArea.YStart) {
            if (myScroller != null && myScroller.scrollsForward()) {
                myScroller.stop();
                myScroller = null;
            }
            if (myScroller == null && isTopScroll) {
                myScroller = new Scroller(false, x, y);
                return;
            }
        } else if (lastArea != null
                && y + GBTextSelectionCursor.getHeight() / 2 + GBTextSelectionCursor.getAccent() / 2 > lastArea.YEnd) {
            if (myScroller != null && !myScroller.scrollsForward()) {
                myScroller.stop();
                myScroller = null;
            }
            if (myScroller == null && isBottomScroll) {
                myScroller = new Scroller(true, x, y);
                return;
            }
        } else {
            if (myScroller != null) {
                myScroller.stop();
                myScroller = null;
            }
        }

        if (myScroller != null) {
            myScroller.setXY(x, y);
        }

        GBTextRegion region = myView.findRegion(x, y, GBTextView.MAX_SELECTION_DISTANCE, GBTextRegion.AnyRegionFilter,
                isTopScroll, myView.myCurrentPage.tempReginIndex);
        if (region == null && myScroller != null) {
            region = myView.findRegion(x, y, GBTextRegion.AnyRegionFilter);
        }
        if (region == null) {
            return;
        }
        // 判断选择拖把是否需要切换/交叉切换
        final GBTextRegion.Soul soul = region.getSoul();
        if (myCursorInMovement == GBTextSelectionCursor.Right) {
            if (myLeftMostRegionSoul.soul.compareTo(soul) <= 0) {
                myRightMostRegionSoul.soul = soul;
            } else {
                myRightMostRegionSoul.soul = myLeftMostRegionSoul.soul;
                myRightMostRegionSoul.isLeft = myLeftMostRegionSoul.isLeft;
                myLeftMostRegionSoul.soul = soul;
                myCursorInMovement = GBTextSelectionCursor.Left;
            }
        } else {
            if (myRightMostRegionSoul.soul.compareTo(soul) >= 0) {
                myLeftMostRegionSoul.soul = soul;
            } else {
                myLeftMostRegionSoul.soul = myRightMostRegionSoul.soul;
                myLeftMostRegionSoul.isLeft = myRightMostRegionSoul.isLeft;
                myRightMostRegionSoul.soul = soul;
                myCursorInMovement = GBTextSelectionCursor.Right;
            }
        }

        if (myCursorInMovement == GBTextSelectionCursor.Right) {
            if (hasAPartAfterPage(myView.myCurrentPage) && isBottomScroll) {
                myView.scrollPage(true, GBTextView.ScrollingMode.SCROLL_LINES, 1);
                myView.mApplication.getViewImp().reset();
                myView.preparePaintInfo();
            }
        } else {
            if (hasAPartBeforePage(myView.myCurrentPage) && isTopScroll) {
                myView.scrollPage(false, GBTextView.ScrollingMode.SCROLL_LINES, 1);
                myView.mApplication.getViewImp().reset();
                myView.preparePaintInfo();
            }
        }
    }
    boolean isAreaSelected(GBTextElementArea area) {
        return !isEmpty() && myLeftMostRegionSoul.soul.compareTo(area) <= 0
                && myRightMostRegionSoul.soul.compareTo(area) >= 0;
    }

    public GBTextPosition getStartPosition() {
        if (isEmpty()) {
            return null;
        }
        return new GBTextFixedPosition(myLeftMostRegionSoul.soul.ChpFileIndex,
                myLeftMostRegionSoul.soul.ParagraphIndex, myLeftMostRegionSoul.soul.StartElementIndex, 0);
    }

    public GBTextPosition getEndPosition() {
        if (isEmpty()) {
            return null;
        }
        return new GBTextFixedPosition(myRightMostRegionSoul.soul.ChpFileIndex,
                myRightMostRegionSoul.soul.ParagraphIndex, myRightMostRegionSoul.soul.EndElementIndex, 0);
    }

    public GBTextElementArea getStartArea(GBTextPage page) {
        if (isEmpty()) {
            return null;
        }
        final GBTextElementAreaVector vector = page.TextElementMap;
        final GBTextRegion region = vector.getRegion(myLeftMostRegionSoul.soul);
        if (region != null) {
            return region.getFirstArea();
        }
        final GBTextElementArea firstArea = vector.getFirstArea();
        if (firstArea != null && myLeftMostRegionSoul.soul.compareTo(firstArea) <= 0) {
            return firstArea;
        }
        return null;
    }

    public GBTextElementArea getStartArea(GBTextPage page, boolean isLeft) {
        if (isEmpty()) {
            return null;
        }
        // L.i("GBTextSelection", "isLeft:" + isLeft +
        // "--myLeftMostRegionSoul.isLeft:" + myLeftMostRegionSoul.isLeft);
        if (myLeftMostRegionSoul.isLeft == isLeft)
            return getStartArea(page);
        else
            return null;
    }
    public GBTextElementArea getEndArea(GBTextPage page, boolean isLeft) {
        if (isEmpty()) {
            return null;
        }
        // L.i("GBTextSelection", "isLeft:" + isLeft +
        // "--myRightMostRegionSoul.isLeft:" + myRightMostRegionSoul.isLeft);
        if (myRightMostRegionSoul.isLeft == isLeft)
            return getEndArea(page);
        else
            return null;
    }

    public GBTextElementArea getEndArea(GBTextPage page) {
        if (isEmpty()) {
            return null;
        }
        final GBTextElementAreaVector vector = page.TextElementMap;
        final GBTextRegion region = vector.getRegion(myRightMostRegionSoul.soul);
        if (region != null) {
            return region.getLastArea();
        }
        final GBTextElementArea lastArea = vector.getLastArea();
        if (lastArea != null && myRightMostRegionSoul.soul.compareTo(lastArea) >= 0) {
            return lastArea;
        }
        return null;
    }
    /*
     * 判断当前页第一个元素是否章节开始
     */
    boolean hasAPartBeforePage(GBTextPage page) {
        if (isEmpty()) {
            return false;
        }
        final GBTextElementArea firstPageArea = page.TextElementMap.getFirstArea();
        if (firstPageArea == null) {
            return false;
        }
        final int cmp = myLeftMostRegionSoul.soul.compareTo(firstPageArea);
        return cmp < 0 || (cmp == 0 && !firstPageArea.isFirstInElement());
    }
    /*
     * 判断当前页最后一个元素是否章节结束
     */
    boolean hasAPartAfterPage(GBTextPage page) {
        if (isEmpty()) {
            return false;
        }
        final GBTextElementArea lastPageArea = page.TextElementMap.getLastArea();
        if (lastPageArea == null) {
            return false;
        }
        final int cmp = myRightMostRegionSoul.soul.compareTo(lastPageArea);
        return cmp > 0 || (cmp == 0 && !lastPageArea.isLastInElement());
    }

    @Override
    public void setup(GBTextPosition start, GBTextPosition end) {

    }
    /*
     * 跨页面选中时页面滚动业务类
     */
    private class Scroller implements Runnable {
        private final boolean myScrollForward;
        private int myX, myY;

        Scroller(boolean forward, int x, int y) {
            myScrollForward = forward;
            setXY(x, y);
            myView.mApplication.addTimerTask(this, 400);
        }

        boolean scrollsForward() {
            return myScrollForward;
        }

        void setXY(int x, int y) {
            myX = x;
            myY = y;
        }

        public void run() {
            myView.scrollPage(myScrollForward, GBTextView.ScrollingMode.SCROLL_LINES, 1);
            myView.preparePaintInfo();
            expandTo(myX, myY);
            myView.mApplication.getViewImp().reset();
            myView.mApplication.getViewImp().repaint();
        }

        private void stop() {
            myView.mApplication.removeTimerTask(this);
        }
    }
}
