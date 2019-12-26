package com.tin.projectlist.app.library.reader.bookElements;

import java.io.Serializable;

/**
 * 版权信息实体
 *
 * @author jack
 *
 */
public class CopyVersionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static String BOOKNAME = "${bookName}";
	private final static String AUTHOR = "${author}";
	private final static String PUBLISHING = "${publishing}";
	private final static String ISBN = "${isbn}";
	private final static String ISSUENUM = "${issueNum}";
	private final static String PUBLISHNUM = "${publishNum}";
	private final static String WORDCOUNT = "${wordCount}";

	/**
	 * @param mBookName
	 *            图书名称
	 * @param mPublisher
	 *            出版社
	 * @param mAuthor
	 *            作者
	 * @param mISBN
	 *            ｉｓｂｎ
	 * @param mCopyInfo
	 *            发行总数
	 * @param mCopyCount
	 *            版次
	 * @param mWords
	 *            总字数
	 */
	public CopyVersionInfo(String mBookName, String mPublisher, String mAuthor,
                           String mISBN, String mCopyInfo, String mCopyCount, String mWords) {
		super();
		this.mBookName = mBookName;
		this.mPublisher = mPublisher;
		this.mAuthor = mAuthor;
		this.mISBN = mISBN;
		this.mCopyInfo = mCopyInfo;
		this.mCopyCount = mCopyCount;
		this.mWords = mWords;
	}

	public String mBookName;
	public String mPublisher;
	public String mAuthor;
	public String mISBN;
	public String mCopyInfo; // 发行拷贝信息
	public String mCopyCount; // 版次
	public String mWords; // 字数

	public String replaceCopyRight(String content) throws Exception {
		return content.replace(BOOKNAME, mBookName).replace(AUTHOR, mAuthor)
				.replace(PUBLISHING, mPublisher)
				.replace(PUBLISHING, mPublisher).replace(ISBN, mISBN)
				.replace(ISSUENUM, mCopyInfo).replace(PUBLISHNUM, mCopyCount)
				.replace(WORDCOUNT, mWords);

	}
}
