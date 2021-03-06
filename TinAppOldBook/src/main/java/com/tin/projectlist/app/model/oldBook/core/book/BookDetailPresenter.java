package com.tin.projectlist.app.model.oldBook.core.book;

import com.tin.projectlist.app.model.oldBook.entity.BookComment;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

import java.util.List;

public class BookDetailPresenter  extends MvpPresenter<BookDetailContract.View> implements BookDetailContract.Presenter, BookDetailOnListener {


    BookDetailModel bookDetailModel;

    @Override
    public void start() {
        bookDetailModel=new BookDetailModel();
        bookDetailModel.setListener(this);
    }


    @Override
    public void getBookCommendList(boolean result, List<BookComment> bookCommentList) {
    }


}
