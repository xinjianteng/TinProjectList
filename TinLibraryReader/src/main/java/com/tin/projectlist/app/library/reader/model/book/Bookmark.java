package com.tin.projectlist.app.library.reader.model.book;

import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextFixedPosition;

import java.util.Comparator;

/**
 * 类名： Bookmark.java<br>
 * 描述： 书签对象封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class Bookmark extends GBTextFixedPosition {
    public enum DateType {
        Creation, Modification, Access, Latest
    }
    //图书元数据
    private long myId;
    private final long myBookId;
    private final String myBookTitle;
    private String myText;
    private final long myCreationDate;
    //修改时间
    private long myModificationDate;
    //访问时间
    private long myAccessDate;
    //访问数量
    private int myAccessCount;
    //最后
    private long myLatestDate;
    //图书章节字符下标
    private long myChpInWordNum;
    //图书使用mode编号
    public final String ModelId;
    //是否可显示
    public final boolean IsVisible;

    /**
     *
     * @param id
     * @param bookId
     * @param bookTitle
     * @param text
     * @param creationDate
     * @param modificationDate
     * @param accessDate
     * @param accessCount
     * @param modelId
     * @param chpFileIndex 章节索引
     * @param paragraphIndex 段索引
     * @param elementIndex 段中元素索引
     * @param charIndex 元素中字符索引
     * @param isVisible 是否可显示 1可显示 0不可显示
     */
    public Bookmark(long id, long bookId, String bookTitle, String text, long creationDate, long modificationDate,
                    long accessDate, int accessCount, String modelId, int chpFileIndex, int paragraphIndex, int elementIndex,
                    int charIndex, boolean isVisible) {
        super(chpFileIndex, paragraphIndex, elementIndex, charIndex);

        myId = id;
        myBookId = bookId;
        myBookTitle = bookTitle;
        myText = text;
        myCreationDate = creationDate;
        myModificationDate = modificationDate;
        myLatestDate = (modificationDate != 0) ? modificationDate : creationDate;
        if (accessDate != 0) {
            myAccessDate = accessDate;
            if (myLatestDate < accessDate) {
                myLatestDate = accessDate;
            }
        }
        myAccessCount = accessCount;
        ModelId = modelId;
        IsVisible = isVisible;
    }

    public Bookmark(Book book, String modelId, GBTextWordCursor cursor, int maxLength, boolean isVisible) {
        this(book, modelId, cursor, createBookmarkText(cursor, maxLength), isVisible);
    }


    public Bookmark(Book book, String modelId, GBTextPosition position, String text, boolean isVisible) {
        super(position);

        myId = -1;
        myBookId = book.getId();
        myBookTitle = book.getTitle();
        myText = text;
        myCreationDate = 0l;
        ModelId = modelId;
        myChpInWordNum=position.getChpInWordNum();
        IsVisible = isVisible;

    }

    public long getId() {
        return myId;
    }

    public long getBookId() {
        return myBookId;
    }

    public String getText() {
        return myText;
    }

    public String getBookTitle() {
        return myBookTitle;
    }

    public long getDate(DateType type) {
        switch (type) {
            case Creation :
                return myCreationDate;
            case Modification :
                return myModificationDate;
            case Access :
                return myAccessDate;
            default :
            case Latest :
                return myLatestDate;
        }
    }

    public int getAccessCount() {
        return myAccessCount;
    }

    public void setText(String text) {
        if (!text.equals(myText)) {
            myText = text;
            myModificationDate = 0l;
            myLatestDate = myModificationDate;
        }
    }

    public void markAsAccessed() {
        myAccessDate = 0l;
        ++myAccessCount;
        myLatestDate = myAccessDate;
    }

    public long getChpInWordNum(){
        return myChpInWordNum;
    }

    public static class ByTimeComparator implements Comparator<Bookmark> {
        public int compare(Bookmark bm0, Bookmark bm1) {
            final long date0 = bm0.getDate(DateType.Latest);
            final long date1 = bm1.getDate(DateType.Latest);
            if (date0 == 0) {
                return date1 == 0 ? 0 : -1;
            }
            return date1 == 0 ? 1 : (int)(date1 - date0);
        }
    }

    private static String createBookmarkText(GBTextWordCursor cursor, int maxWords) {
        cursor = new GBTextWordCursor(cursor);

        final StringBuilder builder = new StringBuilder();
        final StringBuilder sentenceBuilder = new StringBuilder();
        final StringBuilder phraseBuilder = new StringBuilder();

        int wordCounter = 0;
        int sentenceCounter = 0;
        int storedWordCounter = 0;
        boolean lineIsNonEmpty = false;
        boolean appendLineBreak = false;
        mainLoop : while (wordCounter < maxWords && sentenceCounter < 3) {
            while (cursor.isEndOfParagraph()) {
                if (!cursor.nextParagraph(null,false)) {
                    break mainLoop;
                }
                if ((builder.length() > 0) && cursor.getParagraphCursor().isEndOfSection()) {
                    break mainLoop;
                }
                if (phraseBuilder.length() > 0) {
                    sentenceBuilder.append(phraseBuilder);
                    phraseBuilder.delete(0, phraseBuilder.length());
                }
                if (sentenceBuilder.length() > 0) {
                    if (appendLineBreak) {
                        builder.append("\n");
                    }
                    builder.append(sentenceBuilder);
                    sentenceBuilder.delete(0, sentenceBuilder.length());
                    ++sentenceCounter;
                    storedWordCounter = wordCounter;
                }
                lineIsNonEmpty = false;
                if (builder.length() > 0) {
                    appendLineBreak = true;
                }
            }
            final GBTextElement element = cursor.getElement();
            if (element instanceof GBTextWord) {
                final GBTextWord word = (GBTextWord) element;
                if (lineIsNonEmpty) {
                    phraseBuilder.append(" ");
                }
                phraseBuilder.append(word.Data, word.Offset, word.Length);
                ++wordCounter;
                lineIsNonEmpty = true;
                switch (word.Data[word.Offset + word.Length - 1]) {
                    case ',' :
                    case ':' :
                    case ';' :
                    case ')' :
                        sentenceBuilder.append(phraseBuilder);
                        phraseBuilder.delete(0, phraseBuilder.length());
                        break;
                    case '.' :
                    case '!' :
                    case '?' :
                        ++sentenceCounter;
                        if (appendLineBreak) {
                            builder.append("\n");
                            appendLineBreak = false;
                        }
                        sentenceBuilder.append(phraseBuilder);
                        phraseBuilder.delete(0, phraseBuilder.length());
                        builder.append(sentenceBuilder);
                        sentenceBuilder.delete(0, sentenceBuilder.length());
                        storedWordCounter = wordCounter;
                        break;
                }
            }
            cursor.nextWord();
        }
        if (storedWordCounter < 4) {
            if (sentenceBuilder.length() == 0) {
                sentenceBuilder.append(phraseBuilder);
            }
            if (appendLineBreak) {
                builder.append("\n");
            }
            builder.append(sentenceBuilder);
        }
        return builder.toString();
    }

    void setId(long id) {
        myId = id;
    }

    public void update(Bookmark other) {
        // TODO: copy other fields (?)
        if (other != null) {
            myId = other.myId;
        }
    }
}
