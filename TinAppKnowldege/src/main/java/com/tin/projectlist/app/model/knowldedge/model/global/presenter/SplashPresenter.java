package com.tin.projectlist.app.model.knowldedge.model.global.presenter;

import android.app.Activity;

import com.tin.projectlist.app.model.knowldedge.model.global.imp.ISplash;
import com.tin.projectlist.app.model.knowldedge.model.global.model.SplashModel;
import knowldege.app.tin.com.tinlibrary.mvp.BasePresenter;
import com.tin.projectlist.app.model.knowldedge.model.global.imp.ISplashContract;

public class SplashPresenter extends BasePresenter implements ISplashContract.Presenter {

    private ISplashContract.View mView;
    private ISplash mode;

    public SplashPresenter(Activity mActivity) {
        super(mActivity);
        mode=new SplashModel();
    }

    public static SplashPresenter bind(Activity activity) {
       return new SplashPresenter(activity);
    }

    public SplashPresenter attahView(ISplashContract.View view) {
        this.mView = view;
        mView.setPersenter(this);
        return this;
    }

    @Override
    public void getSplashImg() {
        mView.setSplashImg(mode.getSplashImg());
    }

}
