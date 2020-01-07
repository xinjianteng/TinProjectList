package com.tin.projectlist.app.model.oldBook.core.shelf;

import com.tin.projectlist.app.library.base.utils.FileUtils;
import com.tin.projectlist.app.library.reader.GeeBookLoader;
import com.tin.projectlist.app.library.reader.parser.common.util.BookDescriptor;
import com.tin.projectlist.app.model.oldBook.common.MyApplication;
import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ShelfPresenter extends MvpPresenter<ShelfContract.View> implements ShelfContract.Presenter, ShelfOnListener {


    private ShelfModel shelfModel;

    /**
     * P 层初始化方法
     */
    @Override
    public void start() {
        shelfModel = new ShelfModel();
        shelfModel.setListener(this);

    }

    @Override
    public void getLocationBookList() {
        shelfModel.getLocationBookList();
    }

    @Override
    public void openBook(Book book) throws Exception {
        InputStream abpath = getClass().getResourceAsStream("/assets/test.epub");
        String path = new String(InputStreamToByte(abpath));
        BookDescriptor bookDescriptor = new BookDescriptor(path, "测试图书",
                 20);
        GeeBookLoader.execEpub(MyApplication.getContext(), bookDescriptor);
    }

    @Override
    public void onLocationBookResult(List<Book> bookList) {
        getView().onLocationBookListResult(bookList);
    }


    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }

}
