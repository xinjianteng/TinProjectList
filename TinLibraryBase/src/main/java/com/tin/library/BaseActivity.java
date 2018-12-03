package com.tin.library;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhy.autolayout.AutoLayoutActivity;

import org.xutils.x;

public abstract class BaseActivity extends AutoLayoutActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setView();
        setValue();
        setListener();
    }



    protected abstract void setView();

    protected abstract void setValue();

    protected abstract void setListener();

}
