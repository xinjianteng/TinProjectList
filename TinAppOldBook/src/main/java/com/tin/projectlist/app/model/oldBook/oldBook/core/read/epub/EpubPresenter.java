package com.tin.projectlist.app.model.oldBook.oldBook.core.read.epub;

import com.tin.projectlist.app.model.oldBook.core.read.epub.EpubContract;
import com.tin.projectlist.app.model.oldBook.core.read.epub.EpubModel;
import com.tin.projectlist.app.model.oldBook.core.read.epub.EpubOnListener;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class EpubPresenter extends MvpPresenter<com.tin.projectlist.app.model.oldBook.core.read.epub.EpubContract.View> implements EpubContract.Presenter, EpubOnListener {

    com.tin.projectlist.app.model.oldBook.core.read.epub.EpubModel epubModel;

    @Override
    public void start() {
        epubModel=new EpubModel();
        epubModel.setListener(this);
    }

}
