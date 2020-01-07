package com.tin.projectlist.app.model.oldBook.core.shelf;

import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

import java.util.List;

public class ShelfPresenter extends MvpPresenter<ShelfContract.View> implements ShelfContract.Presenter, ShelfOnListener {


    private ShelfModel shelfModel;

    /**
     * P 层初始化方法
     */
    @Override
    public void start() {
        shelfModel=new ShelfModel();
        shelfModel.setListener(this);

    }

    @Override
    public void getLocationBookList() {
        shelfModel.getLocationBookList();
    }

    @Override
    public void openBook(Book book) {


    }

    @Override
    public void onLocationBookResult(List<Book> bookList) {
        getView().onLocationBookListResult(bookList);
    }

}
