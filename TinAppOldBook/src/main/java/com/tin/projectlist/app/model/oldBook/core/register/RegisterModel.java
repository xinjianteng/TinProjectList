package com.tin.projectlist.app.model.oldBook.core.register;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterModel extends MvpModel<RegisterOnListener> {

    public RegisterModel() {
    }


    public void register(String account, String password) {
        final UserInfo userInfo = new UserInfo();
        userInfo.setMobile(account);
        userInfo.setPassword(password);
        BmobQuery<UserInfo> userInfoBmobQuery = new BmobQuery<>();
        userInfoBmobQuery.addWhereEqualTo("mobile", account);
        userInfoBmobQuery.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(List<UserInfo> object, BmobException e) {
                if (e == null && object.size() > 0) {
                    if (getListener() != null) {
                        getListener().onRegisterFail("已注册的账号，请直接登录");
                    }
                } else {
                    userInfo.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                if (getListener() != null) {
                                    getListener().onRegisterSucceed(userInfo);
                                }
                            } else {
                                if (getListener() != null) {
                                    getListener().onRegisterFail(e.getMessage());
                                }
                            }
                        }
                    });
                }
            }
        });
    }

}
