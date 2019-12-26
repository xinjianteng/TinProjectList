package com.tin.projectlist.app.model.oldBook.oldBook.core.shelf;

import com.tin.projectlist.app.model.oldBook.entity.Book;

import java.util.List;

public interface ShelfOnLIstener {

    void onLocationBookResult(List<Book> bookList);

}
