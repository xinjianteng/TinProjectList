package com.tin.projectlist.app.model.oldBook.core.register;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class RegisterPresenter extends MvpPresenter<RegisterContract.View> implements RegisterContract.Presenter,RegisterOnListener {
    /**
     * P 层初始化方法
     */
    @Override
    public void start() {

    }

    @Override
    public void register(String account, String password) {

    }

    @Override
    public void onRegisterSucceed(UserInfo userInfo) {

    }

    @Override
    public void onRegisterFail(String msg) {

    }
}
