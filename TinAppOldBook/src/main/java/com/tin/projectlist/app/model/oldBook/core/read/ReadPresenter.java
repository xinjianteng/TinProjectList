package com.tin.projectlist.app.model.oldBook.core.read;

import android.content.Context;

import com.tin.projectlist.app.model.oldBook.core.read.ReadContract;
import com.tin.projectlist.app.model.oldBook.core.read.ReadModel;
import com.tin.projectlist.app.model.oldBook.core.read.ReadOnListener;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

import nl.siegmann.epublib.domain.Book;

public class ReadPresenter extends MvpPresenter<ReadContract.View> implements ReadContract.Presenter, ReadOnListener {

    ReadModel readModel;
    private Context mContext;

    public ReadPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void start() {
        readModel=new ReadModel(mContext);
        readModel.setListener(this);
    }

    @Override
    public void loadBook(String filePath) {
        getView().onLoading();
        readModel.loadBook(filePath);
    }


    @Override
    public void loadBookResult(Book book) {
        getView().onComplete();
        getView().loadBookResult(book);
    }


}
