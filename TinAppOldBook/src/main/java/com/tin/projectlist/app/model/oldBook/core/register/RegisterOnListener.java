package com.tin.projectlist.app.model.oldBook.core.register;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;

public interface RegisterOnListener {
    void onRegisterSucceed(UserInfo userInfo);

    void onRegisterFail(String msg);
}
