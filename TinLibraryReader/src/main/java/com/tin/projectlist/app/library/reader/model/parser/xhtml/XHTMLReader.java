package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.core.common.CopyVersionInfo;
import com.core.common.XMLNamespaces;
import com.core.common.util.MiscUtil;
import com.core.file.GBFile;
import com.core.file.zip.GBArchiveEntryFile;
import com.core.xml.GBStringMap;
import com.core.xml.GBXMLReaderAdapter;
import com.geeboo.read.model.bookmodel.BookReader;
import com.geeboo.read.model.bookmodel.GBTextKind;
import com.geeboo.read.model.bookmodel.TagSpoor;
import com.geeboo.read.model.parser.css.XHTMLStyleReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author geeboo
 *
 */
public class XHTMLReader extends GBXMLReaderAdapter {
    private static final HashMap<String, XHTMLTagAction> ourTagActions = new HashMap<String, XHTMLTagAction>();

    private static XHTMLTagAction ourNullAction = new XHTMLTagAction() {
        protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        }

        protected void doAtEnd(XHTMLReader reader) {
        }
    };

    public static XHTMLTagAction addAction(String tag, XHTMLTagAction action) {
        XHTMLTagAction old = ourTagActions.get(tag);
        ourTagActions.put(tag, action);
        return old;
    }

    public static void fillTagTable() {
        if (!ourTagActions.isEmpty()) {
            return;
        }

        // addAction("html", new XHTMLTagAction());
        addAction("body", new XHTMLTagBodyAction());
        // addAction("title", new XHTMLTagAction());
        // addAction("meta", new XHTMLTagAction());
        // addAction("script", new XHTMLTagAction());

        // addAction("font", new XHTMLTagAction());
        // addAction("style", new XHTMLTagAction());

        addAction("p", new XHTMLTagParagraphAction());
        addAction("h1", new XHTMLTagParagraphWithControlAction(GBTextKind.H1));
        addAction("h2", new XHTMLTagParagraphWithControlAction(GBTextKind.H2));
        addAction("h3", new XHTMLTagParagraphWithControlAction(GBTextKind.H3));
        addAction("h4", new XHTMLTagParagraphWithControlAction(GBTextKind.H4));
        addAction("h5", new XHTMLTagParagraphWithControlAction(GBTextKind.H5));
        addAction("h6", new XHTMLTagParagraphWithControlAction(GBTextKind.H6));

        addAction("ol", new XHTMLTagControlAction(GBTextKind.OL));
        addAction("ul", new XHTMLTagControlAction(GBTextKind.UL));
        // addAction("dl", new XHTMLTagAction());
        addAction("li", new XHTMLTagParagraphWithControlAction(GBTextKind.LI));

        addAction("strong", new XHTMLTagControlAction(GBTextKind.STRONG));
        addAction("b", new XHTMLTagControlAction(GBTextKind.BOLD));
        addAction("em", new XHTMLTagControlAction(GBTextKind.EMPHASIS));
        addAction("i", new XHTMLTagControlAction(GBTextKind.ITALIC));
        final XHTMLTagAction codeControlAction = new XHTMLTagControlAction(GBTextKind.CODE);
        addAction("code", codeControlAction);
        addAction("tt", codeControlAction);
        addAction("kbd", codeControlAction);
        addAction("var", codeControlAction);
        addAction("samp", codeControlAction);
        addAction("cite", new XHTMLTagControlAction(GBTextKind.CITE));
        addAction("sub", new XHTMLTagControlAction(GBTextKind.SUB));
        addAction("sup", new XHTMLTagControlAction(GBTextKind.SUP));
        addAction("dd", new XHTMLTagControlAction(GBTextKind.DEFINITION_DESCRIPTION));
        addAction("dfn", new XHTMLTagControlAction(GBTextKind.DEFINITION));
        addAction("strike", new XHTMLTagControlAction(GBTextKind.STRIKETHROUGH));

        addAction("a", new XHTMLTagHyperlinkAction());

        addAction("img", new XHTMLTagImageAction(null, "src"));
        addAction("image", new XHTMLTagImageAction(XMLNamespaces.XLink, "href"));
        addAction("object", new XHTMLTagImageAction(null, "data"));

        // addAction("area", new XHTMLTagAction());
        // addAction("map", new XHTMLTagAction());

        // addAction("base", new XHTMLTagAction());
        // addAction("blockquote", new XHTMLTagAction());
        addAction("br", new XHTMLTagRestartParagraphAction());
        // addAction("center", new XHTMLTagAction());
        addAction("div", new XHTMLTagParagraphAction());
        addAction("dt", new XHTMLTagParagraphAction());
        // addAction("head", new XHTMLTagAction());
        addAction("hr", new XHTMLTagParagraphWithControlAction(GBTextKind.HR));
        // addAction("link", new XHTMLTagAction());
        // addAction("param", new XHTMLTagAction());
        // addAction("q", new XHTMLTagAction());
        // addAction("s", new XHTMLTagAction());

        addAction("pre", new XHTMLTagPreAction());
        // addAction("big", new XHTMLTagAction());
        // addAction("small", new XHTMLTagAction());
        addAction("u", new XHTMLTagControlAction(GBTextKind.U));

        addAction("table", new XHTMLTagTableAction());
        addAction("tr", new XHTMLTagParagraphWithControlAction(GBTextKind.TR));
        addAction("td", new XHTMLTagControlAction(GBTextKind.TD));// new
        // XHTMLTagTDAction());
        addAction("th", new XHTMLTagControlAction(GBTextKind.TH));

        // addAction("tr", new XHTMLTagAction());
        // addAction("caption", new XHTMLTagAction());
        addAction("span", new XHTMLTagControlAction(GBTextKind.SPAN));

        // my extends action
        addAction("link", new XHTMLTagLinkAction());
        addAction("style", new XHTMLTagStyleAction());
        addAction("font", new XHTMLTagFontAction());
        addAction("pizhu", new XHTMLTagNoteAction(GBTextKind.NOTE));
        addAction("audio", new XHTMLTagAudioAndVideoAction(GBTextKind.AUDIO));
        addAction("video", new XHTMLTagAudioAndVideoAction(GBTextKind.VIDEO));
        addAction("filectr", new XHTMLTagFileCtrAction());
        attrAction = new XHTMLTagAttrAction();
    }

    final BookReader myModelReader;
    String myPathPrefix;// 路径前缀
    // 如：/mnt/sdcard/Books/乔布斯的魔力演讲（珍藏版）.epub:OEBPS/Text/
    private String myLocalPathPrefix; // zip 文件内 路径前缀 如：OEBPS/Text/R
    String myReferencePrefix; // 引用索引序号前缀 如：4#
    String myFilePrefix;
    boolean myPreformatted;
    boolean myInsideBody;
    private final Map<String, String> myFileNumbers;
    // 本地文件对应索引
    private final Map<String, String> myLocalFileNumbers = new HashMap<String, String>();
    protected static boolean mIsCoverFile = false;

    public void setmIsCoverFile(boolean mIsCoverFile) {
        XHTMLReader.mIsCoverFile = mIsCoverFile;
    }

    public XHTMLReader(BookReader modelReader, Map<String, String> fileNumbers, String filePrefix) {
        myModelReader = modelReader;
        myModelReader.setMyTextParagraphExists(false);
        myFileNumbers = fileNumbers;
        styleReader = new XHTMLStyleReader();

        myFilePrefix = filePrefix;
    }

    public static final int BOOK_TEXT_MODE = 1, GENER_PAGE_NUM = 2;

    public XHTMLReader(BookReader modelReader, Map<String, String> fileNumbers, String filePrefix, byte modeCase) {
        myModelReader = modelReader;
        myModelReader.setMyTextParagraphExists(false);
        myFileNumbers = fileNumbers;
        styleReader = new XHTMLStyleReader();

        myFilePrefix = filePrefix;

        switch (modeCase) {
            case BOOK_TEXT_MODE :
                modelReader.setMainTextModel();
                break;
            case GENER_PAGE_NUM :
                modelReader.settingGenerPageMode();
                break;
        }
    }

    final BookReader getModelReader() {
        return myModelReader;
    }

    final String getLocalFileAlias(String fileName) {
        String alias = myLocalFileNumbers.get(fileName);
        if (alias == null) {
            alias = getFileAlias(myLocalPathPrefix + fileName);
            myLocalFileNumbers.put(fileName, alias);
        }
        return alias;
    }

    /**
     * 给xhtml添加别名 myFileNumbers存储xhtml entry路径与别名的映射关系
     */
    public final String getFileAlias(String fileName) {
        String num = myFileNumbers.get(fileName);
        if (num == null) {
            fileName = MiscUtil.decodeHtmlReference(fileName);
            fileName = GBArchiveEntryFile.normalizeEntryName(fileName);
            num = myFileNumbers.get(fileName);
        }
        if (num == null) {
            num = String.valueOf(myFileNumbers.size());
            myFileNumbers.put(fileName, num);
        }
        return num;
    }

    /**
     * @param file 被解析文件
     * @param referencePrefix 索引前缀 索引
     */
    public void readFile(GBFile file, String referencePrefix) throws IOException {
        fillTagTable();
        myModelReader.setMyTextParagraphExists(false);

        myReferencePrefix = referencePrefix;

        myPathPrefix = MiscUtil.htmlDirectoryPrefix(file);
        final String localPrefix = MiscUtil.archiveEntryName(myPathPrefix);
        if (!localPrefix.equals(myLocalPathPrefix)) {
            myLocalPathPrefix = localPrefix;
            myLocalFileNumbers.clear();
        }

        myPreformatted = false;
        myInsideBody = false;
        if (copyVersionInfo == null) {
            attrAction.isCopyHtml = false;
            read(file);
        } else {
            attrAction.isCopyHtml = true;
            read(file, copyVersionInfo);
        }

    }

    private final HashMap<String, XHTMLTagAction> myActions = new HashMap<String, XHTMLTagAction>();

    private XHTMLTagAction getTagAction(String tag) {
        XHTMLTagAction action = myActions.get(tag);
        if (action == null) {
            action = ourTagActions.get(tag.toLowerCase());
            if (action == null) {
                action = ourNullAction;
            }
            myActions.put(tag, action);
        }
        return action == ourNullAction ? null : action;
    }

    final String TAG = "XHTMLReader";

    private static XHTMLTagAttrAction attrAction = null;

    XHTMLStyleReader styleReader = null;

    @Override
    public boolean startElementHandler(String tag, GBStringMap attributes) {
        TagSpoor.pushTag(tag);
        // modify LinkAction
        /*
         * if ("link".equals(tag)) { // L.e(TAG,"current tag is link tag");
         * String href = "OEBPS" + attributes.getValue("href").substring(2);
         * styleReader.addStyleExternal(myReferencePrefix, parentPath + ":" +
         * href, attributes.getValue("rel"), attributes.getValue("type")); }
         */

        // modify StyleAction

        /*
         * if ("style".equals(tag)) { L.e(TAG, "current tag is link tag"); //
         * //styleReader.addStyleIxternal(referencePrefix, href, rel, type); }
         */
        // L.i(TAG, "start tag:" + tag);
        final XHTMLTagAction action = getTagAction(tag);
        attrAction.doAtStartProxy(this, styleReader, attributes, tag, myReferencePrefix,
                !(myInsideBody && (action == null || action instanceof XHTMLTagControlAction)));

        String id = attributes.getValue("id");
        if (id != null) {
            myModelReader.addHyperlinkLabel(myReferencePrefix + id);
        }

        // final XHTMLTagAction action = getTagAction(tag);
        if (action != null) {// attributesexec
            action.doAtStart(this, attributes);
        }

        return false;
    }

    @Override
    public boolean endElementHandler(String tag) {
        // L.i(TAG, "eng tag:" + tag);
        TagSpoor.popTag();

        final XHTMLTagAction action = getTagAction(tag);
        if (action != null) {
            action.doAtEnd(this);
        }
        attrAction.doAtEndProxy(this, tag);
        copyVersionInfo = null;
        return false;
    }

    @Override
    public void characterDataHandler(char[] data, int start, int len) {
        if (myPreformatted) {
            final char first = data[start];
            if ((first == '\r') || (first == '\n')) {
                myModelReader.addControl(GBTextKind.CODE, false);
                myModelReader.endParagraph();
                myModelReader.beginParagraph();
                myModelReader.addControl(GBTextKind.CODE, true);
            }
            int spaceCounter = 0;
            cycle : while (spaceCounter < len) {
                switch (data[start + spaceCounter]) {
                    case 0x08 :
                    case 0x09 :
                    case 0x0A :
                    case 0x0B :
                    case 0x0C :
                    case 0x0D :
                    case ' ' :
                        break;
                    default :
                        break cycle;
                }
                ++spaceCounter;
            }
            myModelReader.addFixedHSpace((short) spaceCounter);
            start += spaceCounter;
            len -= spaceCounter;
        }
        if (len > 0) {
//            L.i(TAG, "data:" + start + "," + len + "---" + new String(data, start, len));
//            L.i(TAG, myLocalPathPrefix+" add data "+myModelReader.myCurrentTextModel.getWrithChpFileIndex());
            if (myInsideBody && !myModelReader.paragraphIsOpen()) {
                final char first = data[start];
                if (((first == '\r') || (first == '\n')) && len <= 2) {
                    return;
                } else {
                    myModelReader.beginParagraph();
                }
            }
            myModelReader.addData(data, start, len, false);
        }
    }

    private static ArrayList<String> ourExternalDTDs = new ArrayList<String>();

    public static List<String> xhtmlDTDs() {
        if (ourExternalDTDs.isEmpty()) {
            ourExternalDTDs.add("dtd/xhtml/xhtml-lat1.dtd");
            ourExternalDTDs.add("dtd/xhtml/xhtml-special.dtd");
            ourExternalDTDs.add("dtd/xhtml/xhtml-symbol.dtd");
        }
        return ourExternalDTDs;
    }

    @Override
    public List<String> externalDTDs() {
        return xhtmlDTDs();
    }

    @Override
    public boolean dontCacheAttributeValues() {
        return true;
    }

    @Override
    public boolean processNamespaces() {
        return true;
    }

    // 增加版权业务处理×××××××××××××××
    // 是否版权章节
    private CopyVersionInfo copyVersionInfo = null;
    public void setCopyVersionInfo(CopyVersionInfo copyVersionInfo) {
        this.copyVersionInfo = copyVersionInfo;
    }
}
