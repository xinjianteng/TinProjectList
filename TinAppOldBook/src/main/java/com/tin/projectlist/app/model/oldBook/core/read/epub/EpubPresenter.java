package com.tin.projectlist.app.model.oldBook.core.read.epub;

import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class EpubPresenter extends MvpPresenter<EpubContract.View> implements EpubContract.Presenter, EpubOnListener {

    EpubModel epubModel;

    @Override
    public void start() {
        epubModel=new EpubModel();
        epubModel.setListener(this);
    }

}
