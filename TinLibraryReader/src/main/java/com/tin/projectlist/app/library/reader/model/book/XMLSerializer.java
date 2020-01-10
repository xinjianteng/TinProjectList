package com.tin.projectlist.app.library.reader.model.book;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Xml;

import com.core.common.XMLNamespaces;
import com.core.file.GBFile;

class XMLSerializer extends AbstractSerializer {
    @Override
    public String serialize(Book book) {
        final StringBuilder buffer = new StringBuilder();
        appendTag(buffer, "entry", false, "xmlns:dc", XMLNamespaces.DublinCore, "xmlns:calibre",
                XMLNamespaces.CalibreMetadata);

        appendTagWithContent(buffer, "id", book.getId());
        appendTagWithContent(buffer, "title", book.getTitle());
        appendTagWithContent(buffer, "dc:language", book.getLanguage());
        appendTagWithContent(buffer, "dc:encoding", book.getEncodingNoDetection());

        for (Author author : book.authors()) {
            appendTag(buffer, "author", false);
            appendTagWithContent(buffer, "uri", author.SortKey);
            appendTagWithContent(buffer, "name", author.DisplayName);
            closeTag(buffer, "author");
        }

        for (Tag tag : book.tags()) {
            appendTag(buffer, "category", true, "term", tag.toString("/"), "label", tag.Name);
        }

        final SeriesInfo seriesInfo = book.getSeriesInfo();
        if (seriesInfo != null) {
            appendTagWithContent(buffer, "calibre:series", seriesInfo.Series.getTitle());
            if (seriesInfo.Index != null) {
                appendTagWithContent(buffer, "calibre:series_index", seriesInfo.Index);
            }
        }
        // TODO: serialize description (?)
        // TODO: serialize cover (?)

        appendTag(buffer, "link", true, "href", book.File.getUrl(),
                // TODO: real book mimetype
                "type", "application/epub+zip", "rel", "http://opds-spec.org/acquisition");

        closeTag(buffer, "entry");
        return buffer.toString();
    }

    @Override
    public Book deserializeBook(String xml) {
        try {
            final BookDeserializer deserializer = new BookDeserializer();
            Xml.parse(xml, deserializer);
            return deserializer.getBook();
        } catch (SAXException e) {
            return null;
        }
    }

    @Override
    public String serialize(Bookmark bookmark) {
        final StringBuilder buffer = new StringBuilder();
        appendTag(buffer, "bookmark", false, "id", String.valueOf(bookmark.getId()), "visible",
                String.valueOf(bookmark.IsVisible));
        appendTag(buffer, "book", true, "id", String.valueOf(bookmark.getBookId()), "title", bookmark.getBookTitle());
        appendTagWithContent(buffer, "text", bookmark.getText());
        appendTag(buffer, "history", true, "date-creation", bookmark.getDate(Bookmark.DateType.Creation)+"",
                "date-modification", bookmark.getDate(Bookmark.DateType.Modification)+"", "date-access",
                bookmark.getDate(Bookmark.DateType.Access)+"", "access-count",
                String.valueOf(bookmark.getAccessCount()));
        appendTag(buffer, "position", true, "model", bookmark.ModelId, "paragraph",
                String.valueOf(bookmark.getParagraphIndex()), "element", String.valueOf(bookmark.getElementIndex()),
                "char", String.valueOf(bookmark.getCharIndex()));
        closeTag(buffer, "bookmark");
        return buffer.toString();
    }

    @Override
    public Bookmark deserializeBookmark(String xml) {
        try {
            final BookmarkDeserializer deserializer = new BookmarkDeserializer();
            Xml.parse(xml, deserializer);
            return deserializer.getBookmark();
        } catch (SAXException e) {
            return null;
        }
    }

//    private static DateFormat ourDateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL,
//            Locale.ENGLISH);
//    private static String formatDate(long date) {
//        return date != null ? ourDateFormatter.format(date) : null;
//    }
//    private static Date parseDate(String str) throws ParseException {
//        return str != null ? ourDateFormatter.parse(str) : null;
//    }

    private static void appendTag(StringBuilder buffer, String tag, boolean close, String... attrs) {
        buffer.append('<').append(tag);
        for (int i = 0; i < attrs.length - 1; i += 2) {
            if (attrs[i + 1] != null) {
                buffer.append(' ').append(escapeForXml(attrs[i])).append("=\"").append(escapeForXml(attrs[i + 1]))
                        .append('"');
            }
        }
        if (close) {
            buffer.append('/');
        }
        buffer.append(">\n");
    }

    private static void closeTag(StringBuilder buffer, String tag) {
        buffer.append("</").append(tag).append(">");
    }

    private static void appendTagWithContent(StringBuilder buffer, String tag, String content) {
        if (content != null) {
            buffer.append('<').append(tag).append('>').append(escapeForXml(content)).append("</").append(tag)
                    .append(">\n");
        }
    }

    private static void appendTagWithContent(StringBuilder buffer, String tag, Object content) {
        if (content != null) {
            appendTagWithContent(buffer, tag, String.valueOf(content));
        }
    }

    private static String escapeForXml(String data) {
        if (data.indexOf('&') != -1) {
            data = data.replaceAll("&", "&amp;");
        }
        if (data.indexOf('<') != -1) {
            data = data.replaceAll("<", "&lt;");
        }
        if (data.indexOf('>') != -1) {
            data = data.replaceAll(">", "&gt;");
        }
        if (data.indexOf('\'') != -1) {
            data = data.replaceAll("'", "&apos;");
        }
        if (data.indexOf('"') != -1) {
            data = data.replaceAll("\"", "&quot;");
        }
        return data;
    }

    private static void clear(StringBuilder buffer) {
        buffer.delete(0, buffer.length());
    }

    private static String string(StringBuilder buffer) {
        return buffer.length() != 0 ? buffer.toString() : null;
    }

    private static final class BookDeserializer extends DefaultHandler {
        private static enum State {
            READ_NOTHING, READ_ENTRY, READ_ID, READ_TITLE, READ_LANGUAGE, READ_ENCODING, READ_AUTHOR, READ_AUTHOR_URI, READ_AUTHOR_NAME, READ_SERIES_TITLE, READ_SERIES_INDEX,
        }

        private State myState = State.READ_NOTHING;

        private long myId = -1;
        private String myUrl;
        private final StringBuilder myTitle = new StringBuilder();
        private final StringBuilder myLanguage = new StringBuilder();
        private final StringBuilder myEncoding = new StringBuilder();
        private final ArrayList<Author> myAuthors = new ArrayList<Author>();
        private final ArrayList<Tag> myTags = new ArrayList<Tag>();
        private final StringBuilder myAuthorSortKey = new StringBuilder();
        private final StringBuilder myAuthorName = new StringBuilder();
        private final StringBuilder mySeriesTitle = new StringBuilder();
        private final StringBuilder mySeriesIndex = new StringBuilder();

        private Book myBook;

        public Book getBook() {
            return myState == State.READ_NOTHING ? myBook : null;
        }

        @Override
        public void startDocument() {
            myBook = null;

            myId = -1;
            myUrl = null;
            clear(myTitle);
            clear(myLanguage);
            clear(myEncoding);
            clear(mySeriesTitle);
            clear(mySeriesIndex);
            myAuthors.clear();
            myTags.clear();

            myState = State.READ_NOTHING;
        }

        @Override
        public void endDocument() {
            if (myId == -1) {
                return;
            }
            myBook = new Book(myId, GBFile.createFileByUrl(myUrl), string(myTitle), string(myEncoding),
                    string(myLanguage));
            for (Author author : myAuthors) {
                myBook.addAuthorWithNoCheck(author);
            }
            for (Tag tag : myTags) {
                myBook.addTagWithNoCheck(tag);
            }
            myBook.setSeriesInfoWithNoCheck(string(mySeriesTitle), string(mySeriesIndex));
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (myState) {
                case READ_NOTHING :
                    if (!"entry".equals(localName)) {
                        throw new SAXException("Unexpected tag " + localName);
                    }
                    myState = State.READ_ENTRY;
                    break;
                case READ_ENTRY :
                    if ("id".equals(localName)) {
                        myState = State.READ_ID;
                    } else if ("title".equals(localName)) {
                        myState = State.READ_TITLE;
                    } else if ("language".equals(localName) && XMLNamespaces.DublinCore.equals(uri)) {
                        myState = State.READ_LANGUAGE;
                    } else if ("encoding".equals(localName) && XMLNamespaces.DublinCore.equals(uri)) {
                        myState = State.READ_ENCODING;
                    } else if ("author".equals(localName)) {
                        myState = State.READ_AUTHOR;
                        clear(myAuthorName);
                        clear(myAuthorSortKey);
                    } else if ("category".equals(localName)) {
                        final String term = attributes.getValue("term");
                        if (term != null) {
                            myTags.add(Tag.getTag(term.split("/")));
                        }
                    } else if ("series".equals(localName) && XMLNamespaces.CalibreMetadata.equals(uri)) {
                        myState = State.READ_SERIES_TITLE;
                    } else if ("series_index".equals(localName) && XMLNamespaces.CalibreMetadata.equals(uri)) {
                        myState = State.READ_SERIES_INDEX;
                    } else if ("link".equals(localName)) {
                        // TODO: use "rel" attribute
                        myUrl = attributes.getValue("href");
                    } else {
                        throw new SAXException("Unexpected tag " + localName);
                    }
                    break;
                case READ_AUTHOR :
                    if ("uri".equals(localName)) {
                        myState = State.READ_AUTHOR_URI;
                    } else if ("name".equals(localName)) {
                        myState = State.READ_AUTHOR_NAME;
                    } else {
                        throw new SAXException("Unexpected tag " + localName);
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (myState) {
                case READ_NOTHING :
                    throw new SAXException("Unexpected closing tag " + localName);
                case READ_ENTRY :
                    if ("entry".equals(localName)) {
                        myState = State.READ_NOTHING;
                    }
                    break;
                case READ_AUTHOR_URI :
                case READ_AUTHOR_NAME :
                    myState = State.READ_AUTHOR;
                    break;
                case READ_AUTHOR :
                    if (myAuthorSortKey.length() > 0 && myAuthorName.length() > 0) {
                        myAuthors.add(new Author(myAuthorName.toString(), myAuthorSortKey.toString()));
                    }
                    myState = State.READ_ENTRY;
                    break;
                default :
                    myState = State.READ_ENTRY;
                    break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            switch (myState) {
                case READ_ID :
                    try {
                        myId = Long.parseLong(new String(ch, start, length));
                    } catch (NumberFormatException e) {
                    }
                    break;
                case READ_TITLE :
                    myTitle.append(ch, start, length);
                    break;
                case READ_LANGUAGE :
                    myLanguage.append(ch, start, length);
                    break;
                case READ_ENCODING :
                    myEncoding.append(ch, start, length);
                    break;
                case READ_AUTHOR_URI :
                    myAuthorSortKey.append(ch, start, length);
                    break;
                case READ_AUTHOR_NAME :
                    myAuthorName.append(ch, start, length);
                    break;
                case READ_SERIES_TITLE :
                    mySeriesTitle.append(ch, start, length);
                    break;
                case READ_SERIES_INDEX :
                    mySeriesIndex.append(ch, start, length);
                    break;
            }
        }
    }

    private static final class BookmarkDeserializer extends DefaultHandler {
        private static enum State {
            READ_NOTHING, READ_BOOKMARK, READ_TEXT
        }

        private State myState = State.READ_NOTHING;
        private Bookmark myBookmark;

        private long myId = -1;
        private long myBookId;
        private String myBookTitle;
        private final StringBuilder myText = new StringBuilder();
        private long myCreationDate;
        private long myModificationDate;
        private long myAccessDate;
        private int myAccessCount;
        private String myModelId;
        private int myChpFileIndex;
        private int myParagraphIndex;
        private int myElementIndex;
        private int myCharIndex;
        private boolean myIsVisible;

        public Bookmark getBookmark() {
            return myState == State.READ_NOTHING ? myBookmark : null;
        }

        @Override
        public void startDocument() {
            myBookmark = null;

            myId = -1;
            myBookId = -1;
            myBookTitle = null;
            clear(myText);
            myCreationDate = 0l;
            myModificationDate = 0l;
            myAccessDate = 0l;
            myAccessCount = 0;
            myModelId = null;
            myChpFileIndex = 0;
            myParagraphIndex = 0;
            myElementIndex = 0;
            myCharIndex = 0;
            myIsVisible = false;

            myState = State.READ_NOTHING;
        }

        @Override
        public void endDocument() {
            if (myBookId == -1) {
                return;
            }
            myBookmark = new Bookmark(myId, myBookId, myBookTitle, myText.toString(), myCreationDate,
                    myModificationDate, myAccessDate, myAccessCount, myModelId, myChpFileIndex, myParagraphIndex,
                    myElementIndex, myCharIndex, myIsVisible);
        }

        // appendTagWithContent(buffer, "text", bookmark.getText());
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (myState) {
                case READ_NOTHING :
                    if (!"bookmark".equals(localName)) {
                        throw new SAXException("Unexpected tag " + localName);
                    }
                    try {
                        myId = Long.parseLong(attributes.getValue("id"));
                        myIsVisible = Boolean.parseBoolean(attributes.getValue("visible"));
                        myState = State.READ_BOOKMARK;
                    } catch (Exception e) {
                        throw new SAXException("XML parsing error", e);
                    }
                    break;
                case READ_BOOKMARK :
                    if ("book".equals(localName)) {
                        try {
                            myBookId = Long.parseLong(attributes.getValue("id"));
                            myBookTitle = attributes.getValue("title");
                        } catch (Exception e) {
                            throw new SAXException("XML parsing error", e);
                        }
                    } else if ("text".equals(localName)) {
                        myState = State.READ_TEXT;
                    } else if ("history".equals(localName)) {
                        try {
                            myCreationDate = Long.valueOf(attributes.getValue("date-creation"));
                            myModificationDate = Long.valueOf(attributes.getValue("date-modification"));
                            myAccessDate = Long.valueOf(attributes.getValue("date-access"));
                            myAccessCount = Integer.parseInt(attributes.getValue("access-count"));
                        } catch (Exception e) {
                            throw new SAXException("XML parsing error", e);
                        }
                    } else if ("position".equals(localName)) {
                        try {
                            myModelId = attributes.getValue("model");
                            myParagraphIndex = Integer.parseInt(attributes.getValue("paragraph"));
                            myElementIndex = Integer.parseInt(attributes.getValue("element"));
                            myCharIndex = Integer.parseInt(attributes.getValue("char"));
                        } catch (Exception e) {
                            throw new SAXException("XML parsing error", e);
                        }
                    } else {
                        throw new SAXException("Unexpected tag " + localName);
                    }
                    break;
                case READ_TEXT :
                    throw new SAXException("Unexpected tag " + localName);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            switch (myState) {
                case READ_NOTHING :
                    throw new SAXException("Unexpected closing tag " + localName);
                case READ_BOOKMARK :
                    if ("bookmark".equals(localName)) {
                        myState = State.READ_NOTHING;
                    }
                    break;
                case READ_TEXT :
                    myState = State.READ_BOOKMARK;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            if (myState == State.READ_TEXT) {
                myText.append(ch, start, length);
            }
        }
    }
}
