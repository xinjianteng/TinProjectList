package com.tin.projectlist.app.library.reader.parser.view;
/**
 * 类名： GBViewInter.java#ZLViewWidget<br>
 * 描述： 翻页控件业务定义，例如重绘和启动动画的方法<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public interface GBViewInter {

    void reset();
    void repaint();
    void repaintOnThread(boolean isReset);
    /**
     * startManualScrolling 功能描述：手指移动时的翻页<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x 手指触点x
     * @param y 手指触点y
     * @param direction
     */
    void fingerScroll(int x, int y, PageEnum.DirectType direction);
    /**
     * scrollManuallyTo 功能描述： 点击翻页<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x 点击点x
     * @param y 点击点y
     */
    void clickScroll(int x, int y);
    /**
     * startAnimatedScrolling 功能描述： 自动翻页<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param pageIndex 翻页的页面标示
     * @param x 翻页开始点x
     * @param y 翻页开始点y
     * @param direction 翻页方向
     * @param speed 动画执行速度
     */
    void startScroll(PageEnum.PageIndex pageIndex, int x, int y, PageEnum.DirectType direction, int speed);
    /**
     * startAnimatedScrolling 功能描述： 自动翻页<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param pageIndex 翻页的页面标示
     * @param direction 翻页方向
     * @param speed 动画执行速度
     */
    void startScroll(PageEnum.PageIndex pageIndex, PageEnum.DirectType direction, int speed);
    /**
     * startAnimatedScrolling 功能描述： 自动翻页<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-28<br>
     *
     * @param x 翻页开始点x
     * @param y 翻页开始点y
     * @param speed 动画执行速度
     */
    void startScroll(int x, int y, int speed);
    /**
     * 功能描述： 销毁接口，释放内存<br>
     * 创建者： jack<br>
     * 创建日期：2014-4-22<br>
     */
    void destory();
}
