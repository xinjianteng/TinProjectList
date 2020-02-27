package com.tin.projectlist.app.library.reader.view;

import android.os.Bundle;
import android.view.View;


import com.tin.projectlist.app.library.reader.R;
import com.tin.projectlist.app.library.reader.view.widget.GBAndroidWidget;

import org.xutils.view.annotation.ContentView;


public final class ReaderActivity extends BaseMenuActivity {


    // epub阅读控件
    protected GBAndroidWidget mWidget;

    public GBAndroidWidget getmWidget() {
        return mWidget;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

    }

    @Override
    protected View getTitleId() {
        return null;
    }

    @Override
    public float getReadPro() {
        return 0;
    }

}
