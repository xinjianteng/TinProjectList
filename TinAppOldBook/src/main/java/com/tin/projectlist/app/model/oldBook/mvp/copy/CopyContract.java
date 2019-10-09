package com.tin.projectlist.app.model.oldBook.mvp.copy;


import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的契约类
 */
public final class CopyContract {

    public interface View extends IMvpView {

        void loginError(String msg);

        void loginSuccess(List<String> data);
    }

    public interface Presenter {
        void login(String account, String password);
    }
}