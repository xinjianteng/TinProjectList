package com.tin.projectlist.app.model.oldBook.core.register;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class RegisterPresenter extends MvpPresenter<RegisterContract.View> implements RegisterContract.Presenter, RegisterOnListener {

    RegisterModel registerModel;

    @Override
    public void start() {
        registerModel = new RegisterModel();
        registerModel.setListener(this);
    }

    @Override
    public void register(String account, String password) {
        getView().onLoading();
        registerModel.register(account, password);

    }

    @Override
    public void onRegisterSucceed(UserInfo userInfo) {
        getView().onComplete();
        getView().registerSuccess(userInfo);
    }

    @Override
    public void onRegisterFail(String msg) {
        getView().onComplete();
        getView().registerError(msg);
    }
}
