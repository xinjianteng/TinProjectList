package com.tin.projectlist.app.model.oldBook.core.shelf;

import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

import java.util.ArrayList;
import java.util.List;

public final class ShelfModel extends MvpModel<ShelfOnListener> {


    public ShelfModel() {
    }


    public void  getLocationBookList(){
        List<Book> bookList=new ArrayList<>();
        for(int i=0;i<6;i++){
            bookList.add(new Book());
        }
        getListener().onLocationBookResult(bookList);
    }


}
