package com.tin.projectlist.app.model.oldBook.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.common.MyLazyFragment;
import com.tin.projectlist.app.model.oldBook.core.home.HomeActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目界面跳转示例
 */

@ContentView(R.layout.fragment_me)
public final class HomeMeFragment extends MyLazyFragment<HomeActivity> {

    public static HomeMeFragment newInstance() {
        return new HomeMeFragment();
    }

    @ViewInject(R.id.tb_test_d_title)
    TitleBar mToolbar;

    @ViewInject(R.id.btn_about)
    TextView btn_about;

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



    @Event(value = R.id.btn_about, type = View.OnClickListener.class/*可选参数, 默认是View.OnClickListener.class*/)
    private void onAboutClick(View view) {
        /**
         * (1)在manifest配置文件中配置了scheme参数
         * (2)网络端获取url
         * (3)跳转
         */
        String url = "scheme://geebook/bookDetail?bookUserId=10011002";

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);


    }


}