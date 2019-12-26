package com.tin.projectlist.app.model.oldBook.oldBook.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.common.MyLazyFragment;
import com.tin.projectlist.app.model.oldBook.core.home.HomeActivity;
import com.tin.projectlist.app.model.oldBook.core.login.LoginActivity;
import com.tin.projectlist.app.model.oldBook.ui.activity.AboutActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;


/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 项目界面跳转示例
 */

@ContentView(R.layout.fragment_me)
public final class HomeMeFragment extends MyLazyFragment<HomeActivity> {

    public static HomeMeFragment newInstance() {
        return new HomeMeFragment();
    }

    @ViewInject(R.id.titleBar)
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


    @Event({R.id.btn_about,R.id.btn_login})
    private void onAboutClick(View view) {
        switch (view.getId()){
            case R.id.btn_about:
                startActivity(AboutActivity.class);
                break;
            case R.id.btn_login:
                startActivity(LoginActivity.class);
                break;
        }
    }



}