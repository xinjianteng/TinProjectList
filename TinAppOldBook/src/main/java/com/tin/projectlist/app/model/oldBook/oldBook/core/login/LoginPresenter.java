package com.tin.projectlist.app.model.oldBook.oldBook.core.login;

import com.tin.projectlist.app.model.oldBook.core.login.LoginContract;
import com.tin.projectlist.app.model.oldBook.core.login.LoginModel;
import com.tin.projectlist.app.model.oldBook.core.login.LoginOnListener;
import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class LoginPresenter extends MvpPresenter<com.tin.projectlist.app.model.oldBook.core.login.LoginContract.View> implements LoginContract.Presenter, LoginOnListener {

    com.tin.projectlist.app.model.oldBook.core.login.LoginModel loginModel;

    @Override
    public void start() {
        loginModel = new LoginModel();
        loginModel.setListener(this);
    }

    @Override
    public void login(String account, String password) {
        getView().onLoading();
        loginModel.login(account, password);
    }

    @Override
    public void onLoginSucceed(UserInfo userInfo) {
        getView().onComplete();
        getView().loginSuccess(userInfo);
    }

    @Override
    public void onLoginFail(String msg) {
        getView().onComplete();
        getView().loginError(msg);
    }

}
