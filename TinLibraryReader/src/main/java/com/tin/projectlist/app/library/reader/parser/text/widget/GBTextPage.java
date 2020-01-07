package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextParagraphCursor;
import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextWordCursor;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextHighlighting.LIGHTMODEL;

import java.util.ArrayList;
import java.util.List;
/**
 * 页 类名： GBTextPage.java<br>
 * 描述： 页内容实体封装<br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-12<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextPage {
    public final GBTextWordCursor StartCursor = new GBTextWordCursor();// 当前页开始文字
    public final GBTextWordCursor EndCursor = new GBTextWordCursor();// 当前页结束文字
    final ArrayList<GBTextLineInfo> LineInfos = new ArrayList<GBTextLineInfo>();// 当前页行集合
    int PaintState = PaintStateEnum.NOTHING_TO_PAINT;// 绘制状态

    final GBTextElementAreaVector TextElementMap = new GBTextElementAreaVector();// 元素坐标信息

    int OldWidth;
    int OldHeight;

    public final GBTextWordCursor tempCursor = new GBTextWordCursor(); // 双翻页中点
    public int tempIndex = -1; // 行列表分页下标
    public int tempElementIndex = -1; // 元素分页下标
    public int tempReginIndex = -1; // 区域元素分页下标

    /**
     * 功能描述： 重置 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param
     */
    void reset() {
        StartCursor.reset();
        tempCursor.reset();
        EndCursor.reset();
        LineInfos.clear();
        PaintState = PaintStateEnum.NOTHING_TO_PAINT;
        mReadLine = null;
    }

    /**
     *
     * 功能描述：将cursor做为开始位置 <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param cursor
     */
    void moveStartCursor(GBTextParagraphCursor cursor) {
        StartCursor.setCursor(cursor);
        EndCursor.reset();
        tempCursor.reset();
        LineInfos.clear();
        PaintState = PaintStateEnum.START_IS_KNOWN;
        mReadLine = null;
    }

    /**
     *
     * 功能描述：将paragraphIndex段中的wordIndex charIndex 描述的位置作为开始位置<br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-12<br>
     *
     * @param chpFileIndex 章节文件
     * @param paragraphIndex 段数
     * @param wordIndex 段元素索引
     * @param charIndex 元素字符索引
     */
    void moveStartCursor(int chpFileIndex, int paragraphIndex, int wordIndex, int charIndex) {
        if (StartCursor.isNull()) {
            StartCursor.setCursor(EndCursor);
        }
        StartCursor.moveToParagraph(chpFileIndex, paragraphIndex);
        StartCursor.moveTo(wordIndex, charIndex);
        EndCursor.reset();
        tempCursor.reset();
        LineInfos.clear();
        PaintState = PaintStateEnum.START_IS_KNOWN;
    }

    void moveEndCursor(int chpFileIndex, int paragraphIndex, int wordIndex, int charIndex) {
        if (EndCursor.isNull()) {
            EndCursor.setCursor(StartCursor);
        }
        EndCursor.moveToParagraph(chpFileIndex, paragraphIndex);
        if ((paragraphIndex > 0) && (wordIndex == 0) && (charIndex == 0)) {
            EndCursor.previousParagraph(null, false);
            EndCursor.moveToParagraphEnd();
        } else {
            EndCursor.moveTo(wordIndex, charIndex);
        }
        StartCursor.reset();
        tempCursor.reset();
        LineInfos.clear();
        PaintState = PaintStateEnum.END_IS_KNOWN;
    }

    boolean isEmptyPage() {
        for (GBTextLineInfo info : LineInfos) {
            if (info.IsVisible) {
                return false;
            }
        }
        return true;
    }
    /**
     * 功能描述：从当前页开始位置查找行开始索引<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-23<br>
     *
     * @param cursor 要封装的索引
     * @param overlappingValue 重叠偏移量
     */
    void findLineFromStart(GBTextWordCursor cursor, int overlappingValue) {
        if (LineInfos.isEmpty() || (overlappingValue == 0)) {
            cursor.reset();
            return;
        }
        GBTextLineInfo info = null;
        for (GBTextLineInfo i : LineInfos) {
            info = i;
            if (info.IsVisible) {
                --overlappingValue;
                if (overlappingValue == 0) {
                    break;
                }
            }
        }
        cursor.setCursor(info.ParagraphCursor);
        cursor.moveTo(info.EndElementIndex, info.EndCharIndex);
    }
    /**
     * 功能描述：从当前页结束位置查找行开始索引<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-23<br>
     *
     * @param cursor 要封装的索引
     * @param overlappingValue 重叠偏移量
     */
    void findLineFromEnd(GBTextWordCursor cursor, int overlappingValue) {
        if (LineInfos.isEmpty() || (overlappingValue == 0)) {
            cursor.reset();
            return;
        }
        final ArrayList<GBTextLineInfo> infos = LineInfos;
        final int size = infos.size();
        GBTextLineInfo info = null;
        for (int i = size - 1; i >= 0; --i) {
            info = infos.get(i);
            if (info.IsVisible) {
                --overlappingValue;
                if (overlappingValue == 0) {
                    break;
                }
            }
        }
        cursor.setCursor(info.ParagraphCursor);
        cursor.moveTo(info.StartElementIndex, info.StartCharIndex);
    }
    /**
     * 功能描述：根据高度百分比获取开始索引<br>
     * 创建者： jack<br>
     * 创建日期：2013-7-23<br>
     *
     * @param cursor 要封装的索引
     * @param areaHeight 屏幕高度
     * @param percent 百分比
     */
    void findPercentFromStart(GBTextWordCursor cursor, int areaHeight, int percent) {
        if (LineInfos.isEmpty()) {
            cursor.reset();
            return;
        }
        int height = areaHeight * percent / 100;
        boolean visibleLineOccured = false;
        GBTextLineInfo info = null;
        for (GBTextLineInfo i : LineInfos) {
            info = i;
            if (info.IsVisible) {
                visibleLineOccured = true;
            }
            height -= info.Height + info.Descent + info.VSpaceAfter;
            if (visibleLineOccured && (height <= 0)) {
                break;
            }
        }
        cursor.setCursor(info.ParagraphCursor);
        cursor.moveTo(info.EndElementIndex, info.EndCharIndex);
    }
    /**
     * 功能描述：获取页面开始内容<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-13<br>
     *
     * @param size 要获取的内容长度
     * @return
     */
    public String getFirstWords(int size) {
        if (LineInfos.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer("");
        loop : for (GBTextLineInfo info : LineInfos) {
            for (int wordIndex = info.RealStartElementIndex; wordIndex < info.EndElementIndex; wordIndex++) {
                final GBTextElement element = info.ParagraphCursor.getElement(wordIndex);
                if (element instanceof GBTextWord) {
                    sb.append(element.toString());
                    size--;
                }
                if (size == 0)
                    break loop;
            }
        }
        return sb.toString();
    }

    protected List<GBTextHighlighting> myHighlightingList = new ArrayList<GBTextHighlighting>();
    protected List<GBTextAnnotation> myAnnotationList = new ArrayList<GBTextAnnotation>();
    /*
     * 判断触点区域是否在高亮，批注和笔记区域
     */
    protected GBTextHighlighting inHighlightingOverlap(int x, int y, int w) {
        if (myHighlightingList.isEmpty())
            return null;
        for (GBTextHighlighting highlighting : myHighlightingList) {
            if (((x < w && highlighting.getLightModel() != LIGHTMODEL.RIGHT) || (x > w && highlighting.getLightModel() != LIGHTMODEL.LEFT))
                    && highlighting.isContainPoint(this, x, y, w))
                return highlighting;
        }
        return null;
    }
    /*
     * 判断触点区域是否在高亮，批注和笔记区域
     */
    protected GBTextAnnotation inAnnotationOverlap(int x, int y, int w) {
        if (myAnnotationList.isEmpty())
            return null;
        for (GBTextAnnotation ann : myAnnotationList) {
            if (((x < w && ann.getLightModel() != LIGHTMODEL.RIGHT) || (x > w && ann.getLightModel() != LIGHTMODEL.LEFT))
                    && ann.isContainPoint(this, x, y, w))
                return ann;
        }
        return null;
    }
    /**
     * 功能描述： 根据id获取选中注释内容<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-19<br>
     *
     * @param id
     * @return
     */
    public GBTextHighlighting getAnnotaitonsById(int id) {
        for (GBTextAnnotation ann : myAnnotationList) {
            if (ann.mId == id)
                return ann;
        }
        for (GBTextHighlighting highlighting : myHighlightingList) {
            if (highlighting.mId == id)
                return highlighting;
        }
        return null;
    }

    protected GBTextLineInfo mReadLine;
    public void clearReadInfo() {
        mReadLine = null;
    }
    /**
     * 功能描述： 设置当前阅读行<br>
     * 创建者： jack<br>
     * 创建日期：2013-9-10<br>
     *
     * @param paraCursor 阅读段落
     * @param pro 段落进度
     * @return 是否切换
     */
    public boolean setCurrentSpeechPara(GBTextParagraphCursor paraCursor, int pro) {
        boolean flag = false;
        GBTextLineInfo mTemp = null;
        for (GBTextLineInfo info : LineInfos) {
            if (info.ParagraphCursor.compareTo(paraCursor) == 0) {
                if (info.StartElementIndex <= pro && info.EndElementIndex >= pro)
                    mTemp = info;
            }
        }
        if (mTemp != mReadLine) {
            // L.i("GBTextPage", "pro:" + pro + "--temp:" +
            // mTemp.StartElementIndex + "--" + mTemp.EndElementIndex
            // + "--" + mTemp.toString());
            mReadLine = mTemp;
            flag = true;
        }
        return flag;
    }
    /**
     * 功能描述： 获取指定元素的区域<br>
     * 创建者： jack<br>
     * 创建日期：2013-11-5<br>
     *
     * @param element
     * @return
     */
    public GBTextElementArea getElementArea(GBTextElement element) {
        for (int i = 0; i < TextElementMap.size(); i++) {
            if (TextElementMap.get(i).Element == element)
                return TextElementMap.get(i);
        }
        return null;
    }

    public int getCurrentChpIndex() {
        try {
            if (LineInfos.size() > 0) {
                final GBTextParagraphCursor cur = LineInfos.get(LineInfos.size() / 2).ParagraphCursor;
                return cur == null ? 0 : cur.chpFileIndex;
            }
            if (StartCursor != null) {

                boolean flag = StartCursor.getParagraphCursor() != null
                        && StartCursor.getParagraphCursor().isEndOfSection()
                        && !StartCursor.getParagraphCursor().isLast();
                return flag ? StartCursor.getChpFileIndex() + 1 : StartCursor.getChpFileIndex();
            }
            if (EndCursor != null) {
                boolean flag = EndCursor.getParagraphCursor() != null && EndCursor.getParagraphCursor().Index == 0
                        && !EndCursor.getParagraphCursor().isFirst();
                return flag ? EndCursor.getChpFileIndex() - 1 : EndCursor.getChpFileIndex();
            }
        } catch (Exception ex) {
        }
        return 0;
    }
}
