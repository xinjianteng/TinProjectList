package com.tin.projectlist.app.model.oldBook.core.login;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;

public interface LoginOnListener {

    void onLoginSucceed(UserInfo userInfo);

    void onLoginFail(String msg);
}
