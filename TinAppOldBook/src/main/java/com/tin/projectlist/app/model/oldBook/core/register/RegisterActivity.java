package com.tin.projectlist.app.model.oldBook.core.register;

import android.view.View;

import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;

import org.xutils.view.annotation.ContentView;


@ContentView(R.layout.activity_register)
public class RegisterActivity extends MvpActivity<RegisterPresenter> implements RegisterContract.View{


    @Override
    protected RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @Override
    protected View getTitleId() {
        return null;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void registerError(String msg) {

    }

    @Override
    public void registerSuccess(UserInfo userInfo) {

    }
}
