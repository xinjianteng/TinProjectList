package com.tin.projectlist.app.library.reader.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


import com.tin.projectlist.app.library.reader.model.book.Annotations;
import com.tin.projectlist.app.library.reader.model.book.Author;
import com.tin.projectlist.app.library.reader.model.book.Book;
import com.tin.projectlist.app.library.reader.model.book.Bookmark;
import com.tin.projectlist.app.library.reader.model.book.BooksDatabase;
import com.tin.projectlist.app.library.reader.model.book.FileInfo;
import com.tin.projectlist.app.library.reader.model.book.FileInfoSet;
import com.tin.projectlist.app.library.reader.model.book.SeriesInfo;
import com.tin.projectlist.app.library.reader.model.book.Tag;
import com.tin.projectlist.app.library.reader.model.book.UID;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextFixedPosition;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextPosition;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 类名： SQLiteBooksDatabase.java<br>
 * 描述： 图书信息数据持久层<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class SQLiteBooksDatabase extends BooksDatabase {
	private static BooksDatabase ourInstance;

	public static BooksDatabase Instance(Context context) {
		if (ourInstance == null) {
			ourInstance = new SQLiteBooksDatabase(context);
		}
		return ourInstance;
	}

	private final SQLiteDatabase myDatabase;

	private SQLiteBooksDatabase(Context context) {
		// myDatabase = context.("books.db", Context.MODE_PRIVATE, null);
		File file = new File(FileUtils.ROOT_PATH+ File.separator);
		if (!file.exists()) {
			file.mkdir();
		}
		// L.e("SQLiteBooksDatabase", "gbread" + file.exists());
		File dbFile = new File(FileUtils.ROOT_PATH+"/books.db");

		if (!dbFile.exists()) {
			File cacheFile = new File(FileUtils.ROOT_PATH+"/.cache");
			if (cacheFile.exists()) {
				for (File cacheItem : cacheFile.listFiles()) {
					cacheItem.delete();
				}

			}
		}

		myDatabase = SQLiteDatabase.openOrCreateDatabase(
				dbFile.getAbsolutePath(), null);

		migrate();

	}

	protected void executeAsTransaction(Runnable actions) {
		boolean transactionStarted = false;
		try {
			myDatabase.beginTransaction();
			transactionStarted = true;
		} catch (Throwable t) {
		}
		try {
			actions.run();
			if (transactionStarted) {
				myDatabase.setTransactionSuccessful();
			}
		} finally {
			if (transactionStarted) {
				myDatabase.endTransaction();
			}
		}
	}

	private void migrate() {
		final int version = myDatabase.getVersion();
		final int currentVersion = 21;
		if (version >= currentVersion) {
			return;
		}

		myDatabase.beginTransaction();

		switch (myDatabase.getVersion()) {
			case 0:
				createTablesGeeboo();
			/*
			 * case 1: updateTables1(); case 2: updateTables2(); case 3:
			 * updateTables3(); case 4: updateTables4(); case 5:
			 * updateTables5(); case 6: updateTables6(); case 7:
			 * updateTables7(); case 8: updateTables8(); case 9:
			 * updateTables9(); case 10: updateTables10(); case 11:
			 * updateTables11(); case 12: updateTables12(); case 13:
			 * updateTables13(); case 14: updateTables14(); case 15:
			 * updateTables15(); case 16: updateTables16(); case 17:
			 * updateTables17(); case 18: updateTables18(); case 19:
			 * updateTables19(); case 20: updateTables20();
			 */
		}
		myDatabase.setTransactionSuccessful();
		myDatabase.setVersion(currentVersion);
		myDatabase.endTransaction();

		myDatabase.execSQL("VACUUM");
	}

	@Override
	protected Book loadBook(long bookId) {
		Book book = null;
		final Cursor cursor = myDatabase.rawQuery(
				"SELECT file_id,title,encoding,language FROM Books WHERE book_id = "
						+ bookId, null);
		if (cursor.moveToNext()) {
			book = createBook(bookId, cursor.getLong(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3));
		}
		cursor.close();
		return book;
	}

	protected Book loadBookByFile(long fileId, GBFile file) {
		if (fileId == -1) {
			return null;
		}
		Book book = null;
		final Cursor cursor = myDatabase.rawQuery(
				"SELECT book_id,title,encoding,language FROM Books WHERE file_id = "
						+ fileId, null);
		if (cursor.moveToNext()) {
			book = createBook(cursor.getLong(0), file, cursor.getString(1),
					cursor.getString(2), cursor.getString(3));
		}
		cursor.close();
		return book;
	}

	private boolean myTagCacheIsInitialized;
	private final HashMap<Tag, Long> myIdByTag = new HashMap<Tag, Long>();
	private final HashMap<Long, Tag> myTagById = new HashMap<Long, Tag>();

	private void initTagCache() {
		if (myTagCacheIsInitialized) {
			return;
		}
		myTagCacheIsInitialized = true;

		Cursor cursor = myDatabase.rawQuery(
				"SELECT tag_id,parent_id,name FROM Tags ORDER BY tag_id", null);
		while (cursor.moveToNext()) {
			long id = cursor.getLong(0);
			if (myTagById.get(id) == null) {
				final Tag tag = Tag.getTag(myTagById.get(cursor.getLong(1)),
						cursor.getString(2));
				myIdByTag.put(tag, id);
				myTagById.put(id, tag);
			}
		}
		cursor.close();
	}

	@Override
	protected Map<Long, Book> loadBooks(FileInfoSet infos, boolean existing) {
		Cursor cursor = myDatabase.rawQuery(
				"SELECT book_id,file_id,title,encoding,language FROM Books WHERE `exists` = "
						+ (existing ? 1 : 0), null);
		final HashMap<Long, Book> booksById = new HashMap<Long, Book>();
		final HashMap<Long, Book> booksByFileId = new HashMap<Long, Book>();
		while (cursor.moveToNext()) {
			final long id = cursor.getLong(0);
			final long fileId = cursor.getLong(1);
			final Book book = createBook(id, infos.getFile(fileId),
					cursor.getString(2), cursor.getString(3),
					cursor.getString(4));
			if (book != null) {
				booksById.put(id, book);
				booksByFileId.put(fileId, book);
			}
		}
		cursor.close();

		initTagCache();

		cursor = myDatabase.rawQuery(
				"SELECT author_id,name,sort_key FROM Authors", null);
		final HashMap<Long, Author> authorById = new HashMap<Long, Author>();
		while (cursor.moveToNext()) {
			authorById.put(cursor.getLong(0), new Author(cursor.getString(1),
					cursor.getString(2)));
		}
		cursor.close();

		cursor = myDatabase
				.rawQuery(
						"SELECT book_id,author_id FROM BookAuthor ORDER BY author_index",
						null);
		while (cursor.moveToNext()) {
			Book book = booksById.get(cursor.getLong(0));
			if (book != null) {
				Author author = authorById.get(cursor.getLong(1));
				if (author != null) {
					addAuthor(book, author);
				}
			}
		}
		cursor.close();

		cursor = myDatabase
				.rawQuery("SELECT book_id,tag_id FROM BookTag", null);
		while (cursor.moveToNext()) {
			Book book = booksById.get(cursor.getLong(0));
			if (book != null) {
				addTag(book, getTagById(cursor.getLong(1)));
			}
		}
		cursor.close();

		cursor = myDatabase.rawQuery("SELECT series_id,name FROM Series", null);
		final HashMap<Long, String> seriesById = new HashMap<Long, String>();
		while (cursor.moveToNext()) {
			seriesById.put(cursor.getLong(0), cursor.getString(1));
		}
		cursor.close();

		cursor = myDatabase.rawQuery(
				"SELECT book_id,series_id,book_index FROM BookSeries", null);
		while (cursor.moveToNext()) {
			Book book = booksById.get(cursor.getLong(0));
			if (book != null) {
				final String series = seriesById.get(cursor.getLong(1));
				if (series != null) {
					setSeriesInfo(book, series, cursor.getString(2));
				}
			}
		}
		cursor.close();
		return booksByFileId;
	}

	@Override
	protected void setExistingFlag(Collection<Book> books, boolean flag) {
		if (books.isEmpty()) {
			return;
		}
		final StringBuilder bookSet = new StringBuilder("(");
		boolean first = true;
		for (Book b : books) {
			if (first) {
				first = false;
			} else {
				bookSet.append(",");
			}
			bookSet.append(b.getId());
		}
		bookSet.append(")");
		myDatabase.execSQL("UPDATE Books SET `exists` = " + (flag ? 1 : 0)
				+ " WHERE book_id IN " + bookSet);
	}

	private SQLiteStatement myUpdateBookInfoStatement;

	@Override
	protected void updateBookInfo(long bookId, long fileId, String encoding,
                                  String language, String title) {
		if (myUpdateBookInfoStatement == null) {
			myUpdateBookInfoStatement = myDatabase
					.compileStatement("UPDATE OR IGNORE Books SET file_id = ?, encoding = ?, language = ?, title = ? WHERE book_id = ?");
		}
		myUpdateBookInfoStatement.bindLong(1, fileId);
		SQLiteUtil.bindString(myUpdateBookInfoStatement, 2, encoding);
		SQLiteUtil.bindString(myUpdateBookInfoStatement, 3, language);
		myUpdateBookInfoStatement.bindString(4, title);
		myUpdateBookInfoStatement.bindLong(5, bookId);
		myUpdateBookInfoStatement.execute();
	}

	private SQLiteStatement myInsertBookInfoStatement;

	@Override
	protected long insertBookInfo(GBFile file, String encoding,
                                  String language, String title) {
		if (myInsertBookInfoStatement == null) {
			myInsertBookInfoStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO Books (encoding,language,title,file_id) VALUES (?,?,?,?)");
		}
		SQLiteUtil.bindString(myInsertBookInfoStatement, 1, encoding);
		SQLiteUtil.bindString(myInsertBookInfoStatement, 2, language);
		myInsertBookInfoStatement.bindString(3, title);
		final FileInfoSet infoSet = new FileInfoSet(this, file);
		myInsertBookInfoStatement.bindLong(4, infoSet.getId(file));
		return myInsertBookInfoStatement.executeInsert();
	}

	private SQLiteStatement myDeleteBookAuthorsStatement;

	protected void deleteAllBookAuthors(long bookId) {
		if (myDeleteBookAuthorsStatement == null) {
			myDeleteBookAuthorsStatement = myDatabase
					.compileStatement("DELETE FROM BookAuthor WHERE book_id = ?");
		}
		myDeleteBookAuthorsStatement.bindLong(1, bookId);
		myDeleteBookAuthorsStatement.execute();
	}

	private SQLiteStatement myGetAuthorIdStatement;
	private SQLiteStatement myInsertAuthorStatement;
	private SQLiteStatement myInsertBookAuthorStatement;

	protected void saveBookAuthorInfo(long bookId, long index, Author author) {
		if (myGetAuthorIdStatement == null) {
			myGetAuthorIdStatement = myDatabase
					.compileStatement("SELECT author_id FROM Authors WHERE name = ? AND sort_key = ?");
			myInsertAuthorStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO Authors (name,sort_key) VALUES (?,?)");
			myInsertBookAuthorStatement = myDatabase
					.compileStatement("INSERT OR REPLACE INTO BookAuthor (book_id,author_id,author_index) VALUES (?,?,?)");
		}

		long authorId;
		try {
			myGetAuthorIdStatement.bindString(1, author.DisplayName);
			myGetAuthorIdStatement.bindString(2, author.SortKey);
			authorId = myGetAuthorIdStatement.simpleQueryForLong();
		} catch (SQLException e) {
			myInsertAuthorStatement.bindString(1, author.DisplayName);
			myInsertAuthorStatement.bindString(2, author.SortKey);
			authorId = myInsertAuthorStatement.executeInsert();
		}
		myInsertBookAuthorStatement.bindLong(1, bookId);
		myInsertBookAuthorStatement.bindLong(2, authorId);
		myInsertBookAuthorStatement.bindLong(3, index);
		myInsertBookAuthorStatement.execute();
	}

	protected List<Author> listAuthors(long bookId) {
		final Cursor cursor = myDatabase
				.rawQuery(
						"SELECT Authors.name,Authors.sort_key FROM BookAuthor INNER JOIN Authors ON Authors.author_id = BookAuthor.author_id WHERE BookAuthor.book_id = ?",
						new String[] { String.valueOf(bookId) });
		if (!cursor.moveToNext()) {
			cursor.close();
			return null;
		}
		final ArrayList<Author> list = new ArrayList<Author>();
		do {
			list.add(new Author(cursor.getString(0), cursor.getString(1)));
		} while (cursor.moveToNext());
		cursor.close();
		return list;
	}

	private SQLiteStatement myGetTagIdStatement;
	private SQLiteStatement myCreateTagIdStatement;

	private long getTagId(Tag tag) {
		if (myGetTagIdStatement == null) {
			myGetTagIdStatement = myDatabase
					.compileStatement("SELECT tag_id FROM Tags WHERE parent_id = ? AND name = ?");
			myCreateTagIdStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO Tags (parent_id,name) VALUES (?,?)");
		}
		{
			final Long id = myIdByTag.get(tag);
			if (id != null) {
				return id;
			}
		}
		if (tag.Parent != null) {
			myGetTagIdStatement.bindLong(1, getTagId(tag.Parent));
		} else {
			myGetTagIdStatement.bindNull(1);
		}
		myGetTagIdStatement.bindString(2, tag.Name);
		long id;
		try {
			id = myGetTagIdStatement.simpleQueryForLong();
		} catch (SQLException e) {
			if (tag.Parent != null) {
				myCreateTagIdStatement.bindLong(1, getTagId(tag.Parent));
			} else {
				myCreateTagIdStatement.bindNull(1);
			}
			myCreateTagIdStatement.bindString(2, tag.Name);
			id = myCreateTagIdStatement.executeInsert();
		}
		myIdByTag.put(tag, id);
		myTagById.put(id, tag);
		return id;
	}

	private SQLiteStatement myDeleteBookTagsStatement;

	protected void deleteAllBookTags(long bookId) {
		if (myDeleteBookTagsStatement == null) {
			myDeleteBookTagsStatement = myDatabase
					.compileStatement("DELETE FROM BookTag WHERE book_id = ?");
		}
		myDeleteBookTagsStatement.bindLong(1, bookId);
		myDeleteBookTagsStatement.execute();
	}

	private SQLiteStatement myInsertBookTagStatement;

	protected void saveBookTagInfo(long bookId, Tag tag) {
		if (myInsertBookTagStatement == null) {
			myInsertBookTagStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO BookTag (book_id,tag_id) VALUES (?,?)");
		}
		myInsertBookTagStatement.bindLong(1, bookId);
		myInsertBookTagStatement.bindLong(2, getTagId(tag));
		myInsertBookTagStatement.execute();
	}

	private Tag getTagById(long id) {
		Tag tag = myTagById.get(id);
		if (tag == null) {
			final Cursor cursor = myDatabase.rawQuery(
					"SELECT parent_id,name FROM Tags WHERE tag_id = ?",
					new String[] { String.valueOf(id) });
			if (cursor.moveToNext()) {
				final Tag parent = cursor.isNull(0) ? null : getTagById(cursor
						.getLong(0));
				tag = Tag.getTag(parent, cursor.getString(1));
				myIdByTag.put(tag, id);
				myTagById.put(id, tag);
			}
			cursor.close();
		}
		return tag;
	}

	protected List<Tag> listTags(long bookId) {
		final Cursor cursor = myDatabase
				.rawQuery(
						"SELECT Tags.tag_id FROM BookTag INNER JOIN Tags ON Tags.tag_id = BookTag.tag_id WHERE BookTag.book_id = ?",
						new String[] { String.valueOf(bookId) });
		if (!cursor.moveToNext()) {
			cursor.close();
			return null;
		}
		final ArrayList<Tag> list = new ArrayList<Tag>();
		do {
			list.add(getTagById(cursor.getLong(0)));
		} while (cursor.moveToNext());
		cursor.close();
		return list;
	}

	private SQLiteStatement myInsertBookUidStatement;

	@Override
	protected void saveBookUid(long bookId, UID uid) {
		if (myInsertBookUidStatement == null) {
			myInsertBookUidStatement = myDatabase
					.compileStatement("INSERT OR REPLACE INTO BookUid (book_id,type,uid) VALUES (?,?,?)");
		}

		synchronized (myInsertBookUidStatement) {
			myInsertBookUidStatement.bindLong(1, bookId);
			myInsertBookUidStatement.bindString(2, uid.Type);
			myInsertBookUidStatement.bindString(2, uid.Id);
			myInsertBookAuthorStatement.execute();
		}
	}

	@Override
	protected List<UID> listUids(long bookId) {
		final ArrayList<UID> list = new ArrayList<UID>();
		final Cursor cursor = myDatabase.rawQuery(
				"SELECT type,uid FROM BookUid WHERE book_id = ?",
				new String[] { String.valueOf(bookId) });
		while (cursor.moveToNext()) {
			list.add(new UID(cursor.getString(0), cursor.getString(1)));
		}
		cursor.close();
		return list;
	}

	@Override
	protected Long bookIdByUid(UID uid) {
		Long bookId = null;
		final Cursor cursor = myDatabase.rawQuery(
				"SELECT book_id FROM BookUid WHERE type = ? AND uid = ?",
				new String[] { uid.Type, uid.Id });
		if (cursor.moveToNext()) {
			bookId = cursor.getLong(0);
		}
		cursor.close();
		return bookId;
	}

	private SQLiteStatement myGetSeriesIdStatement;
	private SQLiteStatement myInsertSeriesStatement;
	private SQLiteStatement myInsertBookSeriesStatement;
	private SQLiteStatement myDeleteBookSeriesStatement;

	protected void saveBookSeriesInfo(long bookId, SeriesInfo seriesInfo) {
		if (myGetSeriesIdStatement == null) {
			myGetSeriesIdStatement = myDatabase
					.compileStatement("SELECT series_id FROM Series WHERE name = ?");
			myInsertSeriesStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO Series (name) VALUES (?)");
			myInsertBookSeriesStatement = myDatabase
					.compileStatement("INSERT OR REPLACE INTO BookSeries (book_id,series_id,book_index) VALUES (?,?,?)");
			myDeleteBookSeriesStatement = myDatabase
					.compileStatement("DELETE FROM BookSeries WHERE book_id = ?");
		}

		if (seriesInfo == null) {
			myDeleteBookSeriesStatement.bindLong(1, bookId);
			myDeleteBookSeriesStatement.execute();
		} else {
			long seriesId;
			try {
				myGetSeriesIdStatement.bindString(1,
						seriesInfo.Series.getTitle());
				seriesId = myGetSeriesIdStatement.simpleQueryForLong();
			} catch (SQLException e) {
				myInsertSeriesStatement.bindString(1,
						seriesInfo.Series.getTitle());
				seriesId = myInsertSeriesStatement.executeInsert();
			}
			myInsertBookSeriesStatement.bindLong(1, bookId);
			myInsertBookSeriesStatement.bindLong(2, seriesId);
			SQLiteUtil.bindString(myInsertBookSeriesStatement, 3,
					seriesInfo.Index != null ? seriesInfo.Index.toString()
							: null);
			myInsertBookSeriesStatement.execute();
		}
	}

	protected SeriesInfo getSeriesInfo(long bookId) {
		final Cursor cursor = myDatabase
				.rawQuery(
						"SELECT Series.name,BookSeries.book_index FROM BookSeries INNER JOIN Series ON Series.series_id = BookSeries.series_id WHERE BookSeries.book_id = ?",
						new String[] { String.valueOf(bookId) });
		SeriesInfo info = null;
		if (cursor.moveToNext()) {
			info = SeriesInfo.createSeriesInfo(cursor.getString(0),
					cursor.getString(1));
		}
		cursor.close();
		return info;
	}

	private SQLiteStatement myRemoveFileInfoStatement;

	protected void removeFileInfo(long fileId) {
		if (fileId == -1) {
			return;
		}
		if (myRemoveFileInfoStatement == null) {
			myRemoveFileInfoStatement = myDatabase
					.compileStatement("DELETE FROM Files WHERE file_id = ?");
		}
		myRemoveFileInfoStatement.bindLong(1, fileId);
		myRemoveFileInfoStatement.execute();
	}

	private SQLiteStatement myInsertFileInfoStatement;
	private SQLiteStatement myUpdateFileInfoStatement;

	protected void saveFileInfo(FileInfo fileInfo) {
		final long id = fileInfo.Id;
		SQLiteStatement statement;
		if (id == -1) {
			if (myInsertFileInfoStatement == null) {
				myInsertFileInfoStatement = myDatabase
						.compileStatement("INSERT OR IGNORE INTO Files (name,parent_id,size) VALUES (?,?,?)");
			}
			statement = myInsertFileInfoStatement;
		} else {
			if (myUpdateFileInfoStatement == null) {
				myUpdateFileInfoStatement = myDatabase
						.compileStatement("UPDATE Files SET name = ?, parent_id = ?, size = ? WHERE file_id = ?");
			}
			statement = myUpdateFileInfoStatement;
		}
		statement.bindString(1, fileInfo.Name);
		final FileInfo parent = fileInfo.Parent;
		if (parent != null) {
			statement.bindLong(2, parent.Id);
		} else {
			statement.bindNull(2);
		}
		final long size = fileInfo.FileSize;
		if (size != -1) {
			statement.bindLong(3, size);
		} else {
			statement.bindNull(3);
		}
		if (id == -1) {
			fileInfo.Id = statement.executeInsert();
		} else {
			statement.bindLong(4, id);
			statement.execute();
		}
	}

	protected Collection<FileInfo> loadFileInfos() {
		Cursor cursor = myDatabase.rawQuery(
				"SELECT file_id,name,parent_id,size FROM Files", null);
		HashMap<Long, FileInfo> infosById = new HashMap<Long, FileInfo>();
		while (cursor.moveToNext()) {
			final long id = cursor.getLong(0);
			final FileInfo info = createFileInfo(id, cursor.getString(1),
					cursor.isNull(2) ? null : infosById.get(cursor.getLong(2)));
			if (!cursor.isNull(3)) {
				info.FileSize = cursor.getLong(3);
			}
			infosById.put(id, info);
		}
		cursor.close();
		return infosById.values();
	}

	protected Collection<FileInfo> loadFileInfos(GBFile file) {
		final LinkedList<GBFile> fileStack = new LinkedList<GBFile>();
		for (; file != null; file = file.getParent()) {
			fileStack.addFirst(file);
		}

		final ArrayList<FileInfo> infos = new ArrayList<FileInfo>(
				fileStack.size());
		final String[] parameters = { null };
		FileInfo current = null;
		for (GBFile f : fileStack) {
			parameters[0] = f.getFullName();
			final Cursor cursor = myDatabase
					.rawQuery(
							(current == null) ? "SELECT file_id,size FROM Files WHERE name = ?"
									: "SELECT file_id,size FROM Files WHERE parent_id = "
									+ current.Id + " AND name = ?",
							parameters);
			if (cursor.moveToNext()) {
				current = createFileInfo(cursor.getLong(0), parameters[0],
						current);
				if (!cursor.isNull(1)) {
					current.FileSize = cursor.getLong(1);
				}
				infos.add(current);
				cursor.close();
			} else {
				cursor.close();
				break;
			}
		}

		return infos;
	}

	protected Collection<FileInfo> loadFileInfos(long fileId) {
		final ArrayList<FileInfo> infos = new ArrayList<FileInfo>();
		while (fileId != -1) {
			final Cursor cursor = myDatabase.rawQuery(
					"SELECT name,size,parent_id FROM Files WHERE file_id = "
							+ fileId, null);
			if (cursor.moveToNext()) {
				FileInfo info = createFileInfo(fileId, cursor.getString(0),
						null);
				if (!cursor.isNull(1)) {
					info.FileSize = cursor.getLong(1);
				}
				infos.add(0, info);
				fileId = cursor.isNull(2) ? -1 : cursor.getLong(2);
			} else {
				fileId = -1;
			}
			cursor.close();
		}
		for (int i = 1; i < infos.size(); ++i) {
			final FileInfo oldInfo = infos.get(i);
			final FileInfo newInfo = createFileInfo(oldInfo.Id, oldInfo.Name,
					infos.get(i - 1));
			newInfo.FileSize = oldInfo.FileSize;
			infos.set(i, newInfo);
		}
		return infos;
	}

	private SQLiteStatement mySaveRecentBookStatement;

	protected void saveRecentBookIds(final List<Long> ids) {
		if (mySaveRecentBookStatement == null) {
			mySaveRecentBookStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO RecentBooks (book_id) VALUES (?)");
		}
		executeAsTransaction(new Runnable() {
			public void run() {
				myDatabase.delete("RecentBooks", null, null);
				for (long id : ids) {
					mySaveRecentBookStatement.bindLong(1, id);
					mySaveRecentBookStatement.execute();
				}
			}
		});
	}

	@Override
	protected List<Long> loadRecentBookIds() {
		final Cursor cursor = myDatabase.rawQuery(
				"SELECT book_id FROM RecentBooks ORDER BY book_index", null);
		final LinkedList<Long> ids = new LinkedList<Long>();
		while (cursor.moveToNext()) {
			ids.add(cursor.getLong(0));
		}
		cursor.close();
		return ids;
	}

	@Override
	protected List<String> labels() {
		final Cursor cursor = myDatabase
				.rawQuery(
						"SELECT Labels.name FROM Labels"
								+ " INNER JOIN BookLabel ON BookLabel.label_id=Labels.label_id"
								+ " GROUP BY Labels.name", null);
		final LinkedList<String> names = new LinkedList<String>();
		while (cursor.moveToNext()) {
			names.add(cursor.getString(0));
		}
		cursor.close();
		return names;
	}

	@Override
	protected List<String> labels(long bookId) {
		final Cursor cursor = myDatabase
				.rawQuery(
						"SELECT Labels.name FROM Labels"
								+ " INNER JOIN BookLabel ON BookLabel.label_id=Labels.label_id"
								+ " WHERE BookLabel.book_id=?",
						new String[] { String.valueOf(bookId) });
		final LinkedList<String> names = new LinkedList<String>();
		while (cursor.moveToNext()) {
			names.add(cursor.getString(0));
		}
		cursor.close();
		return names;
	}

	private SQLiteStatement mySetLabelStatement;

	@Override
	protected void setLabel(long bookId, String label) {
		myDatabase.execSQL("INSERT OR IGNORE INTO Labels (name) VALUES (?)",
				new Object[] { label });
		if (mySetLabelStatement == null) {
			mySetLabelStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO BookLabel(label_id,book_id)"
							+ " SELECT label_id,? FROM Labels WHERE name=?");
		}
		mySetLabelStatement.bindLong(1, bookId);
		mySetLabelStatement.bindString(2, label);
		mySetLabelStatement.execute();
	}

	private SQLiteStatement myRemoveLabelStatement;

	@Override
	protected void removeLabel(long bookId, String label) {
		if (myRemoveLabelStatement == null) {
			myRemoveLabelStatement = myDatabase
					.compileStatement("DELETE FROM BookLabel WHERE book_id=? AND label_id IN"
							+ " (SELECT label_id FROM Labels WHERE name=?)");
		}
		myRemoveLabelStatement.bindLong(1, bookId);
		myRemoveLabelStatement.bindString(2, label);
		myRemoveLabelStatement.execute();
	}

	@Override
	protected List<Long> loadBooksForLabelIds(String label) {
		final Cursor cursor = myDatabase
				.rawQuery(
						"SELECT BookLabel.book_id FROM BookLabel"
								+ " INNER JOIN Labels ON BookLabel.label_id=Labels.label_id"
								+ " WHERE Labels.name=?",
						new String[] { label });
		final LinkedList<Long> ids = new LinkedList<Long>();
		while (cursor.moveToNext()) {
			ids.add(cursor.getLong(0));
		}
		cursor.close();
		return ids;
	}

	@Override
	protected List<Bookmark> loadBookmarks(long bookId, boolean isVisible) {
		LinkedList<Bookmark> list = new LinkedList<Bookmark>();
		Cursor cursor = myDatabase
				.rawQuery(
						"SELECT bm.bookmark_id,bm.book_id,b.title,bm.bookmark_text,bm.creation_time,bm.modification_time,bm.access_time,bm.access_counter,bm.model_id,bm.chpfileindex,bm.paragraph,bm.word,bm.char "
								+ "FROM Bookmarks AS bm INNER JOIN Books AS b ON b.book_id = bm.book_id WHERE bm.book_id = ? AND bm.visible = "
								+ (isVisible ? "1" : "0"),
						new String[] { String.valueOf(bookId) });
		while (cursor.moveToNext()) {
			list.add(createBookmark(cursor.getLong(0), cursor.getLong(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getLong(4), cursor.getLong(5),
					cursor.getLong(6), (int) cursor.getLong(7),
					cursor.getString(8), (int) cursor.getLong(9),
					(int) cursor.getLong(10), (int) cursor.getLong(11),
					(int) cursor.getLong(12), false));
		}
		cursor.close();
		return list;
	}

	@Override
	protected List<Bookmark> loadVisibleBookmarks(long fromId, int limitCount) {
		LinkedList<Bookmark> list = new LinkedList<Bookmark>();
		Cursor cursor = myDatabase
				.rawQuery(
						"SELECT"
								+ " bm.bookmark_id,bm.book_id,b.title,bm.bookmark_text,"
								+ "bm.creation_time,bm.modification_time,bm.access_time,bm.access_counter,"
								+ "bm.model_id,bm.chpfileindex,bm.paragraph,bm.word,bm.char"
								+ " FROM Bookmarks AS bm INNER JOIN Books AS b ON b.book_id = bm.book_id"
								+ " WHERE bm.bookmark_id >= ? AND bm.visible = 1 ORDER BY bm.bookmark_id LIMIT ?",
						new String[] { String.valueOf(fromId),
								String.valueOf(limitCount) });
		while (cursor.moveToNext()) {
			list.add(createBookmark(cursor.getLong(0), cursor.getLong(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getLong(4), cursor.getLong(5),
					cursor.getLong(6), (int) cursor.getLong(7),
					cursor.getString(8), (int) cursor.getLong(9),
					(int) cursor.getLong(10), (int) cursor.getLong(11),
					(int) cursor.getLong(12), true));
		}
		cursor.close();
		return list;
	}

	@Override
	protected List<Bookmark> loadVisibleBookmarks(long bookId, long fromId,
                                                  int limitCount) {
		LinkedList<Bookmark> list = new LinkedList<Bookmark>();
		Cursor cursor = myDatabase
				.rawQuery(
						"SELECT"
								+ " bm.bookmark_id,bm.book_id,b.title,bm.bookmark_text,"
								+ "bm.creation_time,bm.modification_time,bm.access_time,bm.access_counter,"
								+ "bm.model_id,bm.chpfileindex,bm.paragraph,bm.word,bm.char"
								+ " FROM Bookmarks AS bm INNER JOIN Books AS b ON b.book_id = bm.book_id"
								+ " WHERE b.book_id = ? AND bm.bookmark_id >= ? AND bm.visible = 1"
								+ " ORDER BY bm.bookmark_id LIMIT ?",
						new String[] { String.valueOf(bookId),
								String.valueOf(fromId),
								String.valueOf(limitCount) });
		while (cursor.moveToNext()) {
			list.add(createBookmark(cursor.getLong(0), cursor.getLong(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getLong(4),
					cursor.getLong(5),
					cursor.getLong(6), (int) cursor.getLong(7),
					cursor.getString(8), (int) cursor.getLong(9),
					(int) cursor.getLong(10), (int) cursor.getLong(11),
					(int) cursor.getLong(12), true));
		}
		cursor.close();
		return list;
	}

	private SQLiteStatement myInsertBookmarkStatement;
	private SQLiteStatement myUpdateBookmarkStatement;

	@Override
	protected long saveBookmark(Bookmark bookmark) {
		SQLiteStatement statement;
		if (bookmark.getId() == -1) {
			if (myInsertBookmarkStatement == null) {
				myInsertBookmarkStatement = myDatabase
						.compileStatement("INSERT OR IGNORE INTO Bookmarks (book_id,bookmark_text,"
								+ "creation_time,modification_time,access_time,access_counter,model_id,chpfileindex,"
								+ "paragraph,word,char,chp_word_num,visible) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
			}
			statement = myInsertBookmarkStatement;
		} else {
			if (myUpdateBookmarkStatement == null) {
				myUpdateBookmarkStatement = myDatabase
						.compileStatement("UPDATE Bookmarks SET book_id = ?, bookmark_text = ?, creation_time =?, "
								+ "modification_time = ?,access_time = ?, access_counter = ?, model_id = ?, chpfileindex=?,paragraph = ?,"
								+ " word = ?, char = ?, chp_word_num = ? , visible = ?  WHERE bookmark_id = ?");
			}
			statement = myUpdateBookmarkStatement;
		}

		statement.bindLong(1, bookmark.getBookId());
		statement.bindString(2, bookmark.getText());
		SQLiteUtil.bindDate(statement, 3,
				bookmark.getDate(Bookmark.DateType.Creation));
		SQLiteUtil.bindDate(statement, 4,
				bookmark.getDate(Bookmark.DateType.Modification));
		SQLiteUtil.bindDate(statement, 5,
				bookmark.getDate(Bookmark.DateType.Access));
		statement.bindLong(6, bookmark.getAccessCount());
		SQLiteUtil.bindString(statement, 7, bookmark.ModelId);
		statement.bindLong(8, bookmark.ChpFileIndex);
		statement.bindLong(9, bookmark.ParagraphIndex);
		statement.bindLong(10, bookmark.ElementIndex);
		statement.bindLong(11, bookmark.CharIndex);
		statement.bindLong(12, bookmark.getChpInWordNum());
		statement.bindLong(13, bookmark.IsVisible ? 1 : 0);


		if (statement == myInsertBookmarkStatement) {
			return statement.executeInsert();
		} else {
			final long id = bookmark.getId();
			statement.bindLong(14, id);
			statement.execute();
			return id;
		}
	}

	private SQLiteStatement myDeleteBookmarkStatement;

	@Override
	protected void deleteBookmark(Bookmark bookmark) {
		if (myDeleteBookmarkStatement == null) {
			myDeleteBookmarkStatement = myDatabase
					.compileStatement("DELETE FROM Bookmarks WHERE bookmark_id = ?");
		}
		myDeleteBookmarkStatement.bindLong(1, bookmark.getId());
		myDeleteBookmarkStatement.execute();
	}

	protected GBTextPosition getStoredPosition(long bookId) {
		GBTextPosition position = null;
		Cursor cursor = myDatabase.rawQuery(
				"SELECT chpfileindex,paragraph,word,char FROM BookState WHERE book_id = "
						+ bookId, null);
		if (cursor.moveToNext()) {
			position = new GBTextFixedPosition((int) cursor.getLong(0),
					(int) cursor.getLong(1), (int) cursor.getLong(2),
					(int) cursor.getLong(3));
		}
		cursor.close();
		return position;
	}

	private SQLiteStatement myStorePositionStatement;

	protected void storePosition(long bookId, GBTextPosition position) {
		if (myStorePositionStatement == null) {
			myStorePositionStatement = myDatabase
					.compileStatement("INSERT OR REPLACE INTO BookState (book_id,chpfileindex,paragraph,word,char) VALUES (?,?,?,?,?)");
		}
		myStorePositionStatement.bindLong(1, bookId);
		myStorePositionStatement.bindLong(2, position.getChpFileIndex());
		myStorePositionStatement.bindLong(3, position.getParagraphIndex());
		myStorePositionStatement.bindLong(4, position.getElementIndex());
		myStorePositionStatement.bindLong(5, position.getCharIndex());

		// get  position.getChpInWordNum()
		myStorePositionStatement.execute();
	}

	private SQLiteStatement myDeleteVisitedHyperlinksStatement;

	private void deleteVisitedHyperlinks(long bookId) {
		if (myDeleteVisitedHyperlinksStatement == null) {
			myDeleteVisitedHyperlinksStatement = myDatabase
					.compileStatement("DELETE FROM VisitedHyperlinks WHERE book_id = ?");
		}

		myDeleteVisitedHyperlinksStatement.bindLong(1, bookId);
		myDeleteVisitedHyperlinksStatement.execute();
	}

	private SQLiteStatement myStoreVisitedHyperlinksStatement;

	protected void addVisitedHyperlink(long bookId, String hyperlinkId) {
		if (myStoreVisitedHyperlinksStatement == null) {
			myStoreVisitedHyperlinksStatement = myDatabase
					.compileStatement("INSERT OR IGNORE INTO VisitedHyperlinks(book_id,hyperlink_id) VALUES (?,?)");
		}

		myStoreVisitedHyperlinksStatement.bindLong(1, bookId);
		myStoreVisitedHyperlinksStatement.bindString(2, hyperlinkId);
		myStoreVisitedHyperlinksStatement.execute();
	}

	protected Collection<String> loadVisitedHyperlinks(long bookId) {
		final TreeSet<String> links = new TreeSet<String>();
		final Cursor cursor = myDatabase.rawQuery(
				"SELECT hyperlink_id FROM VisitedHyperlinks WHERE book_id = ?",
				new String[] { String.valueOf(bookId) });
		while (cursor.moveToNext()) {
			links.add(cursor.getString(0));
		}
		cursor.close();
		return links;
	}

	/*
	 * 创建数据库表 Author
	 */
	private void createTablesGeeboo() {

		String tblBooksSqlStr = "CREATE TABLE IF NOT EXISTS Books(book_id INTEGER PRIMARY KEY,encoding TEXT,language TEXT,title TEXT NOT NULL,file_id INTEGER UNIQUE NOT NULL REFERENCES Files(file_id), `exists` INTEGER DEFAULT 1);";
		myDatabase.execSQL(tblBooksSqlStr);
		String tblAuthors = "CREATE TABLE IF NOT EXISTS Authors(author_id INTEGER PRIMARY KEY,name TEXT NOT NULL,sort_key TEXT NOT NULL,CONSTRAINT Authors_Unique UNIQUE (name, sort_key));";
		myDatabase.execSQL(tblAuthors);
		String tblBookAuthor = "CREATE TABLE IF NOT EXISTS BookAuthor(author_id INTEGER NOT NULL REFERENCES Authors(author_id),book_id INTEGER NOT NULL REFERENCES Books(book_id),author_index INTEGER NOT NULL,CONSTRAINT BookAuthor_Unique0 UNIQUE (author_id, book_id),CONSTRAINT BookAuthor_Unique1 UNIQUE (book_id, author_index));";
		myDatabase.execSQL(tblBookAuthor);
		String tblSeries = "CREATE TABLE IF NOT EXISTS Series(series_id INTEGER PRIMARY KEY,name TEXT UNIQUE NOT NULL);";
		myDatabase.execSQL(tblSeries);
		String tblBookSeries = "CREATE TABLE IF NOT EXISTS BookSeries(series_id INTEGER NOT NULL REFERENCES Series(series_id),book_id INTEGER NOT NULL UNIQUE REFERENCES Books(book_id),book_index TEXT); ";
		myDatabase.execSQL(tblBookSeries);
		String tblTags = "CREATE TABLE IF NOT EXISTS Tags(tag_id INTEGER PRIMARY KEY,name TEXT NOT NULL,parent_id INTEGER REFERENCES Tags(tag_id),CONSTRAINT Tags_Unique UNIQUE (name, parent_id));";
		myDatabase.execSQL(tblTags);
		String tblBookTag = "CREATE TABLE IF NOT EXISTS BookTag(tag_id INTEGER NOT NULL REFERENCES Tags(tag_id),book_id INTEGER NOT NULL REFERENCES Books(book_id),CONSTRAINT BookTag_Unique UNIQUE (tag_id, book_id));";
		myDatabase.execSQL(tblBookTag);

		String tblAndroidMetaDataStr = "CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT);";
		myDatabase.execSQL(tblAndroidMetaDataStr);
		String tblFilesSqlStr = "CREATE TABLE IF NOT EXISTS Files(file_id INTEGER PRIMARY KEY,name TEXT NOT NULL,parent_id INTEGER REFERENCES Files(file_id),size INTEGER,CONSTRAINT Files_Unique UNIQUE (name, parent_id));";
		myDatabase.execSQL(tblFilesSqlStr);
		String tblRecentBooksSqlStr = "CREATE TABLE IF NOT EXISTS RecentBooks(book_index INTEGER PRIMARY KEY,book_id INTEGER REFERENCES Books(book_id));";
		myDatabase.execSQL(tblRecentBooksSqlStr);
		String tblBookMarkSqlStr = "CREATE TABLE IF NOT EXISTS Bookmarks(bookmark_id INTEGER PRIMARY KEY,book_id INTEGER NOT NULL REFERENCES Books(book_id),bookmark_text TEXT NOT NULL,creation_time INTEGER NOT NULL,modification_time INTEGER,access_time INTEGER,access_counter INTEGER NOT NULL,chpfileindex INTEGER NOT NULL,paragraph INTEGER NOT NULL,word INTEGER NOT NULL,char INTEGER NOT NULL, model_id TEXT ,chp_word_num INTEGER , visible INTEGER DEFAULT 1);";
		myDatabase.execSQL(tblBookMarkSqlStr);
		String tbleBookStateSqlStr = "CREATE TABLE IF NOT EXISTS BookState(book_id INTEGER UNIQUE NOT NULL REFERENCES Books(book_id),chpfileindex INTEGER NOT NULL,paragraph INTEGER NOT NULL,word INTEGER NOT NULL,char INTEGER NOT NULL);";
		myDatabase.execSQL(tbleBookStateSqlStr);
		String tbleVisitedHyperlinksStr = "CREATE TABLE IF NOT EXISTS VisitedHyperlinks(book_id INTEGER NOT NULL REFERENCES Books(book_id),hyperlink_id TEXT NOT NULL,CONSTRAINT VisitedHyperlinks_Unique UNIQUE (book_id, hyperlink_id));";
		myDatabase.execSQL(tbleVisitedHyperlinksStr);
		String tblStatusSqlStr = "CREATE TABLE IF NOT EXISTS BookStatus(book_id INTEGER NOT NULL REFERENCES Books(book_id) PRIMARY KEY,access_time INTEGER NOT NULL,pages_full INTEGER NOT NULL,page_current INTEGER NOT NULL);";
		myDatabase.execSQL(tblStatusSqlStr);
		String tblBookUidSqlStr = "CREATE TABLE IF NOT EXISTS BookUid(book_id INTEGER NOT NULL UNIQUE REFERENCES Books(book_id),type TEXT NOT NULL,uid TEXT NOT NULL);";
		myDatabase.execSQL(tblBookUidSqlStr);
		String tblLableSqlStr = "CREATE TABLE IF NOT EXISTS Labels(label_id INTEGER PRIMARY KEY,name TEXT NOT NULL UNIQUE);";
		myDatabase.execSQL(tblLableSqlStr);
		String tblBookLableSqlStr = " CREATE TABLE IF NOT EXISTS BookLabel(label_id INTEGER NOT NULL REFERENCES Labels(label_id),book_id INTEGER NOT NULL REFERENCES Books(book_id),CONSTRAINT BookLabel_Unique UNIQUE (label_id,book_id));";
		myDatabase.execSQL(tblBookLableSqlStr);
		String tblAnnotationsSqlStr = "CREATE TABLE annotations(_id INTEGER PRIMARY KEY,  book_id INTEGER,  added_date INTEGER,  annotation_type INTEGER,  annotation_range TEXT,  annotation_content TEXT,  annotation_text TEXT,  annotation_uuid TEXT,  modified_date INTEGER)";
		myDatabase.execSQL(tblAnnotationsSqlStr);
	}

	private SQLiteStatement myAddAnnotationStatement;
	private SQLiteStatement myUpdateAnnotationStatement;
	private SQLiteStatement myDeleteAnnotationStatement;

	@Override
	protected long addAnnatitions(Annotations annotations) {
		SQLiteStatement statement;
		if (annotations._id == -1) {
			if (myAddAnnotationStatement == null) {
				myAddAnnotationStatement = myDatabase
						.compileStatement("INSERT OR IGNORE INTO annotations (book_id,added_date,annotation_type,annotation_range,"
								+ "annotation_content,annotation_text,annotation_uuid,modified_date) VALUES (?,?,?,?,?,?,?,?)");
			}
			statement = myAddAnnotationStatement;
		} else {
			if (myUpdateAnnotationStatement == null) {
				myUpdateAnnotationStatement = myDatabase
						.compileStatement("UPDATE annotations SET book_id = ?, added_date = ?, annotation_type =?, annotation_range = ?,"
								+ "annotation_content = ?, annotation_text = ?, annotation_uuid = ?, modified_date=? WHERE _id = ?");
			}
			statement = myUpdateAnnotationStatement;
		}

		statement.bindLong(1, annotations.bookId);
		SQLiteUtil.bindDate(statement, 2, annotations.addedDate);
		statement.bindString(3, annotations.annotationType);
		statement.bindString(4, annotations.annotationRange);
		statement.bindString(5, annotations.annotationContent);
		statement.bindString(6, annotations.annotationText);
		statement.bindString(7, annotations.annotationUUid);

		SQLiteUtil.bindDate(statement, 8, annotations.modifiedDate);

		if (statement == myAddAnnotationStatement) {
			return statement.executeInsert();
		} else {
			final long id = annotations._id;
			statement.bindLong(9, id);
			statement.execute();
			return id;
		}
	}

	@Override
	protected List<Annotations> loadAnotations(int bookId) {
		LinkedList<Annotations> list = new LinkedList<Annotations>();
		Cursor cursor = myDatabase
				.rawQuery(
						"SELECT _id,book_id,added_date,annotation_type,annotation_range,"
								+ "annotation_content,annotation_text,annotation_uuid,modified_date from annotations "
								+ "WHERE book_id = ?",
						new String[] { String.valueOf(bookId) });
		while (cursor.moveToNext()) {
			list.add(new Annotations(cursor.getInt(0), cursor.getInt(1),0,"",
					cursor.getLong(2), cursor.getString(3), cursor
					.getString(4), cursor.getString(5), cursor
					.getString(6), cursor.getString(7), cursor.getLong(8)));
		}
		cursor.close();
		return list;
	}

	@Override
	protected void deleteAnnotation(int _id) {
		if (myDeleteAnnotationStatement == null) {
			myDeleteAnnotationStatement = myDatabase
					.compileStatement("DELETE FROM annotations WHERE _id = ?");
		}
		myDeleteAnnotationStatement.bindLong(1, _id);
		myDeleteAnnotationStatement.execute();
	}

	@Override
	protected Annotations getAnnotationById(int _id) {
		Annotations ann = null;
		Cursor cursor = myDatabase
				.rawQuery(
						"SELECT _id,book_id,added_date,annotation_type,annotation_range,"
								+ "annotation_content,annotation_text,annotation_uuid,modified_date from annotations "
								+ "WHERE _id = ?",
						new String[] { String.valueOf(_id) });
		while (cursor.moveToNext()) {
			ann = new Annotations(cursor.getInt(0), cursor.getInt(1),0,"",
					cursor.getLong(2), cursor.getString(3),
					cursor.getString(4), cursor.getString(5),
					cursor.getString(6), cursor.getString(7),
					cursor.getLong(8));
		}
		cursor.close();
		return ann;
	}

}
