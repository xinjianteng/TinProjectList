package com.tin.projectlist.app.model.oldBook.oldBook.core.register;

import com.tin.projectlist.app.model.oldBook.core.register.RegisterContract;
import com.tin.projectlist.app.model.oldBook.core.register.RegisterModel;
import com.tin.projectlist.app.model.oldBook.core.register.RegisterOnListener;
import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class RegisterPresenter extends MvpPresenter<com.tin.projectlist.app.model.oldBook.core.register.RegisterContract.View> implements RegisterContract.Presenter, RegisterOnListener {

    com.tin.projectlist.app.model.oldBook.core.register.RegisterModel registerModel;

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
