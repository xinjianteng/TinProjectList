package com.tin.projectlist.app.model.knowldedge.model.global;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseAppAct;
import com.tin.projectlist.app.model.knowldedge.model.global.imp.ISplashContract;
import com.tin.projectlist.app.model.knowldedge.model.global.presenter.SplashPresenter;

public class SplashActivity extends BaseAppAct implements ISplashContract.View{

    private ISplashContract.Presenter mPresenter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setValue() {
        bindPersenter();
        mPresenter.getSplashImg();

    }

    @Override
    protected void setListener() {

    }


    @Override
    public void setPersenter(ISplashContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public ISplashContract.Presenter bindPersenter() {
        return SplashPresenter.bind(this).attahView(this);
    }

    @Override
    public void setSplashImg(String url) {

    }

}
