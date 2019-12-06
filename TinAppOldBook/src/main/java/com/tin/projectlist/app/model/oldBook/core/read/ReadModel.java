package com.tin.projectlist.app.model.oldBook.core.read;

import android.content.Context;

import com.tin.projectlist.app.model.oldBook.constant.PathConstant;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;
import com.tin.projectlist.app.model.oldBook.readingTool.BookFileUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

public class ReadModel extends MvpModel<ReadOnListener> {

    Context mContext;

    public ReadModel(Context mContext) {
        this.mContext = mContext;
    }

    public void loadBook(String filePath) {
        try {
            String mFileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
            // 打开书籍
            EpubReader reader = new EpubReader();
            InputStream is = new FileInputStream(filePath);
            Book mBook = reader.readEpub(is);
            // 解压epub至缓存目录
            BookFileUtils.unzipFile(filePath, PathConstant.PATH_EPUB + "/" + mFileName);
            getListener().loadBookResult(mBook);
        } catch (IOException e) {
            e.printStackTrace();
            getListener().loadBookResult(null);
        }
    }


}
