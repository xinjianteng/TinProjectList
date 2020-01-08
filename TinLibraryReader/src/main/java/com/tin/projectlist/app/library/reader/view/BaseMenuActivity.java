package com.tin.projectlist.app.library.reader.view;

import android.view.Window;

import com.tin.projectlist.app.library.base.view.base.BaseActivity;


public abstract class BaseMenuActivity extends BaseActivity {

    @Override
    protected void initData() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);


    }


}
