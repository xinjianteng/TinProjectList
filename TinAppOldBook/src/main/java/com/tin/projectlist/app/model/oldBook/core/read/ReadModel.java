package com.tin.projectlist.app.model.oldBook.core.read;

import android.content.Context;

import com.tin.projectlist.app.model.oldBook.constant.PathConstant;
import com.tin.projectlist.app.model.oldBook.core.read.ReadOnListener;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;
import com.tin.projectlist.app.model.oldBook.readingTool.BookFileUtils;

import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

public class ReadModel extends MvpModel<ReadOnListener> {

    Context mContext;

    public ReadModel(Context mContext) {
        this.mContext = mContext;
    }

    public void loadBook(String filePath) {
        try {
            String mFileName =BookFileUtils.getFileNameForPath(filePath);
            // 打开书籍
            EpubReader reader = new EpubReader();
            Book mBook = reader.readEpub(new FileInputStream(filePath));
            // 解压epub至缓存目录
            BookFileUtils.unzipFile(filePath, PathConstant.PATH_EPUB + "/" + mFileName);
            getListener().loadBookResult(mBook);
        } catch (IOException e) {
            e.printStackTrace();
            getListener().loadBookResult(null);
        }
    }


}
