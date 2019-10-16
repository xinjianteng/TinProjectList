package com.tin.projectlist.app.model.oldBook.core.login;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class LoginPresenter extends MvpPresenter<LoginContract.View> implements LoginContract.Presenter,LoginOnListener{

    LoginModel loginModel;

    @Override
    public void start() {
        loginModel=new LoginModel();

    }

    @Override
    public void login(String account, String password) {
        loginModel.login(account,password);
    }

    @Override
    public void onLoginSucceed(UserInfo userInfo) {
        getView().loginSuccess(userInfo);
    }

    @Override
    public void onLoginFail(String msg) {

    }

}
