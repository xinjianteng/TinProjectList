package com.tin.projectlist.app.model.oldBook.core.login;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;

import java.util.List;

public interface LoginOnListener {

    void onLoginSucceed(UserInfo userInfo);

    void onLoginFail(String msg);
}
