package com.tin.projectlist.app.model.oldBook.core.login;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

public class LoginModel extends MvpModel<LoginOnListener> {

    public LoginModel() {
    }

    public void login(String account, String password) {
        getListener().onLoginSucceed(new UserInfo());
    }

}
