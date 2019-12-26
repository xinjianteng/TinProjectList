package com.tin.projectlist.app.model.oldBook.oldBook.core.login;

import com.tin.projectlist.app.model.oldBook.core.login.LoginOnListener;
import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginModel extends MvpModel<LoginOnListener> {

    public LoginModel() {
    }

    public void login(String account, String password) {
        BmobQuery<UserInfo> userInfoBmobQuery = new BmobQuery<>();
        userInfoBmobQuery.addWhereEqualTo("mobile", account);
        userInfoBmobQuery.addWhereEqualTo("password", password);
        userInfoBmobQuery.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    getListener().onLoginSucceed(list.get(0));
                } else {
                    getListener().onLoginFail(e.getMessage());
                }
            }
        });


    }

}
