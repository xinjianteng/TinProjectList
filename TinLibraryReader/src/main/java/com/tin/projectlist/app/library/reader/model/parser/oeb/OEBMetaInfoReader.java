package com.tin.projectlist.app.library.reader.model.parser.oeb;

import com.core.common.XMLNamespaces;
import com.core.file.GBFile;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLProcessor;
import com.core.xml.GBXMLReaderAdapter;
import com.geeboo.read.model.book.Book;
import com.geeboo.read.model.bookmodel.BookReadingException;

import java.util.ArrayList;

/**
 * 类名： OEBMetaInfoReader.java<br>
 * 描述： 获取epub图书opf中描述的图书信息（metadata）<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class OEBMetaInfoReader extends GBXMLReaderAdapter implements XMLNamespaces {
    private final Book myBook;

    private String mySeriesTitle = "";
    private String mySeriesIndex = null;

    private final ArrayList<String> myAuthorList = new ArrayList<String>();
    private final ArrayList<String> myAuthorList2 = new ArrayList<String>();

    OEBMetaInfoReader(Book book) {
        myBook = book;
        myBook.setTitle(null);
        myBook.setLanguage(null);
    }

    void readMetaInfo(GBFile file) throws BookReadingException {
        myReadState = ReadState.Nothing;
        mySeriesTitle = "";
        mySeriesIndex = null;

        try {
            GBXMLProcessor.read(this, file, 512);
        } catch (Exception e) {
            throw new BookReadingException(e, file);
        }

        final ArrayList<String> authors = myAuthorList.isEmpty() ? myAuthorList2 : myAuthorList;
        for (String a : authors) {
            final int index = a.indexOf(',');
            if (index >= 0) {
                a = a.substring(index + 1).trim() + ' ' + a.substring(0, index).trim();
            } else {
                a = a.trim();
            }
            myBook.addAuthor(a);
        }

        if (!"".equals(mySeriesTitle)) {
            myBook.setSeriesInfo(mySeriesTitle, mySeriesIndex);
        }
    }

    enum ReadState {
        Nothing, Metadata, Author, Author2, Title, Subject, Language
    };
    private ReadState myReadState;

    private final StringBuilder myBuffer = new StringBuilder();

    @Override
    public boolean processNamespaces() {
        return true;
    }

    private boolean testDCTag(String name, String tag) {
        return testTag(DublinCore, name, tag) || testTag(DublinCoreLegacy, name, tag);
    }

    @Override
    public boolean startElementHandler(String tag, GBStringMap attributes) {
        tag = tag.toLowerCase();
        switch (myReadState) {
            default :
                break;
            case Nothing :
                if (testTag(OpenPackagingFormat, "metadata", tag) || "dc-metadata".equals(tag)) {
                    myReadState = ReadState.Metadata;
                }
                break;
            case Metadata :
                if (testDCTag("title", tag)) {
                    myReadState = ReadState.Title;
                } else if (testDCTag("author", tag)) {
                    final String role = attributes.getValue("role");
                    if (role == null) {
                        myReadState = ReadState.Author2;
                    } else if (role.equals("aut")) {
                        myReadState = ReadState.Author;
                    }
                } else if (testDCTag("subject", tag)) {
                    myReadState = ReadState.Subject;
                } else if (testDCTag("language", tag)) {
                    myReadState = ReadState.Language;
                } else if (testTag(OpenPackagingFormat, "meta", tag)) {
                    if ("calibre:series".equals(attributes.getValue("name"))) {
                        mySeriesTitle = attributes.getValue("content");
                    } else if ("calibre:series_index".equals(attributes.getValue("name"))) {
                        mySeriesIndex = attributes.getValue("content");
                    }
                }
                break;
        }
        return false;
    }
    @Override
    public void characterDataHandler(char[] data, int start, int len) {
        switch (myReadState) {
            case Nothing :
            case Metadata :
                break;
            case Author :
            case Author2 :
            case Title :
            case Subject :
            case Language :
                myBuffer.append(data, start, len);
                break;
        }
    }

    @Override
    public boolean endElementHandler(String tag) {
        tag = tag.toLowerCase();
        if (myReadState == ReadState.Metadata
                && (testTag(OpenPackagingFormat, "metadata", tag) || "dc-metadata".equals(tag))) {
            myReadState = ReadState.Nothing;
            return true;
        }

        String bufferContent = myBuffer.toString().trim();
        if (bufferContent.length() != 0) {
            switch (myReadState) {
                case Title :
                    myBook.setTitle(bufferContent);
                    break;
                case Author :
                    myAuthorList.add(bufferContent);
                    break;
                case Author2 :
                    myAuthorList2.add(bufferContent);
                    break;
                case Subject :
                    myBook.addTag(bufferContent);
                    break;
                case Language : {
                    int index = bufferContent.indexOf('_');
                    if (index >= 0) {
                        bufferContent = bufferContent.substring(0, index);
                    }
                    index = bufferContent.indexOf('-');
                    if (index >= 0) {
                        bufferContent = bufferContent.substring(0, index);
                    }
                    myBook.setLanguage("cz".equals(bufferContent) ? "cs" : bufferContent);
                    break;
                }
            }
        }
        myBuffer.delete(0, myBuffer.length());
        myReadState = ReadState.Metadata;
        return false;
    }
}
