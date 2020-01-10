package com.tin.projectlist.app.library.reader.view;

import android.os.Bundle;
import android.view.View;


import com.tin.projectlist.app.library.reader.R;

import org.xutils.view.annotation.ContentView;


public final class ReaderActivity extends BaseMenuActivity {

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
