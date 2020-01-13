package com.tin.projectlist.app.library.reader.model.book;



import com.tin.projectlist.app.library.reader.model.bookmodel.BookReadingException;
import com.tin.projectlist.app.library.reader.model.parser.FormatPlugin;
import com.tin.projectlist.app.library.reader.model.parser.PluginCollection;
import com.tin.projectlist.app.library.reader.parser.common.CopyVersionInfo;
import com.tin.projectlist.app.library.reader.parser.common.util.MiscUtil;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 类名： Book.java<br>
 * 描述： 图书实体封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class Book extends TitledEntity {
	public static final String FAVORITE_LABEL = "favorite";
	// 图书文件对象
	public final GBFile File;

	private volatile long myId;

	private volatile String myEncoding; // 使用编码
	private volatile String myLanguage; // 语言
	private volatile List<Author> myAuthors; // 作者列表
	private volatile List<Tag> myTags; //
	private volatile SeriesInfo mySeriesInfo;
	private volatile List<UID> myUids;

	private volatile boolean myIsSaved;

	private static final WeakReference<GBImage> NULL_IMAGE = new WeakReference<GBImage>(
			null);
	private WeakReference<GBImage> myCover;

	Book(long id, GBFile file, String title, String encoding, String language) {
		super(title);
		myId = id;
		File = file;
		myEncoding = encoding;
		myLanguage = language;
		myIsSaved = true;
	}

	public Book(GBFile file) throws BookReadingException {
		super(null);
		myId = -1;
		final FormatPlugin plugin = getPlugin(file);
		File = plugin.realBookFile(file);
		readMetaInfo(plugin);
		myIsSaved = false;
	}

	public void updateFrom(Book book) {
		if (myId != book.myId) {
			return;
		}
		setTitle(book.getTitle());
		myEncoding = book.myEncoding;
		myLanguage = book.myLanguage;
		myAuthors = book.myAuthors != null ? new ArrayList<Author>(
				book.myAuthors) : null;
		myTags = book.myTags != null ? new ArrayList<Tag>(book.myTags) : null;
		mySeriesInfo = book.mySeriesInfo;
	}

	public void reloadInfoFromFile() {
		try {
			readMetaInfo();
		} catch (BookReadingException e) {
			// ignore
		}
	}

	private static FormatPlugin getPlugin(GBFile file)
			throws BookReadingException {
		final FormatPlugin plugin = PluginCollection.Instance().getPlugin(file);
		if (plugin == null) {
			throw new BookReadingException("pluginNotFound", file);
		}
		return plugin;
	}

	public FormatPlugin getPlugin() throws BookReadingException {
		return getPlugin(File);
	}

	void readMetaInfo() throws BookReadingException {
		readMetaInfo(getPlugin());
	}

	private void readMetaInfo(FormatPlugin plugin) throws BookReadingException {
		myEncoding = null;
		myLanguage = null;
		setTitle(null);
		myAuthors = null;
		myTags = null;
		mySeriesInfo = null;

		myIsSaved = false;

		plugin.readMetaInfo(this);

		if (isTitleEmpty()) {
			final String fileName = File.getShortName();
			final int index = fileName.lastIndexOf('.');
			setTitle(index > 0 ? fileName.substring(0, index) : fileName);
		}
		final String demoPathPrefix = GBPaths.getBookPath() + "/Demos/";
		if (File.getPath().startsWith(demoPathPrefix)) {
			final String demoTag = GBResource.resource("library")
					.getResource("demo").getValue();
			setTitle(getTitle() + " (" + demoTag + ")");
			addTag(demoTag);
		}
	}

	void loadLists(BooksDatabase database) {
		myAuthors = database.listAuthors(myId);
		myTags = database.listTags(myId);
		mySeriesInfo = database.getSeriesInfo(myId);
		myUids = database.listUids(myId);
		myIsSaved = true;
	}

	public List<Author> authors() {
		return (myAuthors != null) ? Collections.unmodifiableList(myAuthors)
				: Collections.<Author> emptyList();
	}

	void addAuthorWithNoCheck(Author author) {
		if (myAuthors == null) {
			myAuthors = new ArrayList<Author>();
		}
		myAuthors.add(author);
	}

	public void removeAllAuthors() {
		if (myAuthors != null) {
			myAuthors = null;
			myIsSaved = false;
		}
	}

	public void addAuthor(Author author) {
		if (author == null) {
			return;
		}
		if (myAuthors == null) {
			myAuthors = new ArrayList<Author>();
			myAuthors.add(author);
			myIsSaved = false;
		} else if (!myAuthors.contains(author)) {
			myAuthors.add(author);
			myIsSaved = false;
		}
	}

	public void addAuthor(String name) {
		addAuthor(name, "");
	}

	public void addAuthor(String name, String sortKey) {
		String strippedName = name;
		strippedName.trim();
		if (strippedName.length() == 0) {
			return;
		}

		String strippedKey = sortKey;
		strippedKey.trim();
		if (strippedKey.length() == 0) {
			int index = strippedName.lastIndexOf(' ');
			if (index == -1) {
				strippedKey = strippedName;
			} else {
				strippedKey = strippedName.substring(index + 1);
				while ((index >= 0) && (strippedName.charAt(index) == ' ')) {
					--index;
				}
				strippedName = strippedName.substring(0, index + 1) + ' '
						+ strippedKey;
			}
		}

		addAuthor(new Author(strippedName, strippedKey));
	}

	public long getId() {
		return myId;
	}

	public void setTitle(String title) {
		if (!getTitle().equals(title)) {
			super.setTitle(title);
			myIsSaved = false;
		}
	}

	public SeriesInfo getSeriesInfo() {
		return mySeriesInfo;
	}

	void setSeriesInfoWithNoCheck(String name, String index) {
		mySeriesInfo = SeriesInfo.createSeriesInfo(name, index);
	}

	public void setSeriesInfo(String name, String index) {
		setSeriesInfo(name, SeriesInfo.createIndex(index));
	}

	public void setSeriesInfo(String name, BigDecimal index) {
		if (mySeriesInfo == null) {
			if (name != null) {
				mySeriesInfo = new SeriesInfo(name, index);
				myIsSaved = false;
			}
		} else if (name == null) {
			mySeriesInfo = null;
			myIsSaved = false;
		} else if (!name.equals(mySeriesInfo.Series.getTitle())
				|| mySeriesInfo.Index != index) {
			mySeriesInfo = new SeriesInfo(name, index);
			myIsSaved = false;
		}
	}

	public String getLanguage() {
		return myLanguage;
	}

	public void setLanguage(String language) {
		if (!MiscUtil.equals(myLanguage, language)) {
			myLanguage = language;
			resetSortKey();
			myIsSaved = false;
		}
	}

	public String getEncoding() {
		if (myEncoding == null) {
			try {
				getPlugin().detectLanguageAndEncoding(this);
			} catch (BookReadingException e) {
			}
			if (myEncoding == null) {
				setEncoding("utf-8");
			}
		}
		return myEncoding;
	}

	public String getEncodingNoDetection() {
		return myEncoding;
	}

	public void setEncoding(String encoding) {
		if (!MiscUtil.equals(myEncoding, encoding)) {
			myEncoding = encoding;
			myIsSaved = false;
		}
	}

	public List<Tag> tags() {
		return myTags != null ? Collections.unmodifiableList(myTags)
				: Collections.<Tag> emptyList();
	}

	void addTagWithNoCheck(Tag tag) {
		if (myTags == null) {
			myTags = new ArrayList<Tag>();
		}
		myTags.add(tag);
	}

	public void removeAllTags() {
		if (myTags != null) {
			myTags = null;
			myIsSaved = false;
		}
	}

	public void addTag(Tag tag) {
		if (tag != null) {
			if (myTags == null) {
				myTags = new ArrayList<Tag>();
			}
			if (!myTags.contains(tag)) {
				myTags.add(tag);
				myIsSaved = false;
			}
		}
	}

	public void addTag(String tagName) {
		addTag(Tag.getTag(null, tagName));
	}

	public boolean matchesUid(UID uid) {
		return myUids.contains(uid);
	}

	public boolean matches(String pattern) {
		if (MiscUtil.matchesIgnoreCase(getTitle(), pattern)) {
			return true;
		}
		if (mySeriesInfo != null
				&& MiscUtil.matchesIgnoreCase(mySeriesInfo.Series.getTitle(),
				pattern)) {
			return true;
		}
		if (myAuthors != null) {
			for (Author author : myAuthors) {
				if (MiscUtil.matchesIgnoreCase(author.DisplayName, pattern)) {
					return true;
				}
			}
		}
		if (myTags != null) {
			for (Tag tag : myTags) {
				if (MiscUtil.matchesIgnoreCase(tag.Name, pattern)) {
					return true;
				}
			}
		}
		if (MiscUtil.matchesIgnoreCase(File.getFullName(), pattern)) {
			return true;
		}
		return false;
	}

	boolean save(final BooksDatabase database, boolean force) {
		if (!force && myId != -1 && myIsSaved) {
			return false;
		}

		database.executeAsTransaction(new Runnable() {
			public void run() {
				if (myId >= 0) {
					final FileInfoSet fileInfos = new FileInfoSet(database,
							File);
					database.updateBookInfo(myId, fileInfos.getId(File),
							myEncoding, myLanguage, getTitle());
				} else {
					myId = database.insertBookInfo(File, myEncoding,
							myLanguage, getTitle());
					if (myId != -1 && myVisitedHyperlinks != null) {
						for (String linkId : myVisitedHyperlinks) {
							database.addVisitedHyperlink(myId, linkId);
						}
					}
				}

				long index = 0;
				database.deleteAllBookAuthors(myId);
				for (Author author : authors()) {
					database.saveBookAuthorInfo(myId, index++, author);
				}
				database.deleteAllBookTags(myId);
				for (Tag tag : tags()) {
					database.saveBookTagInfo(myId, tag);
				}
				database.saveBookSeriesInfo(myId, mySeriesInfo);
			}
		});

		myIsSaved = true;
		return true;
	}

	private Set<String> myVisitedHyperlinks;

	private void initHyperlinkSet(BooksDatabase database) {
		if (myVisitedHyperlinks == null) {
			myVisitedHyperlinks = new TreeSet<String>();
			if (myId != -1) {
				myVisitedHyperlinks
						.addAll(database.loadVisitedHyperlinks(myId));
			}
		}
	}

	boolean isHyperlinkVisited(BooksDatabase database, String linkId) {
		initHyperlinkSet(database);
		return myVisitedHyperlinks.contains(linkId);
	}

	void markHyperlinkAsVisited(BooksDatabase database, String linkId) {
		initHyperlinkSet(database);
		if (!myVisitedHyperlinks.contains(linkId)) {
			myVisitedHyperlinks.add(linkId);
			if (myId != -1) {
				database.addVisitedHyperlink(myId, linkId);
			}
		}
	}

	public synchronized GBImage getCover() {
		if (myCover == NULL_IMAGE) {
			return null;
		} else if (myCover != null) {
			final GBImage image = myCover.get();
			if (image != null) {
				return image;
			}
		}
		GBImage image = null;
		try {
			image = getPlugin().readCover(File);
		} catch (BookReadingException e) {
			// ignore
		}
		myCover = image != null ? new WeakReference<GBImage>(image)
				: NULL_IMAGE;
		return image;
	}

	@Override
	public int hashCode() {
		return (int) myId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Book)) {
			return false;
		}
		return File.equals(((Book) o).File);
	}

	// 添加版权支持
	private CopyVersionInfo mCopyVersionInfo = null;

	public CopyVersionInfo getmCopyVersionInfo() {
		return mCopyVersionInfo;
	}

	public void setmCopyVersionInfo(CopyVersionInfo mCopyVersionInfo) {
		this.mCopyVersionInfo = mCopyVersionInfo;
	}

}
