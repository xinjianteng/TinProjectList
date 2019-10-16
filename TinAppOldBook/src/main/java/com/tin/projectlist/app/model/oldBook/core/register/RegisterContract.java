package com.tin.projectlist.app.model.oldBook.core.register;

import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

public class RegisterContract {
    public interface View extends IMvpView {

        void registerError(String msg);

        void registerSuccess(UserInfo userInfo);

    }

    public interface Presenter {
        void register(String account, String password);
    }

}
