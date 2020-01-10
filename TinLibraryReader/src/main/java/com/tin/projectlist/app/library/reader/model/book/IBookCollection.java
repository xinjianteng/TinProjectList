package com.tin.projectlist.app.library.reader.model.book;

import com.core.file.GBFile;
import com.core.text.widget.GBTextPosition;
import com.geeboo.read.model.bookmodel.BookReadingException;

import java.util.List;

/**
 * 类名： IBookCollection.java<br>
 * 描述： 图书相关业务信息集合<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public interface IBookCollection {
    // 图书加载状态
    public enum Status {
        NotStarted(false), Started(false), Succeeded(true), Failed(true);

        public final Boolean IsCompleted;

        Status(boolean completed) {
            IsCompleted = completed;
        }
    }

    public interface Listener {
        void onBookEvent(BookEvent event, Book book);
        void onBuildEvent(Status status);
    }

    public void addListener(Listener listener);
    public void removeListener(Listener listener);

    Status status();

    int size();
    List<Book> books();
    List<Book> booksForLabel(String label);
    List<Book> booksForAuthor(Author author);
    List<Book> booksForTag(Tag tag);
    List<Book> booksForSeries(String series);
    List<Book> booksForSeriesAndAuthor(String series, Author author);
    List<Book> booksForTitlePrefix(String prefix);
    boolean hasBooksForPattern(String pattern);
    List<Book> booksForPattern(String pattern);

    List<String> labels();
    List<String> labels(Book book);
    void setLabel(Book book, String label);
    void removeLabel(Book book, String label);

    /**
     *
     * 功能描述： 获取最近阅读图书列表<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param
     */
    List<Book> recentBooks();

    /**
     *
     * 功能描述： 通过索引获取最近图书<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param index 最近索引 0为最近 依次 1 2 3 ...
     * @return 该索引描述关联的图书
     */
    Book getRecentBook(int index);

    /**
     *
     * 功能描述： 添加图书到最近阅读图书列表<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param book 被添加图书
     */
    void addBookToRecentList(Book book);

    /**
     *
     * 功能描述： 通过文件描述获取图书信息<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param file 文件描述
     * @return 该file描述关联的图书
     */
    Book getBookByFile(GBFile file) throws BookReadingException;

    /**
     *
     * 功能描述： 通过图书编号获取图书信息<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param id 图书编号
     * @return 该图书编号关联的图书信息
     */
    Book getBookById(long id);

    Book getBookByUid(UID uid);

    List<Author> authors();
    boolean hasSeries();
    List<String> series();
    List<Tag> tags();
    List<String> titles();
    List<String> firstTitleLetters();
    List<String> titlesForAuthor(Author author, int limit);
    List<String> titlesForSeries(String series, int limit);
    List<String> titlesForSeriesAndAuthor(String series, Author author, int limit);
    List<String> titlesForTag(Tag tag, int limit);
    List<String> titlesForTitlePrefix(String prefix, int limit);

    /**
     * 、
     *
     * 功能描述： 保存图书<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param book 被保存图书
     * @param force 若数据库已有相同ID的图书是否覆盖 true 覆盖 false 不覆盖
     */
    boolean saveBook(Book book, boolean force);

    /**
     *
     * 功能描述： 删除图书<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param book 被删除图书
     * @param deleteFromDisk 是否同时删除关联图书文件 true 删除关联文件 false 不删除关联文件
     */
    void removeBook(Book book, boolean deleteFromDisk);

    /**
     *
     * 功能描述：通过图书编号获取图书最后一次阅读位置 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param bookId 图书编号
     * @return 最后一次阅读位置
     */
    GBTextPosition getStoredPosition(long bookId);

    /**
     *
     * 功能描述： 保存图书最后阅读位置<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param bookId 图书编号
     * @param position 阅读位置
     *
     */
    void storePosition(long bookId, GBTextPosition position);

    /**
     *
     * 功能描述： 判断链接是否点击过<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param book 图书
     * @param linkId 链接编号
     * @return true 已点击 false 未点击
     */
    boolean isHyperlinkVisited(Book book, String linkId);

    /**
     *
     * 功能描述： 标记链接已被点击<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param book 图书
     * @param linkId 图书链接编号
     */
    void markHyperlinkAsVisited(Book book, String linkId);
    /**
     *
     * 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param fromId
     */
    List<Bookmark> bookmarks(long fromId, int limitCount);
    List<Bookmark> bookmarksForBook(Book book, long fromId, int limitCount);

    /**
     *
     * 功能描述： 获取不可显示的图书标签<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @parambook 包含图书编号的图书
     * @return 不可显示图书标签列表
     */
    List<Bookmark> invisibleBookmarks(Book book);

    /**
     *
     * 功能描述： 获取可显示的图书书签<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param book 包含图书编号的图书
     * @return 可显示图书标签列表
     */
    List<Bookmark> visibleBookmarks(Book book);
    /**
     *
     * 功能描述： 保存图书标签<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param bookmark 被保存图书书签
     */
    void saveBookmark(Bookmark bookmark);
    /**
     *
     * 功能描述： 删除图书书签<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-13<br>
     *
     * @param bookmark 被删除图书书签
     */
    void deleteBookmark(Bookmark bookmark);

    // 新增图书笔记，高亮和批注业务
    long addAnnatitions(Annotations annotations);
    List<Annotations> loadAnotations(int bookId);
    void deleteAnnotation(int _id);
    Annotations loadAnnotationsById(int id);
}
