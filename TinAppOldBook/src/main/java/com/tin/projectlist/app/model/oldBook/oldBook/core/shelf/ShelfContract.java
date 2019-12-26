package com.tin.projectlist.app.model.oldBook.oldBook.core.shelf;

import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

import java.util.List;

public class ShelfContract {


    public interface View extends IMvpView {
        void onLocationBookListResult(List<Book> bookList);
    }


    public interface Presenter {
        void getLocationBookList();
    }

}
