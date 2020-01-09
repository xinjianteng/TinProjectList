package com.tin.projectlist.app.library.reader.controller;

import com.geeboo.read.view.BaseMenuActivity;

/**
 * 类名： ShowReadOutOfRange.java<br>
 * 描述： 显示购买弹出框<br>
 * 创建者： jack<br>
 * 创建日期：2013-12-9<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class ShowReadOutOfRange extends ReadAction {

    private BaseMenuActivity mActivity;

    public ShowReadOutOfRange(ReaderApplication application, BaseMenuActivity activity) {
        super(application);
        this.mActivity = activity;
    }
    @Override
    public void run(Object... o) {
        mActivity.showDisMenu();
        mActivity.showPayDialog();
    }

}
