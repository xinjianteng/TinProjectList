package com.tin.projectlist.app.library.reader.model.bookmodel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.util.Stack;

import com.core.common.util.ArrayUtils;
import com.core.file.image.GBImage;
import com.core.log.L;
import com.core.text.model.GBHyperlinkType;
import com.core.text.model.GBTextModel;
import com.core.text.model.GBTextParagraph;
import com.core.text.model.GBTextWritableModel;
import com.core.text.model.style.GBTextStyleEntryProxy;

/**
 * 类名： BookReader.java<br>
 * 描述： 图书阅读器<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class BookReader {
    // 图书业务对象
    public final JavaBookModel Model;
    // 解析内容存储对象
    public GBTextWritableModel myCurrentTextModel = null;

    private boolean myTextParagraphExists = false;

    public void setMyTextParagraphExists(boolean myTextParagraphExists) {
        this.myTextParagraphExists = myTextParagraphExists;
    }

    private boolean myTextParagraphIsNonEmpty = false;

    private char[] myTextBuffer = new char[4096];
    private char[] myHeadTextBuffer = null;// head 标签内的元数据信息缓存
    // head标签内的元数据标签内容临时放到该缓存，每个标签结束即清空该缓存。
    private int myTextBufferLength;
    public int getMyTextBufferLength() {
        return myTextBufferLength;
    }
    private StringBuilder myContentsBuffer = new StringBuilder();

    private byte[] myKindStack = new byte[20];
    private int myKindStackSize;

    private byte myHyperlinkKind;
    private String myHyperlinkReference = "";

    private boolean myInsideTitle = false;
    private boolean mySectionContainsRegularContents = false;

    private TOCTree myCurrentContentsTree;

    private CharsetDecoder myByteDecoder;

    private byte metaMode = 0;

    public interface Meta {
        byte EMPTY = 0;
        byte STYLE = 1;
    }

    /**
     *
     * 功能描述：设置当前元数据模型（即head标签内的标签类型) 创建者： yangn<br>
     * 创建日期：2013-6-17<br>
     *
     * @param
     */
    public void settingMetaMode(byte metaVal) {
        this.metaMode = metaVal;
    }

    public BookReader(BookModel model) {
        Model = (JavaBookModel) model;
        myCurrentContentsTree = model.TOCTree;
    }

    public final void setByteDecoder(CharsetDecoder decoder) {
        myByteDecoder = decoder;
    }

    private final void flushTextBufferToParagraph() {
        if (myTextBufferLength > 0) {
            myCurrentTextModel.addText(myTextBuffer, 0, myTextBufferLength);
            myTextBufferLength = 0;
            if (myByteDecoder != null) {
                myByteDecoder.reset();
            }
        }
    }

    /**
     *
     * 功能描述：剪切head标签内数据 创建者： yangn<br>
     * 创建日期：2013-6-14<br>
     *
     * @param
     */
    public String getCutHeadBuffer() {
        /*
         * if(myTextBufferLength==0){ return ""; }else{
         */
        String ret = new String(myHeadTextBuffer);
        // myTextBufferLength=0;
        return ret;
        // }
    }

    /*
     * 添加一个标签标示
     * @param kind 标签标示
     * @param start 是否开始标签
     */
    public final void addControl(byte kind, boolean start) {
        if (myTextParagraphExists) {
            flushTextBufferToParagraph();
            myCurrentTextModel.addControl(kind, start);
        }
        if (!start && myHyperlinkReference.length() != 0 && kind == myHyperlinkKind) {
            myHyperlinkReference = "";
        }
    }

    public final void pushKind(byte kind) {
        byte[] stack = myKindStack;
        if (stack.length == myKindStackSize) {
            stack = ArrayUtils.createCopy(stack, myKindStackSize, myKindStackSize << 1);
            myKindStack = stack;
        }
        stack[myKindStackSize++] = kind;
    }

    public final boolean popKind() {
        if (myKindStackSize != 0) {
            --myKindStackSize;
            return true;
        }
        return false;
    }

    public final void beginParagraph() {
        beginParagraph(GBTextParagraph.Kind.TEXT_PARAGRAPH);
    }

    /*
     * 一个段落开始处理
     * @param kind 段落标签标示
     */
    public final void beginParagraph(byte kind) {
        endParagraph();
        final GBTextWritableModel textModel = myCurrentTextModel;
        if (textModel != null) {
            textModel.createParagraph(kind);
            final byte[] stack = myKindStack;
            final int size = myKindStackSize;
            for (int i = 0; i < size; ++i) {
                textModel.addControl(stack[i], true);
            }
            if (myHyperlinkReference.length() != 0) {
                textModel.addHyperlinkControl(myHyperlinkKind, hyperlinkType(myHyperlinkKind), myHyperlinkReference);
            }
            myTextParagraphExists = true;
        }
    }

    public final void endParagraph() {
        if (myTextParagraphExists) {
            flushTextBufferToParagraph();
            myTextParagraphExists = false;
            myTextParagraphIsNonEmpty = false;
        }
    }

    private final void insertEndParagraph(byte kind) {
        final GBTextWritableModel textModel = myCurrentTextModel;
        if (textModel != null /* && mySectionContainsRegularContents */) {
            int size = textModel.getParagraphsNumber(myCurrentTextModel.getWrithChpFileIndex());
            if (size > 0
                    && textModel.getParagraph(myCurrentTextModel.getWrithChpFileIndex(), size - 1).getKind() != kind) {
                textModel.createParagraph(kind);
                mySectionContainsRegularContents = false;
            }
        }
    }

    /**
     * 插入章结束符
     */
    public final void insertEndOfSectionParagraph() {
        insertEndParagraph(GBTextParagraph.Kind.END_OF_SECTION_PARAGRAPH);
    }

    /*
     * public final void insertEndOfTextParagraph() {
     * insertEndParagraph(GBTextParagraph.Kind.END_OF_TEXT_PARAGRAPH); }
     */

    public final void unsetCurrentTextModel() {
        if (myCurrentTextModel != null) {
            myCurrentTextModel.stopReading();
        }
        myCurrentTextModel = null;
    }

    public void settingGenerPageMode() {
        if (myCurrentTextModel != null && myCurrentTextModel != Model.GenerPageNumModel) {
            myCurrentTextModel.stopReading();
        }
        myCurrentTextModel = (GBTextWritableModel) Model.GenerPageNumModel;
    }

    public final void enterTitle() {
        myInsideTitle = true;
    }

    public final void exitTitle() {
        myInsideTitle = false;
    }

    public final void setMainTextModel() {
        if (myCurrentTextModel != null && myCurrentTextModel != Model.BookTextModel) {
            myCurrentTextModel.stopReading();
        }
        myCurrentTextModel = (GBTextWritableModel) Model.BookTextModel;

    }

    // public final void setFootnoteTextModel(String id) {
    // if (myCurrentTextModel != null && myCurrentTextModel !=
    // Model.BookTextModel) {
    // myCurrentTextModel.stopReading();
    // }
    // myCurrentTextModel = (GBTextWritableModel) Model.getFootnoteModel(id);
    // }

    public final void addData(char[] data) {
        addData(data, 0, data.length, false);
    }

    public final void addData(char[] data, int offset, int length, boolean direct) {
        if (!myTextParagraphExists || length == 0) {
            switch (this.metaMode) {
                case Meta.STYLE :
                    myHeadTextBuffer = new char[length];
                    System.arraycopy(data, offset, myHeadTextBuffer, 0, length);
                    break;
            }
            return;
        }
        // if (!myInsideTitle && !mySectionContainsRegularContents) {
        // 段落数据开始去空格处理
        while (length > 0 && Character.isWhitespace(data[offset])) {
            --length;
            ++offset;
        }
        if (length == 0) {
            return;
        }
        // }

        myTextParagraphIsNonEmpty = true;

        if (direct && myTextBufferLength == 0 && !myInsideTitle) {
            myCurrentTextModel.addText(data, offset, length);
        } else {
            final int oldLength = myTextBufferLength;
            final int newLength = oldLength + length;
            if (myTextBuffer.length < newLength) {
                myTextBuffer = ArrayUtils.createCopy(myTextBuffer, oldLength, newLength);
            }
            System.arraycopy(data, offset, myTextBuffer, oldLength, length);
            myTextBufferLength = newLength;
            if (myInsideTitle) {
                addContentsData(myTextBuffer, oldLength, length);
            }
        }
        if (!myInsideTitle) {
            mySectionContainsRegularContents = true;
        }
    }

    private byte[] myUnderflowByteBuffer = new byte[4];
    private int myUnderflowLength;

    public final void addByteData(byte[] data, int start, int length) {
        if (!myTextParagraphExists || length == 0) {
            return;
        }
        myTextParagraphIsNonEmpty = true;

        final int oldLength = myTextBufferLength;
        if (myTextBuffer.length < oldLength + length) {
            myTextBuffer = ArrayUtils.createCopy(myTextBuffer, oldLength, oldLength + length);
        }
        final CharBuffer cb = CharBuffer.wrap(myTextBuffer, myTextBufferLength, length);

        if (myUnderflowLength > 0) {
            int l = myUnderflowLength;
            while (length-- > 0 && l < 4) {
                myUnderflowByteBuffer[l++] = data[start++];
                final ByteBuffer ubb = ByteBuffer.wrap(myUnderflowByteBuffer);
                myByteDecoder.decode(ubb, cb, false);
                if (cb.position() != oldLength) {
                    myUnderflowLength = 0;
                    break;
                }
            }
            if (length == 0) {
                myUnderflowLength = l;
                return;
            }
        }

        ByteBuffer bb = ByteBuffer.wrap(data, start, length);
        myByteDecoder.decode(bb, cb, false);
        myTextBufferLength = cb.position();
        int rem = bb.remaining();
        if (rem > 0) {
            for (int i = 0, j = start + length - rem; i < rem;) {
                myUnderflowByteBuffer[i++] = data[j++];
            }
            myUnderflowLength = rem;
        }

        if (myInsideTitle) {
            addContentsData(myTextBuffer, oldLength, myTextBufferLength - oldLength);
        } else {
            mySectionContainsRegularContents = true;
        }
    }

    private static byte hyperlinkType(byte kind) {
        return (kind == GBTextKind.EXTERNAL_HYPERLINK) ? GBHyperlinkType.EXTERNAL : GBHyperlinkType.INTERNAL;
    }

    public final void addHyperlinkControl(byte kind, String label) {
        if (myTextParagraphExists) {
            flushTextBufferToParagraph();
            myCurrentTextModel.addHyperlinkControl(kind, hyperlinkType(kind), label);
        }
        myHyperlinkKind = kind;
        myHyperlinkReference = label;
    }

    /*
     * 添加一个超链接标记
     * @param label 标签id
     */
    public final void addHyperlinkLabel(String label) {

        final GBTextWritableModel textModel = myCurrentTextModel;
        if (textModel != null) {
            int paragraphNumber = textModel.getParagraphsNumber(myCurrentTextModel.getWrithChpFileIndex());
            if (myTextParagraphExists) {
                --paragraphNumber;
            }
            Model.addHyperlinkLabel(label, textModel, myCurrentTextModel.getWrithChpFileIndex(), paragraphNumber);
        }

    }

    public final void addHyperlinkLabel(String label, int chpFileIndex, int paragraphIndex) {
        Model.addHyperlinkLabel(label, myCurrentTextModel, chpFileIndex, paragraphIndex);

    }

    public final void addHtml5AudioControl(char[] label) {
        final GBTextWritableModel textModel = myCurrentTextModel;

        if (textModel != null) {
            beginParagraph(GBTextParagraph.Kind.TEXT_PARAGRAPH);
            textModel.addHtml5AudioControl(label);
            endParagraph();
        }

    }

    public final void addHtml5NoteControl(char[] label) {
        final GBTextWritableModel textModel = myCurrentTextModel;
        if (textModel != null) {
//            beginParagraph(GBTextParagraph.Kind.TEXT_PARAGRAPH);
            textModel.addHtml5NoteControl(label);
//            endParagraph();
        }

    }

    public final void addHtml5VideoControl(char[] label) {
        final GBTextWritableModel textModel = myCurrentTextModel;

        if (textModel != null) {
            beginParagraph(GBTextParagraph.Kind.TEXT_PARAGRAPH);
            textModel.addHtml5VideoControl(label);
            endParagraph();
        }

    }

    public final void addHtml5FileControl(char[] label) {
        final GBTextWritableModel textModel = myCurrentTextModel;

        if (textModel != null) {
            beginParagraph(GBTextParagraph.Kind.TEXT_PARAGRAPH);
            textModel.addHtml5Control(GBTextParagraph.Entry.HTML5_FILE_CTR, label);
            endParagraph();
        }

    }

    /*
     * public final void addStyleEntry(GBTextStyleEntry entry) {
     * myCurrentTextModel.addStyleEntry(entry); }
     */
    /**
     * 功能描述：添加样式段落<br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-21<br>
     *
     * @param entryProxy
     * @return
     */
    public final int addStyleEntry(GBTextStyleEntryProxy entryProxy) {
        beginParagraph(GBTextParagraph.Kind.CSS_PARAGRAPH);
        int ret = myCurrentTextModel.addStyleEntry(entryProxy);
        endParagraph();
        return ret;
    }

    /**
     * 功能描述： 添加样式段落结束标志<br>
     * 创建者： yangn<br>
     * 创建日期：2013-6-21<br>
     */
    public final void addStyleColse() {
        beginParagraph(GBTextParagraph.Kind.CSS_PARAGRAPH);
        myCurrentTextModel.addStyleColse();
        endParagraph();
    }

    /**
     * 功能描述： 添加段落内部样式<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-21<br>
     *
     * @param entryProxy
     * @return
     */
    public final int addStyleEntry2(GBTextStyleEntryProxy entryProxy) {
        flushTextBufferToParagraph();
        int ret = myCurrentTextModel.addStyleEntry(entryProxy);
        return ret;
    }

    /**
     * 功能描述：添加段落内部样式闭合标签<br>
     * 创建者： jack<br>
     * 创建日期：2013-6-21<br>
     */
    public final void addStyleColse2() {
        myCurrentTextModel.addStyleColse();
    }

    /**
     *
     * 功能描述：获取作用当前标签有效的样式 创建者： yangn<br>
     * 创建日期：2013-6-14<br>
     *
     * @param
     */
    public Stack<Integer> getStylePossible() {
        return myCurrentTextModel.getStylePossible();
    }

    // public final void addAudio(String src,String)

    public final void addContentsData(char[] data) {
        addContentsData(data, 0, data.length);
    }

    // 添加title文本信息
    public final void addContentsData(char[] data, int offset, int length) {
        if (length != 0 && myCurrentContentsTree != null) {
            myContentsBuffer.append(data, offset, length);
        }
    }

    public final boolean hasContentsData() {
        return myContentsBuffer.length() > 0;
    }

    public final void beginContentsParagraph(int referenceNumber) {
        beginContentsParagraph(Model.BookTextModel, referenceNumber);
    }

    /**
     * 功能描述： 文本段落开始<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-17<br>
     *
     * @param bookTextModel
     * @param referenceNumber
     */
    public final void beginContentsParagraph(GBTextModel bookTextModel, int referenceNumber) {
        final GBTextModel textModel = myCurrentTextModel;
        if (textModel == bookTextModel) {
            if (referenceNumber == -1) {
                referenceNumber = textModel.getChapterFileNumber(myCurrentTextModel.getWrithChpFileIndex());
                // referenceNumber = textModel.getParagraphsNumber();
            }
            TOCTree parentTree = myCurrentContentsTree;
            if (parentTree.Level > 0) {
                if (myContentsBuffer.length() > 0) {
                    parentTree.setText(myContentsBuffer.toString());
                    myContentsBuffer.delete(0, myContentsBuffer.length());
                } else if (parentTree.getText() == null) {
                    parentTree.setText("...");
                }
            } else {
                myContentsBuffer.delete(0, myContentsBuffer.length());
            }
            TOCTree tree = new TOCTree(parentTree);
            tree.setReference(myCurrentTextModel, referenceNumber, 0);// chpFileIndex
            // modify
            // by
            // yangn
            myCurrentContentsTree = tree;
        }
    }

    public final void endContentsParagraph() {
        final TOCTree tree = myCurrentContentsTree;
        if (tree.Level == 0) {
            myContentsBuffer.delete(0, myContentsBuffer.length());
            return;
        }
        if (myContentsBuffer.length() > 0) {
            tree.setText(myContentsBuffer.toString());
            myContentsBuffer.delete(0, myContentsBuffer.length());
        } else if (tree.getText() == null) {
            tree.setText("...");
        }
        myCurrentContentsTree = tree.Parent;
    }

    /*
     * public final void setReference(int contentsParagraphNumber, int
     * referenceNumber) { setReference(contentsParagraphNumber,
     * myCurrentTextModel, referenceNumber); } public final void
     * setReference(int contentsParagraphNumber, GBTextWritableModel textModel,
     * int referenceNumber) { final TOCTree contentsTree = Model.TOCTree; if
     * (contentsParagraphNumber < contentsTree.getSize()) {
     * contentsTree.getTreeByParagraphNumber
     * (contentsParagraphNumber).setReference( textModel, referenceNumber ); } }
     */

    public final boolean paragraphIsOpen() {
        return myTextParagraphExists;
    }

    public boolean paragraphIsNonEmpty() {
        return myTextParagraphIsNonEmpty;
    }

    public final boolean contentsParagraphIsOpen() {
        return myCurrentContentsTree.Level > 0;
    }

    public final void beginContentsParagraph() {
        beginContentsParagraph(-1);
    }

    public final void addImageReference(String ref, boolean isCover) {
        addImageReference(ref, (short) 0, isCover);
    }

    public final void addImageReference(String ref, short vOffset, boolean isCover) {
        final GBTextWritableModel textModel = myCurrentTextModel;
        if (textModel != null) {
            mySectionContainsRegularContents = true;
            if (myTextParagraphExists) {
                flushTextBufferToParagraph();
                textModel.addImage(ref, vOffset, isCover);
            } else {
                beginParagraph(GBTextParagraph.Kind.TEXT_PARAGRAPH);
                textModel.addControl(GBTextKind.IMAGE, true);
                textModel.addImage(ref, vOffset, isCover);
                textModel.addControl(GBTextKind.IMAGE, false);
                endParagraph();
            }
        }
    }

    public final void addImage(String id, GBImage image) {
        Model.addImage(id, image);
    }

    public final void addFixedHSpace(short length) {
        if (myTextParagraphExists) {
            myCurrentTextModel.addFixedHSpace(length);
        }
    }

    // /**
    // * 功能描述：文件解密方法<br>
    // * 创建者： jack<br>
    // * 创建日期：2012-11-20<br>
    // *
    // * @param bookFile
    // * 要解密的文件
    // * @param cacheDir
    // * 解密缓存地址
    // * @return file 解密后的图书文件
    // */
    // public static File decryptBook(File bookFile, File cacheDir)
    // throws GBBookException {
    // SecretKey sk = new SecretKey(1 + "", Build.BRAND, Build.MODEL,
    // GBLibrary.Instance().getPhoneSN());
    // String dest = GBBook.book.openBook(sk, bookFile.getAbsolutePath(),
    // cacheDir.getAbsolutePath());
    // if (dest != null)
    // return new File(dest);
    // else
    // return null;
    // }
}
