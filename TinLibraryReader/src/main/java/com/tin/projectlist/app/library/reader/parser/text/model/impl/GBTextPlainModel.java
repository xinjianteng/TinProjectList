package com.tin.projectlist.app.library.reader.parser.text.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.reader.parser.common.util.GBSearchUtil;
import com.tin.projectlist.app.library.reader.parser.file.image.GBImage;
import com.tin.projectlist.app.library.reader.parser.object.GBSearchPattern;
import com.tin.projectlist.app.library.reader.parser.text.model.GBAudioEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.GBFileCtrEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.GBImageEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.GBNoteEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextMark;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextModel;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextParagraph;
import com.tin.projectlist.app.library.reader.parser.text.model.GBVideoEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.cache.CharStorage;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntry;
import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntryProxy;
import com.tin.projectlist.app.library.reader.parser.xml.GBIntMap;

public class GBTextPlainModel implements GBTextModel, GBTextStyleEntry.Feature {
    final String TAG = "GBTextPlainModel";

    private final String myId;
    private final String myLanguage;
    // 段落信息数组
    protected int[][] myStartEntryIndices; // 段落所在缓存列表的下标[文件id][段对应缓存索引]
    protected int[][] myStartEntryOffsets; // 段落所在缓存对象的开始下标[文件id][段开始索引]
    protected int[][] myParagraphLengths; // 段落的长度[文件id][段对应长度]
    protected int[][] myTextSizes; // [文件id][段文本长度]该段落文字长度数组
    // len[len,len[0]+len,len[1]+len]
    protected byte[][] myParagraphKinds; // 段落类型数组[文件id][段落类型]

    protected int[] myParagraphsNumber; // 图书章节段落数[文件id]
    protected int myTotalParagrapsNumber = 0; // 图书从段落数
    protected int mChpFileNum, mChapterSize;

    protected volatile GBIntMap mChpFileNumMapping = new GBIntMap();

    protected int[][][] myStyleParagraphs;// 记录容器样式链接[文件id][段id][样式段id]
    protected CharStorage myStorage; // 缓存对象
    protected final Map<String, GBImage> myImageMap; // 图片集合

    private ArrayList<GBTextMark> myMarks; // 书签集合

    /**
     * 根据章节文件索引获取该章节文本长度
     *
     * @param chpFileIndex
     * @return
     */
    public int[] getTextSizesByChpFileIndex(int chpFileIndex) {
        if (chpFileIndex >= myTextSizes.length) {
            return null;
        }
        return myTextSizes[chpFileIndex];
    }

    public void resetTextSize(int chpFileIndex) {
        int size = myTextSizes[chpFileIndex].length;
        myTextSizes[chpFileIndex] = new int[size];
    }

    public void resetParagraphsNumber(int chpFileIndex) {
        myParagraphsNumber[chpFileIndex] = 0;

    }

    public void setTextSize(int chpFileIndex, int[] textSize) {
        myTextSizes[chpFileIndex] = textSize;
        // System.arraycopy(textSize, 0, myTextSizes[chpFileIndex], 0,
        // myTextSizes[chpFileIndex].length);
    }

    public void setTextSize(int chpFileIndex, int[] textSize, int arraySize) {
        // myTextSizes[chpFileIndex] = textSize;
        if (myTextSizes[chpFileIndex].length < arraySize) {
            myTextSizes[chpFileIndex] = new int[arraySize];
        }
        System.arraycopy(textSize, 0, myTextSizes[chpFileIndex], 0, arraySize);
    }

    public void setParagraphsNumber(int chpFileIndex, int paragraphsNumber) {
        myParagraphsNumber[chpFileIndex] = paragraphsNumber;
    }

    // public void setParagraphsNumber(int[] paragraphsNumber) {
    // myParagraphsNumber = paragraphsNumber;
    // }

    public int[] getParagraphsNumberAll() {
        return myParagraphsNumber;
    }

    public void setTextSize(int[][] textSize) {
        myTextSizes = textSize;
    }

    public int[][] getTextSizeAll() {
        return myTextSizes;
    }

    /**
     * 根据章节文件索引获取该章节段数
     *
     * @param chpFileIndex
     * @return
     */
    public int getParagraphsNumberByChpFileIndex(int chpFileIndex) {
        if (chpFileIndex >= myParagraphsNumber.length) {
            return -1;
        }
        return myParagraphsNumber[chpFileIndex];
    }

    /**
     *
     * 描述： 段 迭代器 实现解析char[]结构数据 创建者： 燕冠楠<br>
     * 创建日期：2013-4-2<br>
     */
    public final class EntryIteratorImpl implements GBTextParagraph.EntryIterator {
        private int myCounter; // 当前读取到段落的位置
        private int myLength; // 当前读取到的段落长度
        private byte myType; // 当前读取到的数据段落类型

        int mychpFileIndex; // 读取的章节下标
        int myDataIndex; // 读取的缓存下标
        int myDataOffset; // 当前正在读取char数组的下标

        // TextEntry data
        private char[] myTextData;
        private int myTextOffset;
        private int myTextLength;

        // ControlEntry data
        private byte myControlKind;
        private boolean myControlIsStart;
        // HyperlinkControlEntry data
        private byte myHyperlinkType;
        private String myHyperlinkId;
        private int myHyperlinkChpFileIndex;
        // ImageEntry
        private GBImageEntry myImageEntry;
        // html5 audio and video controls
        private GBAudioEntry myAudioEntry;
        private GBVideoEntry myVideoEntry;
        private GBNoteEntry myNoteEntry;
        private GBFileCtrEntry myCtrEntry;
        // StyleEntry
        private GBTextStyleEntryProxy myStyleEntry;

        // style link parent style offset
        private int[] styleLink;
        // FixedHSpaceEntry data
        private short myFixedHSpaceLength;

        /**
         * 根据index 实例化迭代起
         *
         * @param chpFileIndex 文件id
         * @param index 段落索引
         */
        EntryIteratorImpl(int chpFileIndex, int index) {
            try {
                myLength = myParagraphLengths[getChapterFileNumber(chpFileIndex)][index];
                myDataIndex = myStartEntryIndices[getChapterFileNumber(chpFileIndex)][index];
                myDataOffset = myStartEntryOffsets[getChapterFileNumber(chpFileIndex)][index];
                mychpFileIndex = chpFileIndex;
            } catch (Exception ex) {
                LogUtils.i("GBTextPainModel$iterator", "EntryIteratorImpl outofIndex" + getChapterFileNumber(chpFileIndex)
                        + "--" + index);
            }
            // L.i("GBTextPainModel$iterator",
            // getChapterFileNumber(chpFileIndex) + "--:" + myLength + "," +
            // myDataIndex+","+myDataOffset+","+mychpFileIndex+","+index);
        }

        void reset(int chpFileIndex, int index) {
            myCounter = 0;
            myLength = myParagraphLengths[getChapterFileNumber(chpFileIndex)][index];
            myDataIndex = myStartEntryIndices[getChapterFileNumber(chpFileIndex)][index];
            myDataOffset = myStartEntryOffsets[getChapterFileNumber(chpFileIndex)][index];
            mychpFileIndex = chpFileIndex;
        }

        public byte getType() {
            return myType;
        }

        public char[] getTextData() {
            return myTextData;
        }

        public int getTextOffset() {
            return myTextOffset;
        }

        public int getTextLength() {
            return myTextLength;
        }

        public byte getControlKind() {
            return myControlKind;
        }

        public boolean getControlIsStart() {
            return myControlIsStart;
        }

        public byte getHyperlinkType() {
            return myHyperlinkType;
        }

        public String getHyperlinkId() {
            return myHyperlinkId;
        }

        public GBImageEntry getImageEntry() {
            return myImageEntry;
        }

        public GBAudioEntry getAudioEntry() {
            return myAudioEntry;
        }

        public GBVideoEntry getVideoEntry() {
            return myVideoEntry;
        }

        public GBNoteEntry getNoteEntry() {
            return myNoteEntry;
        }

        @Override
        public GBFileCtrEntry getFileCtrEntry() {
            return myCtrEntry;
        }

        public GBTextStyleEntryProxy getStyleEntry() {
            return myStyleEntry;
        }

        public short getFixedHSpaceLength() {
            return myFixedHSpaceLength;
        }

        public boolean hasNext() {
            return myCounter < myLength;
        }

        /**
         * 解析内存或缓存char[]数据
         */
        public void next() {

            int dataOffset = myDataOffset;
            char[] data = myStorage.block(mychpFileIndex, myDataIndex);
            /*
             * try { if ((byte) data[dataOffset - 1] ==
             * GBTextParagraph.Entry.STYLE_CLOSE) { myDataOffset--; dataOffset =
             * myDataOffset; } } catch (ArrayIndexOutOfBoundsException ex) { }
             */
            if (data == null) {
                myCounter = myLength;
                return;
            }
            if (dataOffset == data.length) {// 如果dataOffset没有数据则获取下一个索引对应的block
                data = myStorage.block(mychpFileIndex, ++myDataIndex);
                if (data == null) {
                    myCounter = myLength;
                    return;
                }
                dataOffset = 0;
            }
            byte type = (byte) data[dataOffset];// 获取标签信息
            if (type == 0 && myStorage.size() > myDataIndex + 1) {
                data = myStorage.block(mychpFileIndex, ++myDataIndex);
                if (data == null) {
                    myCounter = myLength;
                    return;
                }
                dataOffset = 0;
                type = (byte) data[0];
            } else if (type == 0) {
                myCounter = myLength;
                return;
            }
            myType = type;
            ++dataOffset;
            switch (type) {
                case GBTextParagraph.Entry.TEXT : { // 获取文本长度
                    int textLength = (int) data[dataOffset++] + (((int) data[dataOffset++]) << 16);
                    if (textLength > data.length - dataOffset) {
                        textLength = data.length - dataOffset;
                    }
                    myTextLength = textLength;
                    myTextData = data;
                    myTextOffset = dataOffset;
                    dataOffset += textLength;
                    break;
                }
                case GBTextParagraph.Entry.CONTROL : {
                    short kind = (short) data[dataOffset++];
                    myControlKind = (byte) kind;
                    myControlIsStart = (kind & 0x0100) == 0x0100;
                    myHyperlinkType = 0;
                    break;
                }
                case GBTextParagraph.Entry.HYPERLINK_CONTROL : {
                    short kind = (short) data[dataOffset++];
                    myControlKind = (byte) kind;
                    myControlIsStart = true;
                    myHyperlinkType = (byte) (kind >> 8);
                    short labelLength = (short) data[dataOffset++];
                    try {
                        myHyperlinkId = new String(data, dataOffset, labelLength);
                        dataOffset += labelLength;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        LogUtils.i("EntryIteratorImpl", mychpFileIndex + "----" + myDataIndex);
                    }
                    break;
                }
                case GBTextParagraph.Entry.IMAGE : {
                    final short vOffset = (short) data[dataOffset++];
                    final short len = (short) data[dataOffset++];
                    try {
                        final String id = new String(data, dataOffset, len);
                        dataOffset += len;
                        final boolean isCover = data[dataOffset++] != 0;
                        myImageEntry = new GBImageEntry(myImageMap, id, vOffset, isCover);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        LogUtils.i("EntryIteratorImpl", mychpFileIndex + "----" + myDataIndex);
                    }
                    break;
                }
                case GBTextParagraph.Entry.FIXED_HSPACE :
                    myFixedHSpaceLength = (short) data[dataOffset++];
                    break;
                case GBTextParagraph.Entry.STYLE_CSS :
                case GBTextParagraph.Entry.STYLE_OTHER : {

                    short len = (short) data[dataOffset++];
                    if (len > data.length - dataOffset) {
                        len = (short) (data.length - dataOffset);
                    }

                    try {
                        myStyleEntry = new GBTextStyleEntryProxy(data, dataOffset, len);
                        /*
                         * if (null != myStyleEntry) {
                         * GBTextBackgroundStyleEntry bg =
                         * myStyleEntry.getEntry(
                         * GBTextBackgroundStyleEntry.class); if (bg != null) {
                         * L.e(TAG, "bg=" + bg.getBackgroundColor()); } }
                         */
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtils.e("GBTextPlainModel", mychpFileIndex + "----" + myDataIndex);
                    }
                    dataOffset += len;

                    break;
                }
                case GBTextParagraph.Entry.STYLE_CLOSE :
                    // No data
                    break;
                case GBTextParagraph.Entry.RESET_BIDI :
                    // No data
                    break;
                case GBTextParagraph.Entry.HTML5_AUDIO_CONTROL :
                    short len = (short) data[dataOffset++];
                    // modify by jack 异常数据越界 2014，01，27
                    if (len >= data.length - dataOffset) {
                        len = (short) (data.length - dataOffset - 1);
                    }
                    myAudioEntry = new GBAudioEntry(myImageMap);
                    myAudioEntry.loadData(data, dataOffset, dataOffset + len);
                    dataOffset += len;
                    // L.e(TAG, "GBTextParagraph.Entry.HTML5_AUDIO_CONTROL"
                    // + GBTextParagraph.Entry.HTML5_AUDIO_CONTROL + "ss="
                    // + myAudioAndVideoEntry.Src);
                    break;
                case GBTextParagraph.Entry.HTML5_VIDEO_CONTROL :
                    len = (short) data[dataOffset++];
                    // modify by jack 异常数据越界 2014，01，27
                    if (len >= data.length - dataOffset) {
                        len = (short) (data.length - dataOffset - 1);
                    }
                    myVideoEntry = new GBVideoEntry(myImageMap);
                    myVideoEntry.loadData(data, dataOffset, dataOffset + len);
                    dataOffset += len;
                    // L.e(TAG, "GBTextParagraph.Entry.HTML5_VIDEO_CONTROL"
                    // + GBTextParagraph.Entry.HTML5_VIDEO_CONTROL + "ss="
                    // + myAudioAndVideoEntry.Src);
                    break;
                case GBTextParagraph.Entry.HTML5_NOTE_CONTROL ://批注
                    len = (short) data[dataOffset++];
                    if (len >= data.length - dataOffset) {
                        len = (short) (data.length - dataOffset - 1);
                    }
                    myNoteEntry = new GBNoteEntry();
                    myNoteEntry.loadData(data, dataOffset, dataOffset + len);
                    dataOffset += len;
                    break;
                case GBTextParagraph.Entry.HTML5_FILE_CTR :
                    len = (short) data[dataOffset++];
                    if (len > data.length - dataOffset) {
                        len = (short) (data.length - dataOffset);
                    }
                    myCtrEntry = new GBFileCtrEntry(myImageMap);
                    myCtrEntry.loadData(data, dataOffset, dataOffset + len);
                    dataOffset += len;
                    LogUtils.e(TAG, "GBTextParagraph.Entry.HTML5_VIDEO_CONTROL" + GBTextParagraph.Entry.HTML5_FILE_CTR
                            + "path=" + myCtrEntry.Path + "path2=" + myCtrEntry.PathTwo + myCtrEntry.getPathImage()
                            + "ss" + myCtrEntry.getPathTwoImage());
                    break;
            /*
             * case GBTextParagraph.Entry.STYLE_LINK: // break;
             */
            }
            ++myCounter;
            myDataOffset = dataOffset;
        }

        @Override
        public int getHyperlinkChpFileIndex() {
            return myHyperlinkChpFileIndex;

        }

    }

    protected GBTextPlainModel(String id, String language, int[][] entryIndices, int[][] entryOffsets,
                               int[][] paragraphLenghts, int[][] textSizes, byte[][] paragraphKinds, CharStorage storage,
                               Map<String, GBImage> imageMap, int[][][] styleParagraph, int[] paragraphNumber) {
        myId = id;
        myLanguage = language;
        myStartEntryIndices = entryIndices;
        myStartEntryOffsets = entryOffsets;
        myParagraphLengths = paragraphLenghts;
        myTextSizes = textSizes;

        myParagraphKinds = paragraphKinds;
        myStorage = storage;
        myImageMap = imageMap;
        myStyleParagraphs = styleParagraph;
        myParagraphsNumber = paragraphNumber;

    }

    protected GBTextPlainModel(String id, String language, Map<String, GBImage> imageMap) {
        myId = id;
        myLanguage = language;
        myImageMap = imageMap;

    }

    public final String getId() {
        return myId;
    }

    public final String getLanguage() {
        return myLanguage;
    }

    public final GBTextMark getFirstMark() {
        return ((myMarks == null) || myMarks.isEmpty()) ? null : myMarks.get(0);
    }

    public final GBTextMark getLastMark() {
        return ((myMarks == null) || myMarks.isEmpty()) ? null : myMarks.get(myMarks.size() - 1);
    }

    public final GBTextMark getNextMark(GBTextMark position) {
        if ((position == null) || (myMarks == null)) {
            return null;
        }

        GBTextMark mark = null;
        for (GBTextMark current : myMarks) {
            if (current.compareTo(position) >= 0) {
                if ((mark == null) || (mark.compareTo(current) > 0)) {
                    mark = current;
                }
            }
        }
        return mark;
    }

    public final GBTextMark getPreviousMark(GBTextMark position) {
        if ((position == null) || (myMarks == null)) {
            return null;
        }

        GBTextMark mark = null;
        for (GBTextMark current : myMarks) {
            if (current.compareTo(position) < 0) {
                if ((mark == null) || (mark.compareTo(current) < 0)) {
                    mark = current;
                }
            }
        }
        return mark;
    }

    /**
     * 功能描述： 内容搜索<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-5<br>
     *
     * @param text 要搜索的文本
     * @param startChpFileIndex 开始文件索引
     * @param startIndex 开始段落
     * @param endChpFileIndex 结束文件索引
     * @param endIndex 结束段落
     * @param ignoreCase 是否忽略大小写
     * @return
     */
    public final int search(final String text, int startChpFileIndex, int startIndex, int endChpFileIndex,
                            int endIndex, boolean ignoreCase) {
        int count = 0;
        GBSearchPattern pattern = new GBSearchPattern(text, ignoreCase);
        myMarks = new ArrayList<GBTextMark>();
        if (startChpFileIndex > myParagraphsNumber.length) {
            startChpFileIndex = myParagraphsNumber.length;
        }
        if (endChpFileIndex > myParagraphsNumber.length) {
            endChpFileIndex = myParagraphsNumber.length;
        }

        if (startIndex > myParagraphsNumber[startIndex]) {
            startIndex = myParagraphsNumber[startIndex];
        }
        if (endIndex > myParagraphsNumber[endIndex]) {
            endIndex = myParagraphsNumber[endIndex];
        }

        int index = startIndex;
        int chpFileIndex = startChpFileIndex;
        final EntryIteratorImpl it = new EntryIteratorImpl(chpFileIndex, index);
        while (true) {
            int offset = 0;
            while (it.hasNext()) {
                it.next();
                if (it.getType() == GBTextParagraph.Entry.TEXT) {
                    char[] textData = it.getTextData();
                    int textOffset = it.getTextOffset();
                    int textLength = it.getTextLength();
                    for (int pos = GBSearchUtil.find(textData, textOffset, textLength, pattern); pos != -1; pos = GBSearchUtil
                            .find(textData, textOffset, textLength, pattern, pos + 1)) {
                        myMarks.add(new GBTextMark(chpFileIndex, index, offset + pos, pattern.getLength()));
                        ++count;
                    }
                    offset += textLength;
                }
            }

            if (++index > myParagraphsNumber[chpFileIndex] && chpFileIndex < endChpFileIndex) {
                ++chpFileIndex;
                index = 0;
            } else if (chpFileIndex > endChpFileIndex) {
                break;
            } else if (chpFileIndex == endChpFileIndex && index > myParagraphsNumber[chpFileIndex]) {
                break;
            } else if (chpFileIndex == endChpFileIndex && index < myParagraphsNumber[chpFileIndex] && index >= endIndex) {
                break;
            }
            // if (++index >= endIndex) {
            // break;
            // }
            it.reset(chpFileIndex, index);
        }
        return count;
    }

    public final List<GBTextMark> getMarks() {
        return (myMarks != null) ? myMarks : Collections.<GBTextMark> emptyList();
    }

    public final void removeAllMarks() {
        myMarks = null;
    }

    @Override
    public final int getParagraphsNumber(int chpFileIndex) {
        return myParagraphsNumber[chpFileIndex];
    }

    @Override
    public final int getParagraphsNumber() {
        return myParagraphsNumber[mChpFileNum];
    }

    /*
     * public final GBTextParagraph getParagraph(int chpFileIndex, int index) {
     * final byte kind = myParagraphKinds[chpFileIndex][index]; return (kind ==
     * GBTextParagraph.Kind.TEXT_PARAGRAPH) ? new GBTextParagraphImpl( this,
     * chpFileIndex, index) : new GBTextSpecialParagraphImpl( kind, this,
     * chpFileIndex, index); }
     */

    /**
     * 功能描述： 获取指定段落之前的总文本长度<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-5<br>
     *
     * @param chpFileIndex
     * @param index
     * @return
     */
    public final int getTextLength(int chpFileIndex, int index) {
        // L.e(TAG, "myTextSizes" + chpFileIndex);
        return myTextSizes[chpFileIndex][Math.max(Math.min(index, myParagraphsNumber[chpFileIndex] - 1), 0)];
    }

    /*
     * @Override public int getTextOffset(int chpFileIndex, int index) { return
     * myStartEntryOffsets
     * [getChapterFileNumber(chpFileIndex)][Math.max(Math.min(index,
     * myParagraphsNumber[chpFileIndex] - 1), 0)]; }
     */

    /**
     *
     * 功能描述： 根据样式段码获取包含其的样式标签段码 创建者： yangn<br>
     * 创建日期：2013-6-3<br>
     *
     * @param 样式段码
     * @return 包含其的样式段码数组
     */
    @Override
    public final int[] getStyleParagraphIncluded(int chpFileIndex, int index) {
        if (myStyleParagraphs != null)
            return myStyleParagraphs[getChapterFileNumber(chpFileIndex)][Math.max(
                    Math.min(index, myParagraphsNumber[chpFileIndex] - 1), 0)];
        return null;
    }

    /**
     * 功能描述： 数组定位<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-8<br>
     *
     * @param array 要定位的段落长度数组
     * @param length 总段落数
     * @param value 需要定位的长度
     * @return
     */
    private static PositionInfo binarySearch(int[][] array, int[] length, int value) {
        int chapFileIndex = 0;
        // 查找章节下标
        int lowChpFileIndex = 0;
        int highChpFileIndex = length.length - 1;
        while (length[highChpFileIndex] <= 0) {
            highChpFileIndex--;
            if (highChpFileIndex <= lowChpFileIndex)
                break;
        }
        if (value >= array[highChpFileIndex][length[highChpFileIndex] - 1]) {
            return new PositionInfo(highChpFileIndex, length[highChpFileIndex] - 1);
        } else if (value <= 0) {
            return new PositionInfo(0, 0);
        }

        while (lowChpFileIndex <= highChpFileIndex) {
            int midChpFileIndex = (lowChpFileIndex + highChpFileIndex) >>> 1;
            while (length[midChpFileIndex] <= 0) {
                midChpFileIndex--;
                if (midChpFileIndex <= lowChpFileIndex) {
                    return new PositionInfo(midChpFileIndex, length[midChpFileIndex] - 1);
                }
            }
            if (value > array[midChpFileIndex][length[midChpFileIndex] - 1]) {
                if (lowChpFileIndex < midChpFileIndex)
                    lowChpFileIndex = midChpFileIndex;
                else {
                    chapFileIndex = midChpFileIndex + 1;
                    break;
                }

            } else if (value < array[midChpFileIndex][0]) {
                highChpFileIndex = midChpFileIndex;
            } else if (value > array[midChpFileIndex][0] && value < array[midChpFileIndex][length[midChpFileIndex] - 1]) {
                chapFileIndex = midChpFileIndex;
                break;
            } else if (value == array[midChpFileIndex][0]) {
                return new PositionInfo(midChpFileIndex, 0);
            } else if (value == array[midChpFileIndex][length[midChpFileIndex] - 1]) {
                return new PositionInfo(midChpFileIndex, length[midChpFileIndex] - 1);
            }
        }

        // 查找段落下标
        int lowIndex = 0;
        int highIndex = length[chapFileIndex] - 1;

        while (lowIndex <= highIndex) {
            int midIndex = (lowIndex + highIndex) >>> 1;
            int midValue = array[chapFileIndex][midIndex];
            if (midValue > value) {
                highIndex = midIndex - 1;
            } else if (midValue < value) {
                lowIndex = midIndex + 1;
            } else {
                return new PositionInfo(chapFileIndex, midIndex);
            }
        }
        return new PositionInfo(chapFileIndex, lowIndex > 0 ? lowIndex - 1 : lowIndex);
    }

    /**
     * 根据文本长度获取指定的段落下标
     *
     * @param length 要计算的长度
     */
    public final PositionInfo findParagraphByTextLength(int length) {
        try {
            PositionInfo index = binarySearch(myTextSizes, myParagraphsNumber, length);
            if (index.mChpFileIndex >= 0 && index.mParagraphIndex >= 0) {
                return index;
            }
        } catch (Exception ex) {
            StringBuffer sb = new StringBuffer();
            for (int i : myParagraphsNumber) {
                sb.append("," + i);
            }
            LogUtils.i(TAG, sb.toString());
        }
        return new PositionInfo(0, 0);// Math.min(-index - 1,
        // getTotalParagraphsNumber() - 1);
    }

    @Override
    public Stack<Integer> getStylePossible() {
        return null;
    }

    @Override
    public int getChapterFileNumber() {
        if (mChpFileNumMapping == null || mChpFileNumMapping.getSize() == 0) {
            return mChpFileNum;
        }
        int val = mChpFileNumMapping.getVal(mChpFileNum);
        return val == -1 ? 0 : val;
    }

    @Override
    public int getChapterFileNumber(int chapFileNum) {
        mChpFileNum = chapFileNum;
        if (mChpFileNumMapping == null || mChpFileNumMapping.getSize() == 0) {
            return chapFileNum;
        }
        int ret = mChpFileNumMapping.getVal(chapFileNum);
        return ret == -1 ? chapFileNum % cacheChapNumber : ret;
    }

    @Override
    public GBIntMap getChpFileNumMapping() {
        return mChpFileNumMapping;
    }

    @Override
    public int getChapterSize() {
        return mChapterSize;
    }

    @Override
    public void setChapterSize(int chapterSize) {
        mChapterSize = chapterSize;

    }

    @Override
    public int getTotalParagraphsNumber() {
        return myTotalParagrapsNumber;
    }

    @Override
    public PositionInfo preParagraph(PositionInfo info) {
        if (info.mParagraphIndex > 0) {
            return new PositionInfo(info.mChpFileIndex, info.mParagraphIndex - 1);
        } else if (info.mParagraphIndex <= 0) {
            if (info.mChpFileIndex > 0) {
                return new PositionInfo(info.mChpFileIndex - 1, getParagraphsNumber(info.mChpFileIndex - 1));
            }
        }
        return new PositionInfo(0, 0);
    }

    @Override
    public PositionInfo nextParagraph(PositionInfo info) {
        if (info.mParagraphIndex < getParagraphsNumber(info.mChpFileIndex) - 1) {
            return new PositionInfo(info.mChpFileIndex, info.mParagraphIndex + 1);
        } else if (info.mParagraphIndex >= getParagraphsNumber(info.mChpFileIndex) - 1) {
            if (info.mChpFileIndex < getChapterFileNumber(mChpFileNum - 1)) {
                return new PositionInfo(info.mChpFileIndex + 1, 0);
            }
        }

        return new PositionInfo(getChapterFileNumber(mChpFileNum - 1),
                getParagraphsNumber(getChapterFileNumber(mChpFileNum - 1)));
    }

    @Override
    public GBTextParagraph getParagraph(int chpFileIndex, int index) {

        if (getChapterFileNumber(chpFileIndex) < 0 || index < 0) {
            return null;
        }
        final byte kind = myParagraphKinds[getChapterFileNumber(chpFileIndex)][index];
        switch (kind) {
            case GBTextParagraph.Kind.TEXT_PARAGRAPH :
                return new GBTextParagraphImpl(this, chpFileIndex, index);
            case GBTextParagraph.Kind.TREE_PARAGRAPH :
                return new GBTextTrParagraphImpl(kind, this, chpFileIndex, index);
            default :
                return new GBTextSpecialParagraphImpl(kind, this, chpFileIndex, index);
        }
        // return (kind == GBTextParagraph.Kind.TEXT_PARAGRAPH)
        // ? new GBTextParagraphImpl(this, chpFileIndex, index)
        // : new GBTextSpecialParagraphImpl(kind, this, chpFileIndex, index);

    }

    public void init(int[][] entryIndices, int[][] entryOffsets, int[][] paragraphLenghts, int[][] textSizes,
                     byte[][] paragraphKinds, CharStorage storage, int[][][] styleParagraph, int[] paragraphNumber) {
        myStartEntryIndices = entryIndices;
        myStartEntryOffsets = entryOffsets;
        myParagraphLengths = paragraphLenghts;
        myTextSizes = textSizes;
        myParagraphKinds = paragraphKinds;
        myStorage = storage;
        myStyleParagraphs = styleParagraph;
        myParagraphsNumber = paragraphNumber;
    }

    @Override
    public GBTextParagraph getParagraph(int index) {
        final byte kind = myParagraphKinds[getChapterFileNumber(mChpFileNum)][index];
        switch (kind) {
            case GBTextParagraph.Kind.TEXT_PARAGRAPH :
                return new GBTextParagraphImpl(this, getChapterFileNumber(), index);
            case GBTextParagraph.Kind.TREE_PARAGRAPH :
                return new GBTextTrParagraphImpl(kind, this, getChapterFileNumber(), index);
            default :
                return new GBTextSpecialParagraphImpl(kind, this, getChapterFileNumber(), index);
        }
        // return (kind == GBTextParagraph.Kind.TEXT_PARAGRAPH) ? new
        // GBTextParagraphImpl(this, getChapterFileNumber(),
        // index) : new GBTextSpecialParagraphImpl(kind, this,
        // getChapterFileNumber(), index);
    }

    @Override
    public int resetChpSize(int newChpSize, boolean isFromCache) {
        return 0;
    }

    @Override
    public boolean isCacheExists(boolean isTemp) {
        return myStorage.isCacheExists(isTemp);
    }

    final String TEMP_PREFIX = "temp_cache_file_";

    public boolean isCacheItemExists(int chpFileIndex) {
        return myStorage.isCacheItemExists(chpFileIndex);
    };

    @Override
    public boolean buildLinkCache(boolean isTemp) {
        String prefix = isTemp ? TEMP_PREFIX : "";

        if (isTemp) {
            myStorage.setLinkCache(TEMP_PREFIX + "myParagraphsNumber_new", myParagraphsNumber);
            myStorage.setLinkCache(TEMP_PREFIX + "TextSizes_new", myTextSizes);
        } else {
            myStorage.setLinkCache("myParagraphsNumber_new", myParagraphsNumber);
            myStorage.setLinkCache("TextSizes_new", myTextSizes);

            myStorage.delCache(TEMP_PREFIX + "myParagraphsNumber_new");
            myStorage.delCache(TEMP_PREFIX + "TextSizes_new");
        }
        return true;
    }

    @Override
    public boolean getLinkCache(boolean isTemp) {
        String prefix = isTemp ? TEMP_PREFIX : "";
        int[] tempParagrap = (int[]) myStorage.getLinkCache(prefix + "myParagraphsNumber_new".intern());
        if (null != tempParagrap) {
            if (!isTemp && myParagraphsNumber.length != tempParagrap.length)
                return false;
            myParagraphsNumber = tempParagrap;
        }

        int[][] tempTextSizes = (int[][]) myStorage.getLinkCache(prefix + "TextSizes_new".intern());
        if (null != tempTextSizes) {
            if (!isTemp && myTextSizes.length != tempTextSizes.length)
                return false;
            myTextSizes = tempTextSizes;
        }

        // temp
        // myParagraphLengths=(int[][])myStorage.getLinkCache("myParagraphLengths");
        // myStartEntryIndices=(int[][])myStorage.getLinkCache("myStartEntryIndices");
        // myStartEntryOffsets=(int[][])myStorage.getLinkCache("myStartEntryOffsets");
        // myParagraphKinds = (byte[][])
        // myStorage.getLinkCache("myParagraphKinds"
        // .intern());
        return null == tempTextSizes || null == tempParagrap;
    }

    @Override
    public void clearCache(int chpFileIndex, boolean isMapping) {

        if (chpFileIndex >= myParagraphsNumber.length) {
            return;
        }
        myParagraphsNumber[chpFileIndex] = 0;
        myStorage.clearMemoryByChpIndex(chpFileIndex);

        int readChpPosition = cacheChapNumber;
        if (isMapping) {
            readChpPosition = chpFileIndex % cacheChapNumber;
        }

        int size = myStartEntryOffsets[readChpPosition].length;

        // if (chpFileIndex != 3) {
        LogUtils.e(TAG, "clearCache chpFileIndex" + chpFileIndex);
        for (int i = 0; i < size; i++) {
            myStartEntryOffsets[readChpPosition][i] = 0;
            myParagraphLengths[readChpPosition][i] = 0;
            myParagraphKinds[readChpPosition][i] = 0;
        }
        /*
         * for(int i=0;i<myTextSizes[chpFileIndex].length;i++){
         * myTextSizes[chpFileIndex][i] = 0; }
         */
        // 因为每次extend时只能extend到chpFileIndex%3 故其他的一直都是初始长度 没有参与过extend
        // 与其他周面数组长度不符 重新开辟空间
        myTextSizes[chpFileIndex] = new int[size];
        /*
         * } else { myStartEntryOffsets[readChpPosition] = new int[size];
         * myParagraphLengths[readChpPosition] = new int[size];
         * myTextSizes[chpFileIndex] = new int[size];
         * myParagraphKinds[readChpPosition] = new byte[size]; }
         */

        System.gc();
        System.gc();

    }

    @Override
    public void buildLastDataCache(int chpFileIndex) {

    }

    // 图书是否加载完成
    boolean mIsLoadOver = false;

    @Override
    public void loadBookOver() {
        LogUtils.e(TAG, "start  in  load book over  ");
        myTotalParagrapsNumber = 0;
        for (int i : myParagraphsNumber) {
            myTotalParagrapsNumber += i;
        }
        // 段落尺寸累计处理 （wait for restruction）
        if (!isCacheExists(false)) {
            for (int i = 1; i < myTextSizes.length; i++) {
                for (int j = 0; j < myParagraphsNumber[i]; j++) {
                    int pn = myParagraphsNumber[i - 1];
                    if (pn <= 0)
                        break;
                    myTextSizes[i][j] += myTextSizes[i - 1][pn - 1];

                }
            }
        }
        mIsLoadOver = true;
    }

    @Override
    public boolean isLoadBookOver() {
        return mIsLoadOver;
    }

    @Override
    public int nextChp() {
        if (!mIsLoadOver)
            myTotalParagrapsNumber += myParagraphsNumber[mChpFileNum];
        return 0;
    }

    @Override
    public void initCacheDir() {
        myStorage.initDir();

    }

    @Override
    public void extendTextSizes(int newChpSize, boolean isFromCache) {
        // TODO Auto-generated method stub

    }

    int mChpSelected = -1;

    @Override
    public int getChpSelected() {
        return mChpSelected;
    }

    @Override
    public void setChpSelected(int chpFileIndex) {
        mChpSelected = chpFileIndex;

    }

    @Override
    public void addCacheItem(String key, Object item) {
        myStorage.setLinkCache(key, item);

    }

    @Override
    public Object getCacheItem(String key) {

        return myStorage.getLinkCache(key);
    }

    byte bookNameDisplayModeVal = 0;

    @Override
    public byte getBookNameDisplayMode() {
        return bookNameDisplayModeVal;
    }

    @Override
    public void setBookNameDisplayMode(byte val) {
        bookNameDisplayModeVal = val;
    }

    byte chpNameDisplayModeVal = 0;

    @Override
    public void setChpNameDisplayMode(byte val) {
        chpNameDisplayModeVal = val;

    }

    @Override
    public byte getChpNameDisplayMode() {

        return chpNameDisplayModeVal;
    }

    @Override
    public boolean delCacheItemAll() {

        return myStorage.delCacheItemAll();
    }

    @Override
    public boolean delLinkCache() {
        return myStorage.delLinkCache();
    }

    @Override
    public void finalize() {
        myStartEntryIndices = null;
        myStartEntryOffsets = null;
        myParagraphLengths = null;
        myTextSizes = null;
        myParagraphKinds = null;
        myParagraphsNumber = null;
        myStyleParagraphs = null;
        if (myStorage != null)
            myStorage.finalize();
        if (myImageMap != null)
            myImageMap.clear();
        if (myMarks != null)
            myMarks.clear(); // 书签集合

    }

    // 添加版权标示
    private boolean mIsHaveCopyRight = false;
    @Override
    public void setIsHaveCopyRight(boolean isHave) {
        mIsHaveCopyRight = isHave;
    }
    @Override
    public boolean isHaveCopyRight() {
        return mIsHaveCopyRight;
    }

}
