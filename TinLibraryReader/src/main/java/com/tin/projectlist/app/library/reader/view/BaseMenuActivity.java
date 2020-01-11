package com.tin.projectlist.app.library.reader.view;

import android.view.Window;

import com.tin.projectlist.app.library.base.view.base.BaseActivity;
import com.tin.projectlist.app.library.reader.controller.ReaderApplication;
import com.tin.projectlist.app.library.reader.view.widget.GBAndroidWidget;


public abstract class BaseMenuActivity extends BaseActivity {
    protected ReaderApplication mApplication;
    // 当前阅读图书实体
    protected ReadBottomMenu mBottomMenu;
    protected ReadHeadMenu mHeadMenu;


    // epub阅读控件
    protected GBAndroidWidget mWidget;

    @Override
    protected void initData() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomMenu = new ReadBottomMenu(this);
        mHeadMenu = new ReadHeadMenu(this);

    }


    /***
     * 获取阅读进度
     * @return
     */
    public abstract float getReadPro();



}
