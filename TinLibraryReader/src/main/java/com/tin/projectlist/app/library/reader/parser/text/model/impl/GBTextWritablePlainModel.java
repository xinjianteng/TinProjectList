package com.core.text.model.impl;

import com.core.common.util.ArrayUtils;
import com.core.file.image.GBImage;
import com.core.log.L;
import com.core.text.model.GBTextParagraph;
import com.core.text.model.GBTextWritableModel;
import com.core.text.model.cache.impl.CachedCharStorage;
import com.core.text.model.style.GBTextStyleEntry;
import com.core.text.model.style.GBTextStyleEntryProxy;

import java.util.Map;
import java.util.Stack;

/**
 * 类名： GBTextWritablePlainModel.java<br>
 * 描述： 文本内容缓存写入模型<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-10<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextWritablePlainModel extends GBTextPlainModel implements GBTextWritableModel {
    final String TAG = "ZLTextWritablePlainModel";
    private char[] myCurrentDataBlock;
    private int myBlockOffset;
    private volatile int mWrithChpFileIndex = 0;

    public GBTextWritablePlainModel(String id, String language, int arraySize, int dataBlockSize, String directoryName,
                                    String bookName, String extension, Map<String, GBImage> imageMap, int chpFileSize) {
        super(id, language, new int[chpFileSize][arraySize], new int[chpFileSize][arraySize],
                new int[chpFileSize][arraySize], new int[chpFileSize][arraySize], new byte[chpFileSize][arraySize],
                new CachedCharStorage(dataBlockSize, directoryName, bookName, extension, chpFileSize), imageMap,
                new int[chpFileSize][arraySize][0], new int[chpFileSize]);
    }

    public GBTextWritablePlainModel(String id, String language, Map<String, GBImage> imageMap) {
        super(id, language, imageMap);

    }

    /**
     * 数据容器长度倍增
     */
    private void extend() {
        final int size = myStartEntryIndices[getChapterFileNumber(mWrithChpFileIndex)].length;
        this.extendByNum(mWrithChpFileIndex, size, size << 1, false);
        /*
         * final int size =
         * myStartEntryIndices[getChapterFileNumber(mWrithChpFileIndex)].length;
         * myStartEntryIndices[mChpFileNumber] = ArrayUtils.createCopy(
         * myStartEntryIndices[mChpFileNumber], size, size << 1);
         * myStartEntryOffsets[mChpFileNumber] = ArrayUtils.createCopy(
         * myStartEntryOffsets[mChpFileNumber], size, size << 1);
         * myParagraphLengths[mChpFileNumber] = ArrayUtils.createCopy(
         * myParagraphLengths[mChpFileNumber], size, size << 1);
         * myTextSizes[mChpFileNumber] = ArrayUtils.createCopy(
         * myTextSizes[mChpFileNumber], size, size << 1);
         * myParagraphKinds[mChpFileNumber] = ArrayUtils.createCopy(
         * myParagraphKinds[mChpFileNumber], size, size << 1);
         * myStyleParagraphs[mChpFileNumber] = ArrayUtils.createCopy(
         * myStyleParagraphs[mChpFileNumber], size, size << 1);
         */

    }

    @Override
    public synchronized void extendTextSizes(int newChpSize, boolean isCacheExists) {
        mChapterSize = newChpSize;
        // 加载分析过的信息
        // getLinkCache(!isCacheExists);

        final int size = Math.min(myParagraphsNumber.length, myTextSizes.length);
        if (newChpSize > size) {
            // myStartEntryIndices.length;
            // final int chpContextSize=myStartEntryIndices[0].length;
            myTextSizes = ArrayUtils.createCopy(myTextSizes, size, newChpSize);
            myParagraphsNumber = ArrayUtils.createCopy(myParagraphsNumber, size, newChpSize);

            for (int i = size; i < newChpSize; i++) {
                myTextSizes[i] = new int[myTextSizes[0].length];
            }

        }

        myStorage.resetChpSize(newChpSize);
    }

    private void extendByNum(int realChpFileIndex, int currentSize, int targetSize, boolean isFromCache) {
        L.i("GBTextWritable", currentSize + "----" + targetSize);
        // 获取映射索引
        int chpFileIndex = getChapterFileNumber(realChpFileIndex);

        if (myStartEntryIndices[chpFileIndex].length == 0) {
            myStartEntryIndices[chpFileIndex] = new int[targetSize];
            myStartEntryOffsets[chpFileIndex] = new int[targetSize];
            myParagraphLengths[chpFileIndex] = new int[targetSize];
            if (myStyleParagraphs != null)
                myStyleParagraphs[chpFileIndex] = new int[targetSize][0];

            myParagraphKinds[chpFileIndex] = new byte[targetSize];
            if (!isFromCache) {
                myTextSizes[realChpFileIndex] = new int[targetSize];
            }

        } else {
            myStartEntryIndices[chpFileIndex] = ArrayUtils.createCopy(myStartEntryIndices[chpFileIndex], currentSize,
                    targetSize);
            myStartEntryOffsets[chpFileIndex] = ArrayUtils.createCopy(myStartEntryOffsets[chpFileIndex], currentSize,
                    targetSize);
            myParagraphLengths[chpFileIndex] = ArrayUtils.createCopy(myParagraphLengths[chpFileIndex], currentSize,
                    targetSize);
            myParagraphKinds[chpFileIndex] = ArrayUtils.createCopy(myParagraphKinds[chpFileIndex], currentSize,
                    targetSize);
            if (!isFromCache) {
                myTextSizes[realChpFileIndex] = ArrayUtils.createCopy(myTextSizes[realChpFileIndex], currentSize,
                        targetSize);
            }

            if (myStyleParagraphs != null)
                myStyleParagraphs[chpFileIndex] = ArrayUtils.createCopy(myStyleParagraphs[chpFileIndex], currentSize,
                        targetSize);
            System.gc();
        }

    }

    @Override
    public int resetChpSize(int newChpSize, boolean isFromCache) {
        final int size = myStartEntryIndices.length;
        myStartEntryIndices = ArrayUtils.createCopy(myStartEntryIndices, size, newChpSize);
        myStartEntryOffsets = ArrayUtils.createCopy(myStartEntryOffsets, size, newChpSize);
        myParagraphLengths = ArrayUtils.createCopy(myParagraphLengths, size, newChpSize);
        if (myStyleParagraphs != null)
            myStyleParagraphs = ArrayUtils.createCopy(myStyleParagraphs, size, newChpSize);
        myParagraphKinds = ArrayUtils.createCopy(myParagraphKinds, size, newChpSize);

        boolean flag = false;
        if (flag = (!isFromCache || myTextSizes.length == 1)) {
            myTextSizes = ArrayUtils.createCopy(myTextSizes, size, newChpSize);
            myParagraphsNumber = ArrayUtils.createCopy(myParagraphsNumber, size, newChpSize);
        }

        int i = 1;
        while (i < newChpSize) {

            this.extendByNum(i, newChpSize, myStartEntryIndices[0].length, !flag);

            ++i;
        }
        if (flag) {
            myStorage.resetChpSize(newChpSize);

        } else {
            myStorage.resetChpSize(myTextSizes.length);
        }
        return myStorage.getChpFileSize();
    }

    /**
     * 创建段
     *
     * @param kind 段类型
     */
    public void createParagraph(byte kind) {
        final int index = myParagraphsNumber[mWrithChpFileIndex]++;
        int chpRealIndex = getChapterFileNumber(mWrithChpFileIndex);
        /* try { */

        int[][] startEntryIndices = myStartEntryIndices;
        // L.e(TAG,
        // "startEntryIndices[getChapterFileNumber(mWrithChpFileIndex)].length="
        // +
        // startEntryIndices[getChapterFileNumber(mWrithChpFileIndex)].length);
        if (index >= startEntryIndices[chpRealIndex].length) {
            L.e(TAG, "in extend");
            extend();
            startEntryIndices = myStartEntryIndices;
        }

        if (isSum) {
            // 非第一章 但是第一段情况 获取上一章最后一段文字长度
            if (mWrithChpFileIndex > 0 && index == 0) {

                if (mWrithChpFileIndex < myTextSizes.length) {
                    L.e(TAG, "myTextSizes len=" + myTextSizes.length + "  chpindex=" + mWrithChpFileIndex + " index="
                            + index);
                }
                // try{
                int previousTextSizes = myTextSizes[mWrithChpFileIndex - 1][myParagraphsNumber[mWrithChpFileIndex - 1] - 1];//
                myTextSizes[mWrithChpFileIndex][index] = previousTextSizes;

            } else if (index > 0) {
                // try {
                myTextSizes[mWrithChpFileIndex][index] = myTextSizes[mWrithChpFileIndex][index - 1];

            }

        } else {
            if (index > 0) {
                // try{
                myTextSizes[mWrithChpFileIndex][index] = myTextSizes[mWrithChpFileIndex][index - 1];

            }
        }
        // L.i(TAG, "create paragrah:" + mWrithChpFileIndex + "---" +
        // index+"--:"+myBlockOffset);
        final int dataSize = myStorage.size(mWrithChpFileIndex);
        startEntryIndices[chpRealIndex][index] = (dataSize == 0) ? 0 : (dataSize - 1);
        myStartEntryOffsets[chpRealIndex][index] = myBlockOffset;
        myParagraphLengths[chpRealIndex][index] = 0;
        myParagraphKinds[chpRealIndex][index] = kind;
        settingStyleParagraphs(index);

    }

    /**
     * 返回最小minimumLength长度的char[] 若当前char剩余空间小于minimumLength时 将当前char[]缓存到文件
     * 创建最小能容纳minimumLength长度的新char[]
     *
     * @param minimumLength
     */
    private char[] getDataBlock(int minimumLength) {
        // 新章节开始 获取新章节char[]
        // 不是新章节块
        char[] block = myCurrentDataBlock;
        if (mIsStartNewChp) {
            // 获取新章之前 将当前放入缓存
            if (block != null) {

                if (mWrithChpFileIndex == 0) {
                    myStorage.freezeLastBlock(mWrithChpFileIndex, true);
                } else {
                    myStorage.freezeLastBlock(mWrithChpFileIndex - 1, true);
                }

            }
            // 获取新章块
            myStorage.setChpFileNum(mWrithChpFileIndex);
            block = myStorage.createNewBlock(mWrithChpFileIndex, minimumLength);
            myBlockOffset = 0;
            mIsStartNewChp = false;
        }

        if ((block == null) || (minimumLength > block.length - myBlockOffset)) {
            if (block != null) {
                myStorage.freezeLastBlock(mWrithChpFileIndex, true);
            }
            block = myStorage.createNewBlock(mWrithChpFileIndex, minimumLength);
            myBlockOffset = 0;
        }

        myCurrentDataBlock = block;

        return block;

    }

    /**
     * 添加文本
     */
    public void addText(char[] text) {
        addText(text, 0, text.length);
    }

    /**
     * [Entry Type][textLen][textLen][data]...
     */
    public void addText(char[] text, int offset, int length) {
        // L.d("addText", new String(text, offset, length) + "---" + length);
        char[] block = getDataBlock(3 + length);
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        int blockOffset = myBlockOffset;
        block[blockOffset++] = (char) GBTextParagraph.Entry.TEXT;
        block[blockOffset++] = (char) length;
        block[blockOffset++] = (char) (length >> 16);
        // L.d("addText current==", new
        // String(text,myBlockOffset,blockOffset));
        System.arraycopy(text, offset, block, blockOffset, length);
        myBlockOffset = blockOffset + length;
        myTextSizes[mWrithChpFileIndex][myParagraphsNumber[mWrithChpFileIndex] - 1] += length;
        // 开始一个新章节的情况

    }

    /**
     * 添加图片 [Entry type][vOffset][path len][imgPath][1/0(1:封面0:非封面)]
     */
    public void addImage(String id, short vOffset, boolean isCover) {
        final int len = id.length();
        final char[] block = getDataBlock(4 + len);
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        int blockOffset = myBlockOffset;
        block[blockOffset++] = (char) GBTextParagraph.Entry.IMAGE;
        block[blockOffset++] = (char) vOffset;
        block[blockOffset++] = (char) len;
        id.getChars(0, len, block, blockOffset);
        blockOffset += len;
        block[blockOffset++] = (char) (isCover ? 1 : 0);
        myBlockOffset = blockOffset;
    }

    /**
     * 添加控件标识
     *
     * @param isStart 是否控件开始 true 控件开始 false 控件结束 [标示控件符][控件类型符]
     */
    public void addControl(byte textKind, boolean isStart) {
        // try {
        final char[] block = getDataBlock(2);

        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        block[myBlockOffset++] = (char) GBTextParagraph.Entry.CONTROL;
        short kind = textKind;
        if (isStart) {
            kind += 0x0100;
        }
        block[myBlockOffset++] = (char) kind;

    }

    /**
     * 添加超链接控件 [标示超链接控件符][超链接类型][链接地址长度][链接地址]
     */
    public void addHyperlinkControl(byte textKind, byte hyperlinkType, String label) {
        final short labelLength = (short) label.length();
        final char[] block = getDataBlock(3 + labelLength);
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        int blockOffset = myBlockOffset;
        block[blockOffset++] = (char) GBTextParagraph.Entry.HYPERLINK_CONTROL;
        block[blockOffset++] = (char) ((hyperlinkType << 8) + textKind);
        block[blockOffset++] = (char) labelLength;
        label.getChars(0, labelLength, block, blockOffset);
        myBlockOffset = blockOffset + labelLength;
    }

    @Override
    public void addHtml5Control(byte textKind, char[] label) {
        final short labelLength = (short) label.length;
        final char[] block = getDataBlock(2 + labelLength);
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        int blockOffset = myBlockOffset;

        block[blockOffset++] = (char) textKind;
        block[blockOffset++] = (char) labelLength;
        System.arraycopy(label, 0, block, blockOffset, labelLength);
        myBlockOffset = blockOffset + labelLength;
    }

    @Override
    public void addHtml5AudioControl(char[] label) {
        addHtml5Control(GBTextParagraph.Entry.HTML5_AUDIO_CONTROL, label);

    }

    @Override
    public void addHtml5VideoControl(char[] label) {
        addHtml5Control(GBTextParagraph.Entry.HTML5_VIDEO_CONTROL, label);

    }

    @Override
    public void addHtml5NoteControl(char[] label){
        addHtml5Control(GBTextParagraph.Entry.HTML5_NOTE_CONTROL, label);
    }

    public void addFixedHSpace(short length) {
        final char[] block = getDataBlock(2);
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        block[myBlockOffset++] = (char) GBTextParagraph.Entry.FIXED_HSPACE;
        block[myBlockOffset++] = (char) length;
    }

    public void addBidiReset() {
        final char[] block = getDataBlock(1);
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        block[myBlockOffset++] = (char) GBTextParagraph.Entry.RESET_BIDI;
    }

    public void stopReading() {
        /*
         * if (myCurrentDataBlock != null) { myStorage.freezeLastBlock();
         * myCurrentDataBlock = null; } final int size = myParagraphsNumber;
         * myStartEntryIndices = ZLArrayUtils.createCopy(myStartEntryIndices,
         * size, size); myStartEntryOffsets =
         * ZLArrayUtils.createCopy(myStartEntryOffsets, size, size);
         * myParagraphLengths = ZLArrayUtils.createCopy(myParagraphLengths,
         * size, size); myParagraphKinds =
         * ZLArrayUtils.createCopy(myParagraphKinds, size, size);
         */
    }

    public final void addStyleColse() {// /L.e(TAG,"exec addStyleColse");
        final char[] block = getDataBlock(2);
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][myParagraphsNumber[mWrithChpFileIndex] - 1];
        // myBlockOffset++;
        block[myBlockOffset++] = (char) GBTextParagraph.Entry.STYLE_CLOSE;

    }

    /**
     * 添加样式
     */
    public void addStyleEntry(GBTextStyleEntry entry) {

        char[] data = entry.toChars();

        final short len = (short) data.length;
        final char[] block = getDataBlock(2 + len);
        int blockOffset = myBlockOffset;
        block[blockOffset++] = GBTextParagraph.Entry.STYLE_OTHER;
        block[blockOffset++] = (char) len;
        // block[blockOffset++]=(char)(len>>16);
        System.arraycopy(data, 0, block, blockOffset, len);
        // ((ZLTextOtherStyleEntry)entry).toString().getChars(0,len,
        // block,blockOffset);
        myBlockOffset = blockOffset + len;

    }

    @Override
    public int addStyleEntry(GBTextStyleEntryProxy entryProxy) {

        char[] data = entryProxy.toChars();
        if (null == data) {
            return -1;
        }
        final short len = (short) data.length;
        final char[] block = getDataBlock(2 + len);
        final int index = myParagraphsNumber[mWrithChpFileIndex] - 1;
        ++myParagraphLengths[getChapterFileNumber(mWrithChpFileIndex)][index];
        int blockOffset = myBlockOffset;
        block[blockOffset++] = GBTextParagraph.Entry.STYLE_OTHER;
        block[blockOffset++] = (char) len;
        // block[blockOffset++]=(char)(len>>16);
        System.arraycopy(data, 0, block, blockOffset, len);
        // ((ZLTextOtherStyleEntry)entry).toString().getChars(0,len,
        // block,blockOffset);
        myBlockOffset = blockOffset + len;

        // testStyle();

        return index;

    }

    /**
     * 设置该段关联有效样式段段码
     */
    protected void settingStyleParagraphs(int index) {
        if (myStyleParagraphs == null)
            return;
        myStyleParagraphs[getChapterFileNumber(mWrithChpFileIndex)][index] = new int[getStylePossible().size()];
        for (int i = 0; i < myStyleParagraphs[getChapterFileNumber(mWrithChpFileIndex)][index].length; i++) {
            myStyleParagraphs[getChapterFileNumber(mWrithChpFileIndex)][index][i] = getStylePossible().get(i);
        }
    }

    public int getBlockOffset() {
        return myBlockOffset;
    }

    // private void testStyle() {
    //
    // GBTextWordStyleEntry word = new GBTextWordStyleEntry();
    // word.setTextAlign((byte) 2);
    //
    // GBTextFontStyleEntry font = new GBTextFontStyleEntry();
    // font.setFontSize("1.6em");
    // font.setColor(89757);
    // font.setFamily("fontFamily");
    //
    // GBTextBackgroundStyleEntry back = new GBTextBackgroundStyleEntry();
    // back.setBackgroundColor("#FFFFFF");
    //
    // GBTextBoxStyleEntry box = new GBTextBoxStyleEntry();
    // box.setMarginTop("10em");
    // // en.setMargin("5px"); //en.setPadding("10em","20em");
    //
    // box.setMarginLeft("1px");
    // box.setMarginBottom("33px");
    // box.setMarginRight("10em");
    // box.setMarginTop("10px");
    //
    // box.setPaddingLeft("1.6px");
    // box.setPaddingBottom("3.3px");
    // box.setPaddingRight("2.5em");
    // box.setPaddingTop("0.2");
    // /*
    // * en.setMarginStr("3px 3em 3em 9px ");
    // * en.setPaddingStr("5px 3em 4px 8px");
    // */
    //
    // GBTextBorderStyleEntry border = new GBTextBorderStyleEntry();
    // border.setBorder("12px 1 red");
    // /*
    // * border.setBorderColor("#FFFFFF"); border.setBorderStyle((byte)2);
    // * border.setBorderWidth("22em");
    // */
    // // border.setBorderTopStyle("solid");
    //
    // GBTextStyleEntryProxy proxy = new GBTextStyleEntryProxy(box);
    // proxy.put(font);
    // proxy.put(back);
    // proxy.put(word);
    // proxy.put(border);
    //
    // char[] arr = proxy.toChars();
    // GBTextStyleEntryProxy proxy3;
    // try {
    // proxy3 = new GBTextStyleEntryProxy(arr);
    // GBTextWordStyleEntry wordRet =
    // proxy3.getEntry(GBTextWordStyleEntry.class);
    //
    // GBTextBoxStyleEntry boxRet = proxy3.getEntry(GBTextBoxStyleEntry.class);
    //
    // GBTextBackgroundStyleEntry backRet =
    // proxy3.getEntry(GBTextBackgroundStyleEntry.class);
    //
    // GBTextBorderStyleEntry borderRet =
    // proxy3.getEntry(GBTextBorderStyleEntry.class);
    // // int backColor = backRet.getBackgroundColor();
    //
    // /*
    // * GBTextWordStyleEntry wordret =
    // * proxy2.getEntry(GBTextWordStyleEntry.class); byte align =
    // * wordret.getTextAlign();
    // */
    // System.out.println("ok");
    //
    // System.out.println("ok");
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // } catch (NoSuchMethodException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (InvocationTargetException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (InstantiationException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // }

    private Stack<Integer> mStack = new Stack<Integer>();

    @Override
    public void addStylePossible(Stack<Integer> stylePossible) {
        mStack = stylePossible;

    }

    @Override
    public Stack<Integer> getStylePossible() {
        return this.mStack;
    }

    // 是否开始新的一章
    boolean mIsStartNewChp = false;

    @Override
    public int nextChp() {
        super.nextChp();

        // if (mWrithChpFileIndex == myParagraphsNumber.length - 1) {
        myStorage.freezeLastBlock(mWrithChpFileIndex, false);
        // }
        myBlockOffset = 0;
        mIsStartNewChp = true;
        ++mWrithChpFileIndex;
        // L.i(TAG, "next chp :" + mWrithChpFileIndex);

        /*
         * for (int i = 0; i < mWrithChpFilzeIndex; i++) { int size =
         * myStorage.size(i); for (int j = 0; j < size; j++) { char[] ret =
         * myStorage.block(i, j); System.out.println("ok"); } }
         */

        return getChapterFileNumber(mWrithChpFileIndex);
    }

    @Override
    public void setWrithChpFiliNum(int writhChpFileIndex) {
        mWrithChpFileIndex = writhChpFileIndex;
        // L.i(TAG, "set write chp :" + mWrithChpFileIndex);

    }

    @Override
    public int getWrithChpFileIndex() {

        return mWrithChpFileIndex;
    }

    @Override
    public void buildLastDataCache(int chpFileIndex) {
        if (chpFileIndex == -1) {
            return;
        }
        myStorage.freezeLastBlock(chpFileIndex, true);
        super.buildLastDataCache(chpFileIndex);
    }

    private boolean isSum = false;

    @Override
    public void settingSumSize(boolean isSumSize) {
        this.isSum = isSumSize;

    }

    /**
     *
     * 功能描述： 获取chp包含文本字符长度<br>
     * 创建者： yangn<br>
     * 创建日期：2013-12-20<br>
     *
     * @param
     */
    /*
     * public long[] getChpTextSizeByChpFileIndex(int chp) { long[] arr = new
     * long[2]; arr[0]= myStartEntryOffsets[getChapterFileNumber(chp)][0]; if
     * (isSum) { arr[1]=
     * myStartEntryOffsets[getChapterFileNumber(chp)][getParagraphsNumber
     * (chp)-1] - myStartEntryOffsets[getChapterFileNumber(chp)][0]; }else {
     * arr[
     * 1]=myStartEntryOffsets[getChapterFileNumber(chp)][getParagraphsNumber(
     * chp)-1]; } int aa=myTextSizes[chp][0]; return arr; }
     */
    @Override
    public void finalize() {
        myCurrentDataBlock = null;
        super.finalize();
    }
}
