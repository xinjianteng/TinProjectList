package com.core.view;

import com.core.domain.GBApplication;

/**
 * 类名： GBView.java#ZLView<br>
 * 描述： 自定义翻页控件抽象类<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBView {
    public final GBApplication mApplication;

    protected GBPaint mPaint = new NullPaint();

    protected GBView(GBApplication application) {
        mApplication = application;
    }

    public final GBPaint getPaint() {
        return mPaint;
    }
    /**
     * getAnimationType 功能描述：获取当前设置的翻页动画类型<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param
     */
    public abstract PageEnum.Anim getAnimType();
    /**
     * preparePage 功能描述： 初始化视图页面<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @param paint 绘制对象
     * @param index 要显示的页面标示
     */
    public abstract void initPage(GBPaint paint, PageEnum.PageIndex index);
    /**
     * 功能描述： 绘制视图页面<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @param paint 绘制对象
     * @param index 要显示的页面标示
     */
    public abstract void paint(GBPaint paint, PageEnum.PageIndex index);
    /**
     * 功能描述： 服务于双翻页，启动双翻页时绘制右侧页面<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-28<br>
     *
     * @param paint
     * @param index
     */
    public abstract void paintRight(GBPaint paint, PageEnum.PageIndex index);
    /**
     * 功能描述：绘制加载中<br>
     * 创建者： jack<br>
     * 创建日期：2013-12-5<br>
     *
     * @param paint
     */
    public abstract void paintLoading(GBPaint paint);
    /**
     * onScrollingFinished 功能描述： 当滚动结束时<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @param index 滚动结束时的显示页面标示
     */
    public abstract void onScrollEnd(PageEnum.PageIndex index);
    // 按下事件
    public boolean onPress(int x, int y) {
        return false;
    }
    // 抬起事件
    public boolean onRelease(int x, int y) {
        return false;
    }
    // 手指移动事件
    public boolean onMove(int x, int y) {
        return false;
    }
    // 长按事件
    public boolean onLongPress(int x, int y) {
        return false;
    }
    // 长按后的抬起事件
    public boolean onReleaseAfterLongPress(int x, int y) {
        return false;
    }
    // 长按后的移动事件
    public boolean onMoveAfterLongPress(int x, int y) {
        return false;
    }
    /**
     * onFingerSingleTap 功能描述： 单击事件处理<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @param x
     * @param y
     * @return
     */
    public boolean onSingleClick(int x, int y) {
        return false;
    }
    /**
     * onFingerDoubleTap 功能描述：双击事件处理 <br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @param x
     * @param y
     * @return
     */
    public boolean onDoubleClick(int x, int y) {
        return false;
    }
    /**
     * isDoubleTapSupported 功能描述：是否响应双击事件<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @return
     */
    public boolean isDoubleClickSupported() {
        return false;
    }
    /**
     * 功能描述：android设备轨迹球滚动事件<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     *
     * @param diffX
     * @param diffY
     * @return
     */
    public boolean onTrackballRotated(int diffX, int diffY) {
        return false;
    }
    // 是否显示滚动条
    public abstract boolean isScrollbarShow();
    // 获取滚动条尺寸
    public abstract int getScrollbarFullSize();
    // 根据显示界面获取滚动条位置
    public abstract int getScrollbarThumbPosition(PageEnum.PageIndex index);
    // 根据显示界面获取滚动条长度
    public abstract int getScrollbarThumbLength(PageEnum.PageIndex index);
    // 是否可以翻页
    public abstract boolean isCanScroll(PageEnum.PageIndex index);
    // 是否正在加载中
    public abstract boolean isLoading();
    public abstract void clearCaches();
    public abstract boolean preChapter();
    public abstract boolean nextChapter();
    public abstract int getTotalPage();
    // 是否试读限制点
    public abstract boolean isReadRangeEnd();
    public abstract void setReadRange(int range);
    // 是否按章切换
    public abstract boolean isSlipByChapter();
    // 页面页脚定义
    // abstract public interface FooterArea {
    // int getHeight();
    // void paint(ZLPaintContext context);
    // }
    //
    // abstract public FooterArea getFooterArea();
    // 是否文本选择模式
    public abstract boolean isTxtSelectModel();
}
