package com.tin.projectlist.app.model.oldBook.core.book;

import com.tin.projectlist.app.model.oldBook.entity.BookComment;
import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

import java.util.List;

public class BookDetailContract {

    public interface View extends IMvpView {
        void getBookCommentResult(boolean result, List<BookComment> bookCommentList);

    }

    public interface Presenter{
        void  getBookCommentForBookId(String bookid);
    }

}
