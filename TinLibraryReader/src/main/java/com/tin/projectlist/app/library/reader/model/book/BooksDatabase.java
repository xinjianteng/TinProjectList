package com.tin.projectlist.app.library.reader.model.book;

import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 类名： BooksDatabase.java<br>
 * 描述： 图书业务数据存储抽象定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class BooksDatabase {
    protected Book createBook(long id, long fileId, String title, String encoding, String language) {
        final FileInfoSet infos = new FileInfoSet(this, fileId);
        return createBook(id, infos.getFile(fileId), title, encoding, language);
    }
    protected Book createBook(long id, GBFile file, String title, String encoding, String language) {
        return (file != null) ? new Book(id, file, title, encoding, language) : null;
    }
    protected void addAuthor(Book book, Author author) {
        book.addAuthorWithNoCheck(author);
    }
    protected void addTag(Book book, Tag tag) {
        book.addTagWithNoCheck(tag);
    }
    protected void setSeriesInfo(Book book, String series, String index) {
        book.setSeriesInfoWithNoCheck(series, index);
    }

    protected abstract void executeAsTransaction(Runnable actions);

    // returns map fileId -> book
    protected abstract Map<Long, Book> loadBooks(FileInfoSet infos, boolean existing);
    protected abstract void setExistingFlag(Collection<Book> books, boolean flag);
    protected abstract Book loadBook(long bookId);
    protected abstract Book loadBookByFile(long fileId, GBFile file);

    protected abstract List<Author> listAuthors(long bookId);
    protected abstract List<Tag> listTags(long bookId);
    protected abstract SeriesInfo getSeriesInfo(long bookId);
    protected abstract List<UID> listUids(long bookId);

    protected abstract Long bookIdByUid(UID uid);

    protected abstract void updateBookInfo(long bookId, long fileId, String encoding, String language, String title);
    protected abstract long insertBookInfo(GBFile file, String encoding, String language, String title);
    protected abstract void deleteAllBookAuthors(long bookId);
    protected abstract void saveBookAuthorInfo(long bookId, long index, Author author);
    protected abstract void deleteAllBookTags(long bookId);
    protected abstract void saveBookTagInfo(long bookId, Tag tag);
    protected abstract void saveBookSeriesInfo(long bookId, SeriesInfo seriesInfo);
    protected abstract void saveBookUid(long bookId, UID uid);

    protected FileInfo createFileInfo(long id, String name, FileInfo parent) {
        return new FileInfo(name, parent, id);
    }

    protected abstract Collection<FileInfo> loadFileInfos();
    protected abstract Collection<FileInfo> loadFileInfos(GBFile file);
    protected abstract Collection<FileInfo> loadFileInfos(long fileId);
    protected abstract void removeFileInfo(long fileId);
    protected abstract void saveFileInfo(FileInfo fileInfo);

    protected abstract List<Long> loadRecentBookIds();
    protected abstract void saveRecentBookIds(final List<Long> ids);

    protected abstract List<Long> loadBooksForLabelIds(String label);
    protected abstract List<String> labels();
    protected abstract List<String> labels(long bookId);
    protected abstract void setLabel(long bookId, String label);
    protected abstract void removeLabel(long bookId, String label);

    protected Bookmark createBookmark(long id, long bookId, String bookTitle, String text, long creationDate,
                                      long modificationDate, long accessDate, int accessCounter, String modelId, int chpFileIndex,
                                      int paragraphIndex, int wordIndex, int charIndex, boolean isVisible) {
        return new Bookmark(id, bookId, bookTitle, text, creationDate, modificationDate, accessDate, accessCounter,
                modelId, chpFileIndex, paragraphIndex, wordIndex, charIndex, isVisible);
    }

    /**
     * 功能描述： 加载书签<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-14<br>
     *
     * @param bookId 图书id
     * @param isVisible 是否可见
     * @return
     */
    protected abstract List<Bookmark> loadBookmarks(long bookId, boolean isVisible);
    protected abstract List<Bookmark> loadVisibleBookmarks(long fromId, int limitCount);
    protected abstract List<Bookmark> loadVisibleBookmarks(long bookId, long fromId, int limitCount);
    protected abstract long saveBookmark(Bookmark bookmark);
    protected abstract void deleteBookmark(Bookmark bookmark);
    // 获取阅读位置
    protected abstract GBTextPosition getStoredPosition(long bookId);
    // 存储阅读位置
    protected abstract void storePosition(long bookId, GBTextPosition position);
    // 获取访问的链接
    protected abstract Collection<String> loadVisitedHyperlinks(long bookId);
    // 添加访问链接
    protected abstract void addVisitedHyperlink(long bookId, String hyperlinkId);

    /**
     * add by jack 新增批注，高亮和笔记支持业务
     */
    protected abstract long addAnnatitions(Annotations annotations);
    protected abstract List<Annotations> loadAnotations(int bookId);
    protected abstract void deleteAnnotation(int _id);
    protected abstract Annotations getAnnotationById(int _id);
}
