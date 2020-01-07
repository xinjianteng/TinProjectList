package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextWordCursor;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;

/**
 * 类名： RePaintStatus.java<br>
 * 描述： 页面重绘状态信息类<br>
 * 创建者： jack<br>
 * 创建日期：2013-11-22<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class RePaintStatus {
    public boolean mIsCurl; // true，翻页 false，跳转
    public PageEnum.PageIndex mPageIndex;
    public GBTextWordCursor mCursor;

    public boolean mIsStart;
    public int mChpFileIndex;
    public int mParagraphIndex;
    public int mWordIndex;
    public int mCharIndex;
    /**
     * 翻页用的构造方法
     *
     * @param isCurl 是否翻页状态 设置为true
     * @param pageIndex 翻页模式
     * @param cursor 字符游标
     */
    public RePaintStatus(boolean isCurl, PageEnum.PageIndex pageIndex, GBTextWordCursor cursor) {
        this(isCurl, pageIndex, 0, 0, 0, 0, true);
        mCursor = cursor;
    }
    /**
     * 跳转用的构造方法
     *
     * @param isCurl 是否翻页 设置为false
     * @param pageIndex 翻页模式
     * @param chpFileIndex 章节索引
     * @param paragraphIndex 段落索引
     * @param wordIndex 字符索引
     * @param charIndex 字节索引
     * @param isStart 是否为开始位置
     */
    public RePaintStatus(boolean isCurl, PageEnum.PageIndex pageIndex, int chpFileIndex, int paragraphIndex, int wordIndex,
                         int charIndex, boolean isStart) {
        mIsCurl = isCurl;
        mPageIndex = pageIndex;
        mChpFileIndex = chpFileIndex;
        mParagraphIndex = paragraphIndex;
        mWordIndex = wordIndex;
        mCharIndex = charIndex;
        mIsStart = isStart;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return mIsCurl + "--" + mPageIndex + "--" + mChpFileIndex + "," + mParagraphIndex;
    }
}
