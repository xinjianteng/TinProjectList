package com.tin.library;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhy.autolayout.AutoLayoutActivity;

public abstract class BaseActivity extends AutoLayoutActivity{
    protected static final int NO_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getContentLayout()!=NO_ID){
            setContentView(getContentLayout());
        }
        setView();
        setValue();
        setListener();

    }




    protected abstract int getContentLayout();

    protected abstract void setView();

    protected abstract void setValue();

    protected abstract void setListener();

}
