package com.tin.projectlist.app.model.oldBook.core.shelf;

import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

import java.util.List;

public class ShelfPresenter extends MvpPresenter<ShelfContract.View> implements ShelfContract.Presenter,ShelfOnLIstener {

    /**
     * P 层初始化方法
     */
    @Override
    public void start() {

    }

    @Override
    public void getLocationBookList() {

    }

    @Override
    public void onLocationBookResult(List<Book> bookList) {
        getView().onLocationBookListResult(bookList);
    }

}