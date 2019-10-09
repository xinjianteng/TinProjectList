package com.tin.projectlist.app.model.knowldedge.model.koolearn.presenter;

import android.app.Activity;

import com.tin.projectlist.app.model.knowldedge.model.koolearn.imp.IKoolearn;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.imp.IKoolearnContract;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.mdoel.KoolearnModel;

import knowldege.app.tin.com.tinlibrary.mvp.BasePresenter;

public class KoolearnPresenter  extends BasePresenter implements IKoolearnContract.Presenter{

    private static final String TAG = KoolearnPresenter.class.getSimpleName();
    private IKoolearnContract.View mView;
    private IKoolearn mode;


    public KoolearnPresenter(Activity mActivity) {
        super(mActivity);
        mode=new KoolearnModel();
    }


    public static KoolearnPresenter bind(Activity mActivity) {
        return new KoolearnPresenter(mActivity);
    }


    public  KoolearnPresenter attahView(IKoolearnContract.View view) {
        this.mView = view;
        mView.setPersenter(this);
        return this;
    }

    @Override
    public void getTypeList(String url) {
        mView.showTypeList(mode.getKoolearnTypeList(url));
    }

    @Override
    public void getTypeClassildyList(String url) {
        mView.showTypeClassilyLisxt(mode.getKoolearnTypeClassilyList(url));
    }

    @Override
    public void getTypeClassildyDetail(String url) {
        mView.showTypeClassilyDetail(mode.getKoolearnDetailList(url));
    }


}
