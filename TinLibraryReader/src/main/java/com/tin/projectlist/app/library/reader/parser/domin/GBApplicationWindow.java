package com.tin.projectlist.app.library.reader.parser.domin;

import com.core.view.GBViewInter;

/**
 *
 * 类名： GBApplicationWindow.java#ZLApplicationWindow<br>
 * 描述： 窗体管理抽象类<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-29<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBApplicationWindow {
    private GBApplication mApplication;

    protected GBApplicationWindow(GBApplication application) {
        mApplication = application;
        mApplication.setWindow(this);
    }

    public GBApplication getApplication() {
        return mApplication;
    }
    /**
     * 功能描述： 设置窗体标题<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     * @param title 标题
     */
    protected abstract void setTitle(String title);
    /**
     * 功能描述：带提示信息的后台执行任务 <br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     * @param key 要提示信息的key
     * @param runnable 子线程运行任务
     * @param postAction 主线程运行任务
     */
    protected abstract void runWithMessage(String key, Runnable runnable, Runnable postAction);
    /**
     * processException
     * 功能描述： <br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     * @param e
     */
    protected abstract void processException(Exception e);

    protected abstract void refresh();
    /**
     * getViewWidget
     * 功能描述： 获取阅读控件业务类<br>
     * 创建者： jack<br>
     * 创建日期：2013-3-29<br>
     * @return
     */
    protected abstract GBViewInter getViewImp();

    protected abstract void close();

//    protected abstract int getBatteryLevel();
}
