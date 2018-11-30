package com.tin.projectlist.app.model.knowldedge.model.base.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhy.autolayout.AutoLayoutActivity;


public abstract class BaseAct extends AutoLayoutActivity {

    protected View parent;
    protected Bundle mSavedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState=savedInstanceState;
        parent = getWindow().getDecorView().findViewById(android.R.id.content);
        initToolbar();
        setContentView(getLayoutId());
        initView();
        setValue();
        setListener();

    }

    protected abstract void  initToolbar();

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void setValue();

    protected abstract void setListener();


}
