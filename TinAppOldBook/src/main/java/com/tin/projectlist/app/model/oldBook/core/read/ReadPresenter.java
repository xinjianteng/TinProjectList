package com.tin.projectlist.app.model.oldBook.core.read;

import android.content.Context;

import com.tin.projectlist.app.model.oldBook.constant.PathConstant;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;
import com.tin.projectlist.app.model.oldBook.readingTool.BookFileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class ReadPresenter extends MvpPresenter<ReadContract.View> implements ReadContract.Presenter,ReadOnListener{

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
