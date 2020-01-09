package com.tin.projectlist.app.library.reader.view;

import android.view.Window;

import com.tin.projectlist.app.library.base.view.base.BaseActivity;


public abstract class BaseMenuActivity extends BaseActivity {

    // 当前阅读图书实体
    protected ReadBottomMenu mBottomMenu;
    protected ReadHeadMenu mHeadMenu;

    @Override
    protected void initData() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomMenu = new ReadBottomMenu(this);
        mHeadMenu = new ReadHeadMenu(this);

    }


}
