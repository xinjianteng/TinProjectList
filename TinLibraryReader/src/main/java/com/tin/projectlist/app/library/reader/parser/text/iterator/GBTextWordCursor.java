package com.core.text.iterator;

import com.core.domain.GBApplication;
import com.core.text.model.GBTextMark;
import com.core.text.model.GBTextModel;
import com.core.text.widget.GBTextElement;
import com.core.text.widget.GBTextPosition;
import com.core.text.widget.GBTextView;
import com.core.text.widget.GBTextWord;
import com.core.text.widget.RePaintStatus;
import com.core.view.PageEnum.PageIndex;

/**
 * 类名： .java<br>
 * 描述：文字指针 某一段的某个位置 <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-12<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextWordCursor extends GBTextPosition {

    /**
     * 段 指针
     */
    private GBTextParagraphCursor myParagraphCursor;
    // 元素索引
    private int myElementIndex;
    // 字符索引
    private int myCharIndex;

    // private int myModelIndex;

    public GBTextWordCursor() {
    }

    public GBTextWordCursor(GBTextWordCursor cursor) {
        setCursor(cursor);
    }

    /**
     *
     * 功能描述： 指向cursor描述的位置<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-8<br>
     *
     * @param
     */
    public void setCursor(GBTextWordCursor cursor) {
        myParagraphCursor = cursor.myParagraphCursor;
        myElementIndex = cursor.myElementIndex;
        myCharIndex = cursor.myCharIndex;
    }

    public GBTextWordCursor(GBTextParagraphCursor paragraphCursor) {
        setCursor(paragraphCursor);
    }

    /**
     *
     * 功能描述：指向paragraphCursor描述的位置 创建者： yangn<br>
     * 创建日期：2013-4-8<br>
     *
     * @param
     */
    public void setCursor(GBTextParagraphCursor paragraphCursor) {
        myParagraphCursor = paragraphCursor;
        myElementIndex = 0;
        myCharIndex = 0;
    }

    /**
     *
     * 功能描述：判断ParagraphCursor是否为空 空返回true 否则返回false<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-8<br>
     *
     * @param
     */
    public boolean isNull() {
        return myParagraphCursor == null;
    }

    public boolean isStartOfParagraph() {
        return myElementIndex == 0 && myCharIndex == 0;
    }

    public boolean isStartOfText() {
        return !isNull() && isStartOfParagraph() && myParagraphCursor.isFirst();
    }

    /**
     * 是最后一段返回true 否则返回false;
     */
    public boolean isEndOfParagraph() {
        return myParagraphCursor != null && myElementIndex == myParagraphCursor.getParagraphLength();
    }

    public boolean isEndOfText() {
        return isNull() || (isEndOfParagraph() && myParagraphCursor.isLast());
    }

    @Override
    public int getParagraphIndex() {
        return myParagraphCursor != null ? myParagraphCursor.Index : 0;
    }

    @Override
    public int getElementIndex() {
        return myElementIndex;
    }

    @Override
    public int getCharIndex() {
        return myCharIndex;
    }

    public GBTextElement getElement() {
        return myParagraphCursor.getElement(myElementIndex);
    }

    public GBTextParagraphCursor getParagraphCursor() {
        return myParagraphCursor;
    }

    public GBTextMark getMark() {
        if (myParagraphCursor == null) {
            return null;
        }
        final GBTextParagraphCursor paragraph = myParagraphCursor;
        int paragraphLength = paragraph.getParagraphLength();
        int wordIndex = myElementIndex;
        while ((wordIndex < paragraphLength) && (!(paragraph.getElement(wordIndex) instanceof GBTextWord))) {
            wordIndex++;
        }
        if (wordIndex < paragraphLength) {
            return new GBTextMark(paragraph.chpFileIndex, paragraph.Index,
                    ((GBTextWord) paragraph.getElement(wordIndex)).getParagraphOffset(), 0);
        }
        return new GBTextMark(paragraph.chpFileIndex, paragraph.Index + 1, 0, 0);
    }

    public void nextWord() {
        myElementIndex++;
        myCharIndex = 0;
    }

    public void previousWord() {
        myElementIndex--;
        myCharIndex = 0;
    }

    public boolean nextParagraph(GBTextView tv, boolean isNeedRefreash) {
        if (!isNull()) {
            if (!myParagraphCursor.isLast()) {
                // 检查章节是否加载
                if (myParagraphCursor.isEndOfSection()) {
                    final int chapIndex = myParagraphCursor.chpFileIndex + 1;
                    final boolean flag = GBApplication.Instance().isLoadBookChp(chapIndex);
                    if (!flag && tv != null) {
                        // L.i("GBTextWordCursor", "chapter " +
                        // (myParagraphCursor.chpFileIndex + 1) +
                        // " is not load!");
                        if (tv.mRefreashChpIndex == chapIndex)
                            return false;
                        tv.mRefreashChpIndex = chapIndex;
                        if (isNeedRefreash) {
                            tv.mPaintStatus = new RePaintStatus(true, PageIndex.NEXT, tv.myCurrentPage.EndCursor);
                            tv.isPaintLoading = true;
                        }
                        tv.mApplication.openBookByChapFileIndex(chapIndex, tv);
                        return false;
                    } else if (!flag) {
                        return false;
                    } else if (tv != null) {
                        // 预加载下一章
                        tv.mApplication.openBookByChapFileIndex(chapIndex + 1, null);
                    }
                }
                myParagraphCursor = myParagraphCursor.next();
                // L.i(myParagraphCursor.chpFileIndex + "," +
                // myParagraphCursor.Index
                // +",isendsection:"+myParagraphCursor.isEndOfSection()+ ";" +
                // myParagraphCursor.toString());
                moveToParagraphStart();
                return true;
            }
        }
        return false;
    }
    public boolean previousParagraph(GBTextView tv, boolean isNeedRefreash) {
        if (!isNull()) {
            if (!myParagraphCursor.isFirst()) {

                // 检查章节是否加载
                if (myParagraphCursor.Index == 0) {
                    final boolean flag = GBApplication.Instance().isLoadBookChp(myParagraphCursor.chpFileIndex - 1);
                    // L.i("GBTextWordCursor", "chapter " +
                    // (myParagraphCursor.chpFileIndex - 1) + " is load:"+flag);
                    if (!flag && tv != null) {
                        // if (tv.mRefreashChpIndex ==
                        // myParagraphCursor.chpFileIndex - 1)
                        // return false;
                        tv.mRefreashChpIndex = myParagraphCursor.chpFileIndex - 1;
                        if (isNeedRefreash) {
                            tv.mPaintStatus = new RePaintStatus(true, PageIndex.PREVIOUS, tv.myCurrentPage.StartCursor);
                            tv.isPaintLoading = true;
                        }
                        tv.mApplication.openBookByChapFileIndex(myParagraphCursor.chpFileIndex - 1, tv);
                        return false;
                    } else if (!flag) {
                        return false;
                    }
                    // else if (tv != null) {
                    // // 预加载上一章
                    // tv.mApplication.openBookByChapFileIndex(myParagraphCursor.chpFileIndex
                    // - 2, null);
                    // }
                }

                myParagraphCursor = myParagraphCursor.previous();
                // L.i("GBTextWordCursor",
                // "return paragrah:"+myParagraphCursor.chpFileIndex+","+myParagraphCursor.Index+"---"+myParagraphCursor.toString());
                moveToParagraphStart();
                return true;
            }
        }
        return false;
    }

    /**
     * 段 置空 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param
     */
    public void moveToParagraphStart() {
        if (!isNull()) {
            myElementIndex = 0;
            myCharIndex = 0;
        }
    }

    public void moveToParagraphEnd() {
        if (!isNull()) {
            myElementIndex = myParagraphCursor.getParagraphLength();
            myCharIndex = 0;
        }
    }
    /**
     * 功能描述： 移动到指定段落<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-5<br>
     *
     * @param chpFileIndex 章节索引
     * @param paragraphIndex 段落索引
     */
    public void moveToParagraph(int chpFileIndex, int paragraphIndex) {
        if (!isNull() && (chpFileIndex != myParagraphCursor.chpFileIndex || paragraphIndex != myParagraphCursor.Index)) {
            final GBTextModel model = myParagraphCursor.Model;
            paragraphIndex = Math.max(0, Math.min(paragraphIndex, model.getParagraphsNumber(chpFileIndex) - 1));
            myParagraphCursor = GBTextParagraphCursor.cursor(model, chpFileIndex, paragraphIndex);
            moveToParagraphStart();
        }
    }

    public void moveTo(int wordIndex, int charIndex) {
        if (!isNull()) {
            if (wordIndex == 0 && charIndex == 0) {
                myElementIndex = 0;
                myCharIndex = 0;
            } else {
                wordIndex = Math.max(0, wordIndex);
                int size = myParagraphCursor.getParagraphLength();
                if (wordIndex > size) {
                    myElementIndex = size;
                    myCharIndex = 0;
                } else {
                    myElementIndex = wordIndex;
                    setCharIndex(charIndex);
                }
            }
        }
    }

    public void setCharIndex(int charIndex) {
        charIndex = Math.max(0, charIndex);
        myCharIndex = 0;
        if (charIndex > 0) {
            GBTextElement element = myParagraphCursor.getElement(myElementIndex);
            if (element instanceof GBTextWord) {
                if (charIndex <= ((GBTextWord) element).Length) {
                    myCharIndex = charIndex;
                }
            }
        }
    }

    public void reset() {
        myParagraphCursor = null;
        myElementIndex = 0;
        myCharIndex = 0;
    }

    public void rebuild() {
        if (!isNull()) {
            myParagraphCursor.clear();
            myParagraphCursor.fill();
            moveTo(myElementIndex, myCharIndex);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " (" + myParagraphCursor + "," + myElementIndex + "," + myCharIndex + ")";
    }

    @Override
    public int getChpFileIndex() {
        return myParagraphCursor == null ? 0 : myParagraphCursor.chpFileIndex;
    }
}
