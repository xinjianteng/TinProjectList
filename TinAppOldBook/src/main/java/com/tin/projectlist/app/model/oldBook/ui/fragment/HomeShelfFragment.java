package com.tin.projectlist.app.model.oldBook.ui.fragment;

import android.view.View;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.common.MyLazyFragment;
import com.tin.projectlist.app.model.oldBook.core.home.HomeActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目炫酷效果示例
 */

@ContentView(R.layout.fragment_shelf)
public final class HomeShelfFragment extends MyLazyFragment<HomeActivity> {
    @ViewInject(R.id.tb_test_d_title)
    TitleBar mToolbar;

    public static HomeShelfFragment newInstance() {
        return new HomeShelfFragment();
    }

    @Override
    protected View getTitleId() {
        return mToolbar;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }



}