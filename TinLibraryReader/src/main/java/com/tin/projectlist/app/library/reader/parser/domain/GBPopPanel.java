package com.core.domain;
/**
 * 类名： GBPopPanel.java#ZLApplication.PopupPanel<br>
 * 描述： 弹出窗体抽象类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-1<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBPopPanel {
    //上下文管理类
    protected final GBApplication mApplication;

    public GBPopPanel(GBApplication application) {
        application.mPopups.put(getId(), this);
        mApplication = application;
    }
    /**
     * 功能描述： 获取控件唯一id<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     * @return
     */
    public abstract String getId();
    /**
     * 功能描述： 窗体更新<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     */
    protected abstract void update();
    /**
     * 功能描述： 窗体隐藏<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     */
    protected abstract void hide();
    /**
     * 功能描述： 窗体显示<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-1<br>
     */
    protected abstract void show();

}
