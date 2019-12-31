package com.tin.projectlist.app.model.oldBook.ui.activity;


import android.view.View;
import android.widget.TextView;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.library.base.view.MyActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 关于界面
 */

@ContentView(R.layout.activity_about)
public final class AboutActivity extends MyActivity {

    @ViewInject(R.id.tb_about_title)
    TitleBar titleBar;

    @ViewInject(R.id.tv_des)
    TextView tv_des;

    @Override
    protected View getTitleId() {
        return titleBar;
    }


    @Override
    protected void initData() {

    }

}