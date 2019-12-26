package com.tin.projectlist.app.model.oldBook.oldBook.core.shelf;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.library.base.widget.MultiStateView;
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
public final class ShelfFragment extends MyLazyFragment<HomeActivity> {
    @ViewInject(R.id.titleBar)
    TitleBar mToolbar;

    @ViewInject(R.id.recycler)
    RecyclerView rcvList;

    @ViewInject(R.id.multi_state_view)
    MultiStateView multiStateView;

    public static ShelfFragment newInstance() {
        return new ShelfFragment();
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
        multiStateView.showEmpty();
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }



}