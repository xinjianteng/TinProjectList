package com.tin.projectlist.app.model.oldBook.core.login;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

public class LoginContract {
    public interface View extends IMvpView{

        void loginError(String msg);

        void loginSuccess(UserInfo userInfo);

    }

    public  interface  Presenter{
        void login(String account,String password);
    }

}
