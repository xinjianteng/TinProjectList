package com.tin.projectlist.app.library.reader.model.parser.txt;

import com.core.common.util.IFunction;
import com.core.file.GBFile;
import com.core.file.image.GBImage;
import com.core.support.AutoEncodingCollection;
import com.core.text.widget.GBTextPosition;
import com.geeboo.read.model.book.Book;
import com.geeboo.read.model.bookmodel.BookModel;
import com.geeboo.read.model.bookmodel.BookReadingException;
import com.geeboo.read.model.parser.JavaFormatPlugin;
import com.tin.projectlist.app.library.reader.model.book.Book;
import com.tin.projectlist.app.library.reader.model.bookmodel.BookModel;
import com.tin.projectlist.app.library.reader.model.bookmodel.BookReadingException;
import com.tin.projectlist.app.library.reader.model.parser.JavaFormatPlugin;
import com.tin.projectlist.app.library.reader.parser.common.util.IFunction;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;
import com.tin.projectlist.app.library.reader.parser.support.AutoEncodingCollection;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;

/**
 * 类名： TxtPlugin.java<br>
 * 描述： txt文件解析插件<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TxtPlugin extends JavaFormatPlugin {
    public TxtPlugin() {
        super("plain text");
    }

    @Override
    public void readMetaInfo(Book book) throws BookReadingException {
        // new OEBMetaInfoReader(book).readMetaInfo(getOpfFile(book.File));
    }

    TxtBookChapReader txtBookChapReader = null;
    @Override
    public void readModel(BookModel model, GBTextPosition lastPosition) throws BookReadingException {
        model.Book.File.setCached(true);
        // 解析txt文件存储
        // new TxtBookReader(model).readBook(model.Book.File);
        txtBookChapReader = new TxtBookChapReader(model);
        txtBookChapReader.readBook(model.Book.File, lastPosition);
    }

    @Override
    public GBImage readCover(GBFile file) {
        return null;
    }

    @Override
    public String readAnnotation(GBFile file) {
        return "";
    }

    @Override
    public AutoEncodingCollection supportedEncodings() {
        return new AutoEncodingCollection();
    }

    @Override
    public void detectLanguageAndEncoding(Book book) {
        book.setEncoding("auto");
    }

    @Override
    public void readModel(GBTextPosition lastPosition, IFunction<Integer> function) throws BookReadingException {
        if (null != txtBookChapReader) {
            txtBookChapReader.readBookByChpFileIndex(lastPosition.getChpFileIndex(), function);
        }

    }

    @Override
    public void stopReadMode() {
        txtBookChapReader.stopRead(true);

    }

    @Override
    public boolean isLoadChp(int chpFileIndex) {
        return txtBookChapReader.isLoadChp(chpFileIndex);
    }

    @Override
    public void startBuildCache() {
        txtBookChapReader.startBuildCache();

    }

    @Override
    public void getReadProgress(IFunction<Integer> handler) {
        txtBookChapReader.setmProgressHander(handler);

    }
}
