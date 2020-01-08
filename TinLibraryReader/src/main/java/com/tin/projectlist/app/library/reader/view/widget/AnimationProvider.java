package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.FloatMath;

import com.core.platform.GBLibrary;
import com.core.view.PageEnum;
import com.core.view.PageEnum.PageIndex;
import com.geeboo.read.view.GBAndroidLibrary;

import java.util.LinkedList;
import java.util.List;

/**
 * 类名： AnimationProvider.java<br>
 * 描述： 阅读翻页提供者<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
abstract class AnimationProvider {
    /*
     * 翻页模式
     */
    static enum Mode {
        NoScrolling(false), // 无翻页效果
        ManualScrolling(false), // 手触翻页时
        AnimatedScrollingForward(true), // 自动向前翻页
        AnimatedScrollingBackward(true); // 自动向后翻页

        final boolean Auto; // 是否自动

        Mode(boolean auto) {
            Auto = auto;
        }
    }
    // 当前翻页模式
    protected Mode myMode = Mode.NoScrolling;
    // 显示页面图片管理对象
    protected final BitmapManager myBitmapManager;
    protected int myStartX; // 触摸起始点x
    protected int myStartY; // 触摸起始点y
    protected int myEndX; // 目前触控点x
    protected int myEndY; // 目前触控点y
    // 翻页位置
    protected PageEnum.DirectType myDirection;
    protected float mySpeed;
    // 显示页面宽高
    protected int myWidth;
    protected int myHeight;

    protected AnimationProvider(BitmapManager bitmapManager) {
        myBitmapManager = bitmapManager;
    }
    /*
     * 获取当前翻页模式
     */
    Mode getMode() {
        return myMode;
    }
    /*
     * 停止翻页
     */
    final void terminate() {
        myMode = Mode.NoScrolling;
        mySpeed = 0;
        myDrawInfos.clear();
    }
    /*
     * 开始手动翻页
     * @param x 触点x坐标
     * @param y 触电y坐标
     */
    final void startManualScrolling(int x, int y) {
        if (!myMode.Auto) {
            myMode = Mode.ManualScrolling;
            myEndX = myStartX = x;
            myEndY = myStartY = y;
        }
    }

    void scrollTo(int x, int y) {
        if (myMode == Mode.ManualScrolling) {
            myEndX = x;
            myEndY = y;
        }
    }
    /**
     * 功能描述： 启动自动滚动动画<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-7<br>
     *
     * @param x
     * @param y
     * @param speed 速度
     */
    void startAnimatedScrolling(int x, int y, int speed) {
        if (myMode != Mode.ManualScrolling) {
            return;
        }

        if (getPageToScrollTo(x, y) == PageEnum.PageIndex.CURRENT) {
            return;
        }

        final int diff = myDirection.mIsHorizontal ? x - myStartX : y - myStartY;
        final int dpi = GBLibrary.Instance().getDisplayDPI();
        // final int minDiff = myDirection.mIsHorizontal
        // ? (myWidth > myHeight ? myWidth / 8 : myWidth / 6)
        // : (myHeight > myWidth ? myHeight / 8 : myHeight / 6);
        final int minDiff = Math.min(myWidth, myHeight) / 10;
        boolean forward = Math.abs(diff) > Math.min(minDiff, dpi / 4);

        myMode = forward ? Mode.AnimatedScrollingForward : Mode.AnimatedScrollingBackward;

        float velocity = 15;
        if (myDrawInfos.size() > 1) {
            int duration = 0;
            for (DrawInfo info : myDrawInfos) {
                duration += info.Duration;
            }
            duration /= myDrawInfos.size();
            final long time = System.currentTimeMillis();
            myDrawInfos.add(new DrawInfo(x, y, time, time + duration));
            velocity = 0;
            for (int i = 1; i < myDrawInfos.size(); ++i) {
                final DrawInfo info0 = myDrawInfos.get(i - 1);
                final DrawInfo info1 = myDrawInfos.get(i);
                final float dX = info0.X - info1.X;
                final float dY = info0.Y - info1.Y;
                velocity += FloatMath.sqrt(dX * dX + dY * dY) / Math.max(1, info1.Start - info0.Start);
            }
            velocity /= myDrawInfos.size() - 1;
            velocity *= duration;
            velocity = Math.min(100, Math.max(15, velocity));
        }
        myDrawInfos.clear();

        if (getPageToScrollTo() == PageEnum.PageIndex.PREVIOUS) {
            forward = !forward;
        }

        switch (myDirection) {
            case UP :
            case RTOL :
                mySpeed = forward ? -velocity : velocity;
                break;
            case LTOR :
            case DOWN :
                mySpeed = forward ? velocity : -velocity;
                break;
        }

        startAnimatedScrollingInternal(speed);
    }
    public void startAnimatedScrolling(PageEnum.PageIndex pageIndex, Integer x, Integer y, int speed) {
        if (myMode.Auto) {
            return;
        }

        terminate();
        myMode = Mode.AnimatedScrollingForward;

        switch (myDirection) {
            case UP :
            case RTOL :
                mySpeed = pageIndex == PageIndex.NEXT ? -15 : 15;
                break;
            case LTOR :
            case DOWN :
                mySpeed = pageIndex == PageIndex.NEXT ? 15 : -15;
                break;
        }
        setupAnimatedScrollingStart(x, y);
        startAnimatedScrollingInternal(speed);
    }
    /**
     * 功能描述： 启动翻页动画<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-8<br>
     *
     * @param speed 速度
     */
    protected abstract void startAnimatedScrollingInternal(int speed);
    /**
     * 功能描述： 设置动画执行起始点<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-7<br>
     *
     * @param x
     * @param y
     */
    protected abstract void setupAnimatedScrollingStart(Integer x, Integer y);
    /**
     * 功能描述： 是否在执行翻页动画中<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     *
     * @return
     */
    boolean inProgress() {
        return myMode != Mode.NoScrolling;
    }
    /*
     * 获取翻页距离
     */
    protected int getScrollingShift() {
        return myDirection.mIsHorizontal ? myEndX - myStartX : myEndY - myStartY;
    }
    /*
     * 翻页设置
     * @param direction 翻页方向
     * @param width 页面宽
     * @param height 页面高
     */
    final void setup(PageEnum.DirectType direction, int width, int height) {
        final boolean isDouble = GBAndroidLibrary.Instance().DoublePageOption.getValue();
        myDirection = direction;
        myWidth = isDouble ? width / 2 : width;
        myHeight = height;
    }
    /*
     * 翻页动画步长执行算方法定义
     */
    abstract void doStep();
    /*
     * 获取翻页距离百分比
     */
    int getScrolledPercent() {
        final int full = myDirection.mIsHorizontal ? myWidth : myHeight;
        final int shift = Math.abs(getScrollingShift());
        return 100 * shift / full;
        // 修改为点到点的距离比
        // final int full = (int) Math.hypot((double) myWidth, (double)
        // myHeight);
        // final int shift = (int) Math.hypot((double) Math.abs(myStartX -
        // myEndX), (double) Math.abs(myStartY - myEndY));
        // return 100 * shift / full;
    }
    /*
     * 绘制信息封装
     */
    static class DrawInfo {
        final int X, Y;
        final long Start;
        final int Duration;

        DrawInfo(int x, int y, long start, long finish) {
            X = x;
            Y = y;
            Start = start;
            Duration = (int) (finish - start);
        }
    }

    final private List<DrawInfo> myDrawInfos = new LinkedList<DrawInfo>();

    final void draw(Canvas canvas) {
        int w = myWidth;
        myBitmapManager.setSize(w, myHeight, GBLibrary.Instance().DoublePageOption.getValue());
        final long start = System.currentTimeMillis();
        drawInternal(canvas);
        myDrawInfos.add(new DrawInfo(myEndX, myEndY, start, System.currentTimeMillis()));
        if (myDrawInfos.size() > 3) {
            myDrawInfos.remove(0);
        }
    }
    /*
     * 绘制抽象定义
     */
    protected abstract void drawInternal(Canvas canvas);
    /*
     * 获取翻页方向
     * @param x y 启动时的开始坐标点
     */
    abstract PageIndex getPageToScrollTo(int x, int y);

    final PageIndex getPageToScrollTo() {
        return getPageToScrollTo(myEndX, myEndY);
    }

    protected Bitmap[] getBitmapFrom() {
        return myBitmapManager.getBitmap(PageIndex.CURRENT);
    }

    protected Bitmap[] getBitmapTo() {
        return myBitmapManager.getBitmap(getPageToScrollTo());
    }

    public void computeScroll() {
    }
    /**
     * 功能描述：双翻页绘制书脊<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-23<br>
     *
     * @param canvas
     */
    public void drawSpiner(Canvas canvas) {
    }
}
