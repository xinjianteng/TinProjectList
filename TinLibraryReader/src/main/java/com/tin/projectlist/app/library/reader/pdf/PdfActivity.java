package com.tin.projectlist.app.library.reader.pdf;

import android.view.View;

import com.tin.projectlist.app.library.reader.view.BaseMenuActivity;

public class PdfActivity extends BaseMenuActivity {

    public static final String SCREEN_ORIENT = "screen_orient";



    @Override
    protected View getTitleId() {
        return null;
    }

    /***
     * 获取阅读进度
     * @return
     */
    @Override
    public float getReadPro() {
        return 0;
    }
}
