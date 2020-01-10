package com.tin.projectlist.app.library.reader.model.book;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import com.core.file.GBFile;
import com.core.file.GBPhysicalFile;
import com.core.text.widget.GBTextPosition;
import com.geeboo.read.model.bookmodel.BookReadingException;
import com.geeboo.read.model.parser.FormatPlugin;
import com.geeboo.read.model.parser.PluginCollection;

/**
 * 类名： BookCollection.java<br>
 * 描述： 业务接口<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class BookCollection extends AbstractBookCollection {
    private final BooksDatabase myDatabase;
    public final List<String> BookDirectories;

    private final Map<GBFile, Book> myBooksByFile = Collections.synchronizedMap(new LinkedHashMap<GBFile, Book>());
    private final Map<Long, Book> myBooksById = Collections.synchronizedMap(new HashMap<Long, Book>());
    private final List<String> myFilesToRescan = Collections.synchronizedList(new LinkedList<String>());

    private volatile Status myStatus = Status.NotStarted;

    public BookCollection(BooksDatabase db, List<String> bookDirectories) {
        if (bookDirectories == null)
            bookDirectories = Collections.EMPTY_LIST;
        myDatabase = db;
        BookDirectories = Collections.unmodifiableList(new ArrayList<String>(bookDirectories));
    }

    public int size() {
        return myBooksByFile.size();
    }

    @Override
    public Book getBookByFile(GBFile bookFile) throws BookReadingException {
        if (bookFile == null) {
            throw new BookReadingException("errorReadingFile", bookFile);
        }
        final FormatPlugin plugin = PluginCollection.Instance().getPlugin(bookFile);
        if (plugin == null) {
            // L.i("BookCollection", "plugin is null");
            // return null;
            throw new BookReadingException("errorReadingFile", bookFile);
        }
        bookFile = plugin.realBookFile(bookFile);

        Book book = myBooksByFile.get(bookFile);
        if (book != null) {
            return book;
        }

        final GBPhysicalFile physicalFile = bookFile.getPhysicalFile();
        if (physicalFile != null && !physicalFile.exists()) {
            throw new BookReadingException("errorReadingFile", bookFile);
        }

        final FileInfoSet fileInfos = new FileInfoSet(myDatabase, bookFile);

        book = myDatabase.loadBookByFile(fileInfos.getId(bookFile), bookFile);
        if (book != null) {
            book.loadLists(myDatabase);
        }

        if (book != null && fileInfos.check(physicalFile, physicalFile != bookFile)) {
            saveBook(book, false);
            // saved
            addBook(book, false);
            return book;
        }
        fileInfos.save();

        if (book == null) {
            book = new Book(bookFile);
        } else {
            book.readMetaInfo();
        }

        saveBook(book, false);
        return book;
    }
    public Book getBookById(long id) {
        Book book = myBooksById.get(id);
        if (book != null) {
            return book;
        }

        book = myDatabase.loadBook(id);
        if (book == null) {
            return null;
        }
        book.loadLists(myDatabase);

        final GBFile bookFile = book.File;
        final GBPhysicalFile physicalFile = bookFile.getPhysicalFile();
        if (physicalFile == null) {
            // loaded from db
            addBook(book, false);
            return book;
        }
        if (!physicalFile.exists()) {
            return null;
        }

        FileInfoSet fileInfos = new FileInfoSet(myDatabase, physicalFile);
        if (fileInfos.check(physicalFile, physicalFile != bookFile)) {
            // loaded from db
            addBook(book, false);
            return book;
        }
        fileInfos.save();

        try {
            book.readMetaInfo();
            // loaded from db
            addBook(book, false);
            return book;
        } catch (BookReadingException e) {
            return null;
        }
    }

    public Book getBookByUid(UID uid) {
        for (Book book : myBooksById.values()) {
            if (book.matchesUid(uid)) {
                return book;
            }
        }
        final Long bookId = myDatabase.bookIdByUid(uid);
        return bookId != null ? getBookById(bookId) : null;
    }

    private void addBook(Book book, boolean force) {
        if (book == null || book.getId() == -1) {
            return;
        }

        synchronized (myBooksByFile) {
            final Book existing = myBooksByFile.get(book.File);
            if (existing == null) {
                myBooksByFile.put(book.File, book);
                myBooksById.put(book.getId(), book);
                fireBookEvent(BookEvent.Added, book);
            } else if (force) {
                existing.updateFrom(book);
                fireBookEvent(BookEvent.Updated, existing);
            }
        }
    }

    public boolean saveBook(Book book, boolean force) {
        if (book == null) {
            return false;
        }

        final boolean result = book.save(myDatabase, force);
        addBook(book, true);
        return result;
    }

    public void removeBook(Book book, boolean deleteFromDisk) {
        synchronized (myBooksByFile) {
            myBooksByFile.remove(book.File);
            myBooksById.remove(book.getId());

            final List<Long> ids = myDatabase.loadRecentBookIds();
            if (ids.remove(book.getId())) {
                myDatabase.saveRecentBookIds(ids);
            }
            if (deleteFromDisk) {
                book.File.getPhysicalFile().delete();
            }
        }
        fireBookEvent(BookEvent.Removed, book);
    }

    public Status status() {
        return myStatus;
    }

    public List<Book> books() {
        synchronized (myBooksByFile) {
            return new ArrayList<Book>(myBooksByFile.values());
        }
    }

    public List<Book> booksForAuthor(Author author) {
        final boolean isNull = Author.NULL.equals(author);
        final LinkedList<Book> filtered = new LinkedList<Book>();
        for (Book b : books()) {
            final List<Author> bookAuthors = b.authors();
            if (isNull && bookAuthors.isEmpty() || bookAuthors.contains(author)) {
                filtered.add(b);
            }
        }
        return filtered;
    }

    public List<Book> booksForTag(Tag tag) {
        final boolean isNull = Tag.NULL.equals(tag);
        final LinkedList<Book> filtered = new LinkedList<Book>();
        for (Book b : books()) {
            final List<Tag> bookTags = b.tags();
            if (isNull && bookTags.isEmpty() || bookTags.contains(tag)) {
                filtered.add(b);
            }
        }
        return filtered;
    }

    public List<Book> booksForSeries(String series) {
        final LinkedList<Book> filtered = new LinkedList<Book>();
        for (Book b : books()) {
            final SeriesInfo info = b.getSeriesInfo();
            if (info != null && series.equals(info.Series.getTitle())) {
                filtered.add(b);
            }
        }
        return filtered;
    }

    public List<Book> booksForSeriesAndAuthor(String series, Author author) {
        final boolean isNull = Author.NULL.equals(author);
        final LinkedList<Book> filtered = new LinkedList<Book>();
        for (Book b : books()) {
            final List<Author> bookAuthors = b.authors();
            final SeriesInfo info = b.getSeriesInfo();
            if (info != null && series.equals(info.Series.getTitle())
                    && (isNull && bookAuthors.isEmpty() || bookAuthors.contains(author))) {
                filtered.add(b);
            }
        }
        return filtered;
    }

    public List<Book> booksForTitlePrefix(String prefix) {
        if (prefix == null) {
            return Collections.emptyList();
        }
        final LinkedList<Book> filtered = new LinkedList<Book>();
        for (Book b : books()) {
            if (b != null && prefix.equals(b.firstTitleLetter())) {
                filtered.add(b);
            }
        }
        return filtered;
    }

    public boolean hasBooksForPattern(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            return false;
        }
        pattern = pattern.toLowerCase();

        for (Book b : books()) {
            if (b.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    public List<Book> booksForPattern(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            return Collections.emptyList();
        }
        pattern = pattern.toLowerCase();

        final LinkedList<Book> filtered = new LinkedList<Book>();
        for (Book b : books()) {
            if (b.matches(pattern)) {
                filtered.add(b);
            }
        }
        return filtered;
    }

    public List<Book> recentBooks() {
        return books(myDatabase.loadRecentBookIds());
    }

    public List<Book> booksForLabel(String label) {
        return books(myDatabase.loadBooksForLabelIds(label));
    }

    private List<Book> books(List<Long> ids) {
        final List<Book> bookList = new ArrayList<Book>(ids.size());
        for (long id : ids) {
            final Book book = getBookById(id);
            if (book != null) {
                bookList.add(book);
            }
        }
        return bookList;
    }

    public List<Author> authors() {
        final Set<Author> authors = new TreeSet<Author>();
        synchronized (myBooksByFile) {
            for (Book book : myBooksByFile.values()) {
                final List<Author> bookAuthors = book.authors();
                if (bookAuthors.isEmpty()) {
                    authors.add(Author.NULL);
                } else {
                    authors.addAll(bookAuthors);
                }
            }
        }
        return new ArrayList<Author>(authors);
    }

    public List<Tag> tags() {
        final Set<Tag> tags = new HashSet<Tag>();
        synchronized (myBooksByFile) {
            for (Book book : myBooksByFile.values()) {
                final List<Tag> bookTags = book.tags();
                if (bookTags.isEmpty()) {
                    tags.add(Tag.NULL);
                } else {
                    for (Tag t : bookTags) {
                        for (; t != null; t = t.Parent) {
                            tags.add(t);
                        }
                    }
                }
            }
        }
        return new ArrayList<Tag>(tags);
    }

    public boolean hasSeries() {
        synchronized (myBooksByFile) {
            for (Book book : myBooksByFile.values()) {
                if (book.getSeriesInfo() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> series() {
        final Set<String> series = new TreeSet<String>();
        synchronized (myBooksByFile) {
            for (Book book : myBooksByFile.values()) {
                final SeriesInfo info = book.getSeriesInfo();
                if (info != null) {
                    series.add(info.Series.getTitle());
                }
            }
        }
        return new ArrayList<String>(series);
    }

    public List<String> titles() {
        synchronized (myBooksByFile) {
            final List<String> titles = new ArrayList<String>(myBooksByFile.size());
            for (Book book : myBooksByFile.values()) {
                titles.add(book.getTitle());
            }
            return titles;
        }
    }

    public List<String> firstTitleLetters() {
        synchronized (myBooksByFile) {
            final TreeSet<String> letters = new TreeSet<String>();
            for (Book book : myBooksByFile.values()) {
                final String l = book.firstTitleLetter();
                if (l != null) {
                    letters.add(l);
                }
            }
            return new ArrayList<String>(letters);
        }
    }

    public List<String> titlesForAuthor(Author author, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        final ArrayList<String> titles = new ArrayList<String>(limit);
        final boolean isNull = Author.NULL.equals(author);
        synchronized (myBooksByFile) {
            for (Book b : myBooksByFile.values()) {
                if (isNull ? b.authors().isEmpty() : b.authors().contains(author)) {
                    titles.add(b.getTitle());
                    if (--limit == 0) {
                        break;
                    }
                }
            }
        }
        return titles;
    }

    public List<String> titlesForSeries(String series, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        final ArrayList<String> titles = new ArrayList<String>(limit);
        synchronized (myBooksByFile) {
            for (Book b : myBooksByFile.values()) {
                final SeriesInfo info = b.getSeriesInfo();
                if (info != null && series.equals(info.Series.getTitle())) {
                    titles.add(b.getTitle());
                    if (--limit == 0) {
                        break;
                    }
                }
            }
        }
        return titles;
    }

    public List<String> titlesForSeriesAndAuthor(String series, Author author, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        final boolean isNull = Author.NULL.equals(author);
        final ArrayList<String> titles = new ArrayList<String>(limit);
        synchronized (myBooksByFile) {
            for (Book b : myBooksByFile.values()) {
                final List<Author> bookAuthors = b.authors();
                final SeriesInfo info = b.getSeriesInfo();
                if (info != null && series.equals(info.Series.getTitle())
                        && (isNull && bookAuthors.isEmpty() || bookAuthors.contains(author))) {
                    titles.add(b.getTitle());
                    if (--limit == 0) {
                        break;
                    }
                }
            }
        }
        return titles;
    }

    public List<String> titlesForTag(Tag tag, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        final ArrayList<String> titles = new ArrayList<String>(limit);
        final boolean isNull = Tag.NULL.equals(tag);
        synchronized (myBooksByFile) {
            for (Book b : myBooksByFile.values()) {
                if (isNull ? b.tags().isEmpty() : b.tags().contains(tag)) {
                    titles.add(b.getTitle());
                    if (--limit == 0) {
                        break;
                    }
                }
            }
        }
        return titles;
    }

    public List<String> titlesForTitlePrefix(String prefix, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        final ArrayList<String> titles = new ArrayList<String>(limit);
        synchronized (myBooksByFile) {
            for (Book b : myBooksByFile.values()) {
                if (prefix.equals(b.firstTitleLetter())) {
                    titles.add(b.getTitle());
                    if (--limit == 0) {
                        break;
                    }
                }
            }
        }
        return titles;
    }

    public Book getRecentBook(int index) {
        List<Long> recentIds = myDatabase.loadRecentBookIds();
        return recentIds.size() > index ? getBookById(recentIds.get(index)) : null;
    }

    public void addBookToRecentList(Book book) {
        final List<Long> ids = myDatabase.loadRecentBookIds();
        final Long bookId = book.getId();
        ids.remove(bookId);
        ids.add(0, bookId);
        if (ids.size() > 12) {
            ids.remove(12);
        }
        myDatabase.saveRecentBookIds(ids);
    }

    public List<String> labels() {
        return myDatabase.labels();
    }

    public List<String> labels(Book book) {
        if (book == null) {
            return Collections.<String> emptyList();
        }
        return myDatabase.labels(book.getId());
    }

    public void setLabel(Book book, String label) {
        myDatabase.setLabel(book.getId(), label);
        fireBookEvent(BookEvent.Updated, book);
    }

    public void removeLabel(Book book, String label) {
        myDatabase.removeLabel(book.getId(), label);
        fireBookEvent(BookEvent.Updated, book);
    }

    private void setStatus(Status status) {
        myStatus = status;
        fireBuildEvent(status);
    }

    public synchronized void startBuild() {
        if (myStatus != Status.NotStarted) {
            return;
        }
        setStatus(Status.Started);

        final Thread builder = new Thread("Library.build") {
            public void run() {
                try {
                    build();
                    setStatus(Status.Succeeded);
                } catch (Throwable t) {
                    setStatus(Status.Failed);
                } finally {
                    synchronized (myFilesToRescan) {
                        processFilesQueue();
                    }
                }
            }
        };
        builder.setPriority(Thread.MIN_PRIORITY);
        builder.start();
    }

    public void rescan(String path) {
        synchronized (myFilesToRescan) {
            myFilesToRescan.add(path);
            processFilesQueue();
        }
    }

    private void processFilesQueue() {
        synchronized (myFilesToRescan) {
            if (!myStatus.IsCompleted) {
                return;
            }

            final Set<GBFile> filesToRemove = new HashSet<GBFile>();
            for (String path : myFilesToRescan) {
                path = new GBPhysicalFile(new File(path)).getPath();
                synchronized (myBooksByFile) {
                    for (GBFile f : myBooksByFile.keySet()) {
                        if (f.getPath().startsWith(path)) {
                            filesToRemove.add(f);
                        }
                    }
                }
            }

            for (GBFile file : collectPhysicalFiles(myFilesToRescan)) {
                // TODO:
                // collect books from archives
                // rescan files and check book id
                filesToRemove.remove(file);
                Book book;
                try {
                    book = getBookByFile(file);
                } catch (BookReadingException e) {
                    continue;
                }
                if (book != null) {
                    saveBook(book, false);
                }
            }

            for (GBFile f : filesToRemove) {
                synchronized (myBooksByFile) {
                    final Book book = myBooksByFile.remove(f);
                    if (book != null) {
                        myBooksById.remove(book.getId());
                        fireBookEvent(BookEvent.Removed, book);
                    }
                }
            }

            myFilesToRescan.clear();
        }
    }

    private void build() {
        // Step 0: get database books marked as "existing"
        final FileInfoSet fileInfos = new FileInfoSet(myDatabase);
        final Map<Long, Book> savedBooksByFileId = myDatabase.loadBooks(fileInfos, true);
        final Map<Long, Book> savedBooksByBookId = new HashMap<Long, Book>();
        for (Book b : savedBooksByFileId.values()) {
            savedBooksByBookId.put(b.getId(), b);
        }

        // Step 1: check if files corresponding to "existing" books really
        // exists;
        // add books to library if yes (and reload book info if needed);
        // remove from recent/favorites list if no;
        // collect newly "orphaned" books
        final Set<Book> orphanedBooks = new HashSet<Book>();
        final Set<GBPhysicalFile> physicalFiles = new HashSet<GBPhysicalFile>();
        int count = 0;
        for (Book book : savedBooksByFileId.values()) {
            final GBPhysicalFile file = book.File.getPhysicalFile();
            if (file != null) {
                physicalFiles.add(file);
            }
            if (file != book.File && file != null && file.getPath().endsWith(".epub")) {
                continue;
            }
            if (book.File.exists()) {
                boolean doAdd = true;
                if (file == null) {
                    continue;
                }
                if (!fileInfos.check(file, true)) {
                    try {
                        book.readMetaInfo();
                        saveBook(book, false);
                    } catch (BookReadingException e) {
                        doAdd = false;
                    }
                    file.setCached(false);
                }
                if (doAdd) {
                    // loaded from db
                    addBook(book, false);
                }
            } else {
                orphanedBooks.add(book);
            }
        }
        myDatabase.setExistingFlag(orphanedBooks, false);

        // Step 2: collect books from physical files; add new, update already
        // added,
        // unmark orphaned as existing again, collect newly added
        final Map<Long, Book> orphanedBooksByFileId = myDatabase.loadBooks(fileInfos, false);
        final Set<Book> newBooks = new HashSet<Book>();

        final List<GBPhysicalFile> physicalFilesList = collectPhysicalFiles(BookDirectories);
        for (GBPhysicalFile file : physicalFilesList) {
            if (physicalFiles.contains(file)) {
                continue;
            }
            collectBooks(file, fileInfos, savedBooksByFileId, orphanedBooksByFileId, newBooks,
                    !fileInfos.check(file, true));
            file.setCached(false);
        }

        // Step 3: add help file
        try {
            final GBFile helpFile = BookUtil.getHelpFile();
            Book helpBook = savedBooksByFileId.get(fileInfos.getId(helpFile));
            if (helpBook == null) {
                helpBook = new Book(helpFile);
            }
            saveBook(helpBook, false);
            // saved
            addBook(helpBook, false);
        } catch (BookReadingException e) {
            // that's impossible
            e.printStackTrace();
        }

        // Step 4: save changes into database
        fileInfos.save();

        myDatabase.executeAsTransaction(new Runnable() {
            public void run() {
                for (Book book : newBooks) {
                    saveBook(book, false);
                }
            }
        });
        myDatabase.setExistingFlag(newBooks, true);
    }

    private List<GBPhysicalFile> collectPhysicalFiles(List<String> paths) {
        final Queue<GBPhysicalFile> fileQueue = new LinkedList<GBPhysicalFile>();
        final HashSet<GBPhysicalFile> dirSet = new HashSet<GBPhysicalFile>();
        final LinkedList<GBPhysicalFile> fileList = new LinkedList<GBPhysicalFile>();

        for (String p : paths) {
            fileQueue.offer(new GBPhysicalFile(new File(p)));
        }

        while (!fileQueue.isEmpty()) {
            final GBPhysicalFile entry = fileQueue.poll();
            if (!entry.exists()) {
                continue;
            }
            if (entry.isDirectory()) {
                if (dirSet.contains(entry)) {
                    continue;
                }
                dirSet.add(entry);
                for (GBFile file : entry.children()) {
                    fileQueue.add((GBPhysicalFile) file);
                }
            } else {
                entry.setCached(true);
                fileList.add(entry);
            }
        }

        return fileList;
    }

    private void collectBooks(GBFile file, FileInfoSet fileInfos, Map<Long, Book> savedBooksByFileId,
                              Map<Long, Book> orphanedBooksByFileId, Set<Book> newBooks, boolean doReadMetaInfo) {
        final long fileId = fileInfos.getId(file);
        if (savedBooksByFileId.get(fileId) != null) {
            return;
        }

        try {
            final Book book = orphanedBooksByFileId.get(fileId);
            if (book != null) {
                if (doReadMetaInfo) {
                    book.readMetaInfo();
                }
                newBooks.add(book);
                return;
            }
        } catch (BookReadingException e) {
            // ignore
        }

        try {
            final Book book = new Book(file);
            newBooks.add(book);
            return;
        } catch (BookReadingException e) {
            // ignore
        }

        if (file.isArchive()) {
            for (GBFile entry : fileInfos.archiveEntries(file)) {
                collectBooks(entry, fileInfos, savedBooksByFileId, orphanedBooksByFileId, newBooks, doReadMetaInfo);
            }
        }
    }

    public List<Bookmark> bookmarks(long fromId, int limitCount) {
        return myDatabase.loadVisibleBookmarks(fromId, limitCount);
    }

    public List<Bookmark> bookmarksForBook(Book book, long fromId, int limitCount) {
        return myDatabase.loadVisibleBookmarks(book.getId(), fromId, limitCount);
    }

    public List<Bookmark> invisibleBookmarks(Book book) {
        final List<Bookmark> list = myDatabase.loadBookmarks(book.getId(), false);
        Collections.sort(list, new Bookmark.ByTimeComparator());
        return list;
    }

    public List<Bookmark> visibleBookmarks(Book book) {
        final List<Bookmark> list = myDatabase.loadBookmarks(book.getId(), true);
        Collections.sort(list, new Bookmark.ByTimeComparator());
        return list;
    }

    public void saveBookmark(Bookmark bookmark) {
        if (bookmark != null) {
            bookmark.setId(myDatabase.saveBookmark(bookmark));
        }
    }

    public void deleteBookmark(Bookmark bookmark) {
        if (bookmark != null && bookmark.getId() != -1) {
            myDatabase.deleteBookmark(bookmark);
        }
    }

    public GBTextPosition getStoredPosition(long bookId) {
        return myDatabase.getStoredPosition(bookId);
    }

    public void storePosition(long bookId, GBTextPosition position) {
        if (bookId != -1) {
            myDatabase.storePosition(bookId, position);
        }
    }

    public boolean isHyperlinkVisited(Book book, String linkId) {
        return book.isHyperlinkVisited(myDatabase, linkId);
    }

    public void markHyperlinkAsVisited(Book book, String linkId) {
        book.markHyperlinkAsVisited(myDatabase, linkId);
    }

    @Override
    public long addAnnatitions(Annotations annotations) {
        return myDatabase.addAnnatitions(annotations);
    }

    @Override
    public List<Annotations> loadAnotations(int bookId) {
        return myDatabase.loadAnotations(bookId);
    }

    @Override
    public void deleteAnnotation(int _id) {
        myDatabase.deleteAnnotation(_id);
    }

    @Override
    public Annotations loadAnnotationsById(int _id) {
        return myDatabase.getAnnotationById(_id);
    }
}
