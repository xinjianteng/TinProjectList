package com.tin.projectlist.app.model.oldBook.core.read;

import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

import nl.siegmann.epublib.domain.Book;

public class ReadContract {

    public  interface  View extends IMvpView{

        void loadBookResult(Book book);

    }


    public interface Presenter{

        void loadBook(String filePath);



    }

}


