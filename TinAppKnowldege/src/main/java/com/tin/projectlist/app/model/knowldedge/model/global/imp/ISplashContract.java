package com.tin.projectlist.app.model.knowldedge.model.global.imp;

import knowldege.app.tin.com.tinlibrary.mvp.IBasePresenter;
import knowldege.app.tin.com.tinlibrary.mvp.IBaseView;

public interface ISplashContract {

    interface View extends IBaseView<Presenter>{
        void setSplashImg(String url);
    }

    interface Presenter extends IBasePresenter{
        void getSplashImg();
    }

}
