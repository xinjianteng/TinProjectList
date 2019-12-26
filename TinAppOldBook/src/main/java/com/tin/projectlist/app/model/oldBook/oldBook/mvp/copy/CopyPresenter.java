package com.tin.projectlist.app.model.oldBook.oldBook.mvp.copy;


import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;
import com.tin.projectlist.app.model.oldBook.mvp.copy.CopyContract;
import com.tin.projectlist.app.model.oldBook.mvp.copy.CopyModel;
import com.tin.projectlist.app.model.oldBook.mvp.copy.CopyOnListener;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的业务处理类
 */
public final class CopyPresenter extends MvpPresenter<com.tin.projectlist.app.model.oldBook.mvp.copy.CopyContract.View>
        implements com.tin.projectlist.app.model.oldBook.mvp.copy.CopyContract.Presenter, CopyOnListener {

    private com.tin.projectlist.app.model.oldBook.mvp.copy.CopyModel mModel;

    @Override
    public void start() {
        mModel = new CopyModel();
    }

    /**
     * {@link CopyContract.Presenter}
     */

    @Override
    public void login(String account, String password) {
        mModel.setAccount(account);
        mModel.setPassword(password);
        mModel.setListener(this);
        mModel.login();
    }

    /**
     * {@link CopyOnListener}
     */

    @Override
    public void onSucceed(List<String> data) {
        getView().loginSuccess(data);
    }

    @Override
    public void onFail(String msg) {
        getView().loginError(msg);
    }
}