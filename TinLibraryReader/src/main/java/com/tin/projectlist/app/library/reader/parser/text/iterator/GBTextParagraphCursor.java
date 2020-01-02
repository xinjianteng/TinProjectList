package com.tin.projectlist.app.library.reader.parser.text.iterator;

import com.core.file.image.GBFileImage;
import com.core.file.image.GBImage;
import com.core.file.image.GBImageData;
import com.core.file.image.GBImageManager;
import com.core.text.linbreak.LineBreaker;
import com.core.text.model.GBAudioEntry;
import com.core.text.model.GBFileCtrEntry;
import com.core.text.model.GBImageEntry;
import com.core.text.model.GBNoteEntry;
import com.core.text.model.GBTextHyperlink;
import com.core.text.model.GBTextMark;
import com.core.text.model.GBTextModel;
import com.core.text.model.GBTextParagraph;
import com.core.text.model.GBVideoEntry;
import com.core.text.model.impl.GBTextTrParagraphImpl;
import com.core.text.widget.GBAnimObjElement;
import com.core.text.widget.GBAudioElement;
import com.core.text.widget.GBNoteElement;
import com.core.text.widget.GBTextControlElement;
import com.core.text.widget.GBTextElement;
import com.core.text.widget.GBTextFixedHSpaceElement;
import com.core.text.widget.GBTextHyperlinkControlElement;
import com.core.text.widget.GBTextImageElement;
import com.core.text.widget.GBTextStyleElement;
import com.core.text.widget.GBTextWord;
import com.core.text.widget.GBVideoElement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 描述： 文本段游标解析迭代器<br>
 * 创建者： 燕冠楠<br>
 * 创建日期：2013-4-2<br>
 */
public final class GBTextParagraphCursor implements Comparable<GBTextParagraphCursor> {

    /**
     *
     * 描述： 进度信息类 创建者： 燕冠楠<br>
     * 创建日期：2013-4-2<br>
     */
    private static final class Processor {
        final String TAG = "GBTextParagraphCursor$Processor";
        private final GBTextParagraph myParagraph;
        private final LineBreaker myLineBreaker;
        /**
         * 文本元素集合
         * */
        private final ArrayList<GBTextElement> myElements;
        private int myOffset;// 当前偏移量
        private int myFirstMark;
        private int myLastMark;
        private final List<GBTextMark> myMarks;
        private final int myParaIndex;
        public int myWordNum = 0;

        // 当前进度
        private Processor(GBTextParagraph paragraph, LineBreaker lineBreaker, List<GBTextMark> marks, int chpFileIndex,
                          int paragraphIndex, ArrayList<GBTextElement> elements) {
            myParagraph = paragraph;
            myLineBreaker = lineBreaker;
            myElements = elements;
            myMarks = marks;
            myParaIndex = paragraphIndex;
            // 获取当前段第一个书签
            final GBTextMark mark = new GBTextMark(chpFileIndex, paragraphIndex, 0, 0);
            int i;
            for (i = 0; i < myMarks.size(); i++) {
                if (((GBTextMark) myMarks.get(i)).compareTo(mark) >= 0) {
                    break;
                }
            }
            myFirstMark = i;
            myLastMark = myFirstMark;
            for (; (myLastMark != myMarks.size())
                    && (((GBTextMark) myMarks.get(myLastMark)).ParagraphIndex == paragraphIndex); myLastMark++);
            myOffset = 0;
        }

        /**
         * 数据填充为界面元素对象
         */
        int fill() {
            int hyperlinkDepth = 0;
            GBTextHyperlink hyperlink = null;

            final ArrayList<GBTextElement> elements = myElements;
            for (GBTextParagraph.EntryIterator it = myParagraph.iterator(); it.hasNext();) {
                it.next();
                switch (it.getType()) {
                    case GBTextParagraph.Entry.TEXT :
                        processTextEntry(it.getTextData(), it.getTextOffset(), it.getTextLength(), hyperlink);
                        break;
                    case GBTextParagraph.Entry.CONTROL :
                        if (hyperlink != null) {
                            hyperlinkDepth += it.getControlIsStart() ? 1 : -1;
                            if (hyperlinkDepth == 0) {
                                hyperlink = null;
                            }
                        }
                        elements.add(GBTextControlElement.get(it.getControlKind(), it.getControlIsStart()));
                        if (myParagraph instanceof GBTextTrParagraphImpl) {
                            // tr标签段处理 tr:42 43:th
                            final GBTextTrParagraphImpl tp = (GBTextTrParagraphImpl) myParagraph;
                            if ((it.getControlKind() == 42 || it.getControlKind() == 43) && it.getControlIsStart()) {
                                tp.mIndex.add(elements.size());
                                tp.mKinds.add(it.getControlKind());
                                tp.mChildNumber++;
                            }
                        }
                        break;
                    case GBTextParagraph.Entry.HYPERLINK_CONTROL : {
                        final byte hyperlinkType = it.getHyperlinkType();
                        if (hyperlinkType != 0) {
                            final GBTextHyperlinkControlElement control = new GBTextHyperlinkControlElement(
                                    it.getControlKind(), hyperlinkType, it.getHyperlinkId());// it.getHyperlinkChpFileIndex()+","
                            elements.add(control);
                            hyperlink = control.Hyperlink;
                            hyperlinkDepth = 1;
                        }
                        break;
                    }
                    case GBTextParagraph.Entry.IMAGE :
                        final GBImageEntry imageEntry = it.getImageEntry();
                        final GBImage image = imageEntry.getImage();
                        if (image != null) {
                            GBImageData data = GBImageManager.Instance().getImageData(image);
                            if (data != null) {
                                if (hyperlink != null) {
                                    hyperlink.addElementIndex(elements.size());
                                }
                                elements.add(new GBTextImageElement(imageEntry.Id, data, image.getURI(),
                                        imageEntry.IsCover));
                            }
                        }
                        break;
                    case GBTextParagraph.Entry.STYLE_CSS :
                        break;
                    case GBTextParagraph.Entry.STYLE_OTHER :
                        /*
                         * GBTextStyleEntryProxy proxy = (GBTextStyleEntryProxy)
                         * it.getStyleEntry(); GBTextFontStyleEntry fontStyle =
                         * proxy.getEntry(GBTextFontStyleEntry.class); if (null
                         * != fontStyle) { L.e(TAG, "parser=" +
                         * fontStyle.getColor()); }
                         */
                        elements.add(new GBTextStyleElement(it.getStyleEntry(), myParaIndex));
                        break;
                    case GBTextParagraph.Entry.STYLE_CLOSE :
                        // L.e(TAG, "parset= style close");
                        elements.add(GBTextElement.StyleClose);
                        break;
                    case GBTextParagraph.Entry.FIXED_HSPACE :
                        elements.add(GBTextFixedHSpaceElement.getElement(it.getFixedHSpaceLength()));
                        break;
                    case GBTextParagraph.Entry.HTML5_AUDIO_CONTROL :
                        final GBAudioEntry ae = it.getAudioEntry();
                        if (ae.getImage() != null)
                            elements.add(new GBAudioElement(ae.Src, (GBFileImage) ae.getImage(), ae.getImage().getURI()));
                        else
                            elements.add(new GBAudioElement(ae.Src, null, ""));
                        break;
                    case GBTextParagraph.Entry.HTML5_VIDEO_CONTROL :
                        final GBVideoEntry ve = it.getVideoEntry();
                        GBVideoElement elem = null;
                        if (ve.getImage() != null) {
                            elem = new GBVideoElement(ve.Src, (GBFileImage) ve.getImage(), ve.getImage().getURI());
                        } else {
                            elem = new GBVideoElement(ve.Src, null, "");
                        }
//                        elem.setWidth(ve.Width);
//                        elem.setHeight(ve.Height);
                        elements.add(elem);
                        break;
                    case GBTextParagraph.Entry.HTML5_NOTE_CONTROL :
                        final GBNoteEntry ne = it.getNoteEntry();
                        GBNoteElement nlem = null;
                        nlem = new GBNoteElement(ne.Value);
                        elements.add(nlem);
                        break;
                    case GBTextParagraph.Entry.HTML5_FILE_CTR :
                        final GBFileCtrEntry fce = it.getFileCtrEntry();
                        if (fce.getPathImage() != null) {
                            elements.add(new GBAnimObjElement(fce.Path, (GBFileImage) fce.getPathImage(), fce
                                    .getPathImage().getURI(), fce.PathTwo));
                        } else {
                            elements.add(new GBAnimObjElement(fce.Path, null, "", ""));
                        }
                        break;
                }
            }
            return myWordNum;
        }

        private static byte[] ourBreaks = new byte[1024];
        private static final int NO_SPACE = 0;
        private static final int SPACE = 1;
        // private static final int NON_BREAKABLE_SPACE = 2;

        /**
         * 功能描述：生成一个文本元素<br>
         * 创建者： yangn<br>
         * 创建日期：2013-4-11<br>
         *
         * @param data char[]
         * @param offset 偏移量
         * @param length 长度
         */
        private void processTextEntry(final char[] data, final int offset, final int length, GBTextHyperlink hyperlink) {
            if (length != 0) {
                // L.d(TAG, new String(data,offset,length));

                if (ourBreaks.length < length) {
                    ourBreaks = new byte[length];
                }
                final byte[] breaks = ourBreaks;
                myLineBreaker.setLineBreaks(data, offset, length, breaks);

                final GBTextElement hSpace = GBTextElement.HSpace;
                final ArrayList<GBTextElement> elements = myElements;
                char ch = 0;
                char previousChar = 0;
                int spaceState = NO_SPACE;
                int wordStart = 0;
                for (int index = 0; index < length; ++index) {
                    previousChar = ch;
                    ch = data[offset + index];
                    if (Character.isSpace(ch)) {
                        if (index > 0 && spaceState == NO_SPACE) {
                            addWord(data, offset + wordStart, index - wordStart, myOffset + wordStart, hyperlink);
                        }
                        spaceState = SPACE;// 标记空格
                    } else {
                        switch (spaceState) {
                            case SPACE :
                                // if (breaks[index - 1] == LineBreak.NOBREAK ||
                                // previousChar == '-') {
                                // }
                                elements.add(hSpace);
                                wordStart = index;
                                break;
                            // case NON_BREAKABLE_SPACE:
                            // break;
                            case NO_SPACE :
                                if (index > 0 && breaks[index - 1] != LineBreaker.NOBREAK && previousChar != '-'
                                        && index != wordStart) {
                                    addWord(data, offset + wordStart, index - wordStart, myOffset + wordStart,
                                            hyperlink);
                                    wordStart = index;
                                }
                                break;
                        }
                        spaceState = NO_SPACE;
                    }
                }
                switch (spaceState) {
                    case SPACE :
                        elements.add(hSpace);
                        break;
                    // case NON_BREAKABLE_SPACE:
                    // break;
                    case NO_SPACE :
                        addWord(data, offset + wordStart, length - wordStart, myOffset + wordStart, hyperlink);
                        break;
                }
                myOffset += length;
            }
        }

        /**
         * 功能描述： 添加文字<br>
         * 创建者： yangn<br>
         * 创建日期：2013-4-11<br>
         *
         * @param char[]
         * @param offset 偏移量
         * @param len 长度
         * @param 段偏移量
         * @param 超链接信息 没有则为null
         */
        private final void addWord(char[] data, int offset, int len, int paragraphOffset, GBTextHyperlink hyperlink) {
            // String str = new String(data, offset, len);
            // System.out.println(offset + "," + len + "str====" + str);
            GBTextWord word = new GBTextWord(data, offset, len, paragraphOffset);
            for (int i = myFirstMark; i < myLastMark; ++i) {
                final GBTextMark mark = (GBTextMark) myMarks.get(i);
                if ((mark.Offset < paragraphOffset + len) && (mark.Offset + mark.Length > paragraphOffset)) {
                    word.addMark(mark.Offset - paragraphOffset, mark.Length);
                }
            }
            if (hyperlink != null) {
                hyperlink.addElementIndex(myElements.size());
            }
            myWordNum += word.Length;
            myElements.add(word);
        }
    }

    public final int chpFileIndex; // 章节索引
    public final int Index; // 段落索引
    public final byte kind;
    public final GBTextModel Model;
    public GBTextParagraph mParagraph;
    public int wordNum = 0;
    private final ArrayList<GBTextElement> myElements = new ArrayList<GBTextElement>();// 文字集合
    // 字符串
    // GBTextWord

    /**
     * 段指针 用于解析一段信息
     *
     * @param model 数据模型
     * @param chpFileIndex章节文件索引
     * @param index 段落索引
     */
    private GBTextParagraphCursor(GBTextModel model, int chpFileIndex, int index) {
        Model = model;
        this.chpFileIndex = chpFileIndex >= 0 ? chpFileIndex : 0;
        // 较小的一个
        index = Math.min(index, Model.getParagraphsNumber(chpFileIndex) - 1);
        Index = index > 0 ? index : 0;
        kind = model.getParagraph(this.chpFileIndex, Index).getKind();
        wordNum = 0;
        fill();
    }
    /**
     * 功能描述： 创建段落游标<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-5<br>
     *
     * @param model 数据模型
     * @param chpFileIndex 章节索引
     * @param index 段落索引
     * @return
     */
    public static GBTextParagraphCursor cursor(GBTextModel model, int chpFileIndex, int index) {
        GBTextParagraphCursor result = GBTextParagraphCursorCache.get(model, chpFileIndex, index);
        if (result == null) {
            result = new GBTextParagraphCursor(model, chpFileIndex, index);
            GBTextParagraphCursorCache.put(model, chpFileIndex, index, result);
        }
        return result;
    }

    private static final char[] SPACE_ARRAY = {' '};
    /**
     * 功能描述：解析char[]转换为界面元素 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    public void fill() {
        mParagraph = Model.getParagraph(chpFileIndex, Index);
        switch (mParagraph.getKind()) {
            case GBTextParagraph.Kind.CSS_PARAGRAPH :
            case GBTextParagraph.Kind.TREE_PARAGRAPH :
            case GBTextParagraph.Kind.TEXT_PARAGRAPH :// 安段类型填充数据
                LineBreaker lineBreaker = new LineBreaker(Model.getLanguage());
                wordNum = new Processor(mParagraph, lineBreaker, Model.getMarks(), chpFileIndex, Index, myElements)
                        .fill();
                break;
            case GBTextParagraph.Kind.EMPTY_LINE_PARAGRAPH :
                wordNum++;
                myElements.add(new GBTextWord(SPACE_ARRAY, 0, 1, 0));
                break;
            default :
                break;
        }
    }

    /**
     * 清空段元素 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    public void clear() {
        myElements.clear();
    }

    public boolean isFirst() {
        return chpFileIndex == 0 && Index == 0;
    }

    public boolean isLast() {
        return (chpFileIndex + 1 >= Model.getChapterSize() && Index + 1 >= Model.getParagraphsNumber(chpFileIndex));
    }

    public boolean isEndOfSection() {
        return Index + 1 >= Model.getParagraphsNumber(chpFileIndex);
        // return (Model.getParagraph(chpFileIndex, Index).getKind() ==
        // GBTextParagraph.Kind.END_OF_SECTION_PARAGRAPH);
    }

    /**
     * 段元素数量 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    public int getParagraphLength() {
        return myElements.size();
    }

    /**
     * 上一段 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    public GBTextParagraphCursor previous() {
        return isFirst() ? null : cursor(Model, Index <= 0 ? chpFileIndex - 1 : chpFileIndex,
                Index <= 0 ? Model.getParagraphsNumber(chpFileIndex - 1) - 1 : Index - 1);
    }

    /**
     *
     * 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-9<br>
     *
     * @param
     */
    public GBTextParagraphCursor next() {
        return isLast() ? null : cursor(Model, Index + 1 >= Model.getParagraphsNumber(chpFileIndex)
                ? chpFileIndex + 1
                : chpFileIndex, Index + 1 >= Model.getParagraphsNumber(chpFileIndex) ? 0 : Index + 1);
    }

    /**
     * 根据索引获取段元素 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    public GBTextElement getElement(int index) {
        try {
            return myElements.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 功能描述： 获取当前段信息 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-11<br>
     *
     * @param
     */
    GBTextParagraph getParagraph() {
        return mParagraph;// Model.getParagraph(chpFileIndex, Index);
    }

    public boolean isTreeParagraph() {
        return mParagraph instanceof GBTextTrParagraphImpl;
    }

    /*
     * 获取语音文本
     * @param index 指定索引
     */
    public String getSpeechTxt(int index) {
        StringBuffer sb = new StringBuffer("");
        for (int i = index; i < myElements.size(); i++) {// GBTextElement e :
            // myElements) {
            if (myElements.get(i) instanceof GBTextWord) {
                sb.append(myElements.get(i).toString());
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        // return "GBTextParagraphCursor [" + Index + " (0.." +
        // myElements.size() + ")]";
        StringBuffer sb = new StringBuffer("");
        for (GBTextElement e : myElements) {
            if (e instanceof GBTextControlElement) {
                sb.append("control:" + ((GBTextControlElement) e).Kind + ";");
            } else {
                sb.append(e.toString() + ";");
            }

        }
        return sb.toString();
    }
    @Override
    public int compareTo(GBTextParagraphCursor another) {
        if (another.chpFileIndex < chpFileIndex)
            return -1;
        if (another.chpFileIndex > chpFileIndex)
            return 1;
        if (another.Index < Index)
            return -1;
        if (another.Index > Index)
            return 1;

        return 0;
    }
    /**
     * 功能描述： 是否txt目录<br>
     * 创建者： jack<br>
     * 创建日期：2014-12-18<br>
     *
     * @param
     */
    public boolean isTxtCatalog() {
        if (myElements.size() > 0) {
            if (myElements.get(0) instanceof GBTextControlElement) {
                return ((GBTextControlElement) myElements.get(0)).Kind == 35;
            }
        }
        return false;
    }

}
