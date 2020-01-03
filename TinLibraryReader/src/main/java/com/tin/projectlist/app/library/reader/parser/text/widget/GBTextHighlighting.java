package com.tin.projectlist.app.library.reader.parser.text.widget;
/**
 * 高亮文本 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-19<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextHighlighting implements GBTextAbstractHighlighting {
    protected GBTextPosition myStartPosition;
    protected GBTextPosition myEndPosition;
    protected int mId = -1;
    public int getmId() {
        return mId;
    }
    protected String mText = ""; // 高亮的文字
    public String getmText() {
        return mText;
    }

    private LIGHTMODEL mLightModel; // 高亮模式
    public void setmLightModel(LIGHTMODEL mLightModel) {
        this.mLightModel = mLightModel;
    }
    public LIGHTMODEL getLightModel() {
        return mLightModel;
    }

    public GBTextHighlighting() {

    }
    public GBTextHighlighting(int id, GBTextFixedPosition start, GBTextFixedPosition end, String text) {
        mId = id;
        mText = text;
        myStartPosition = start;
        myEndPosition = end;
    }
    /**
     * 设置高亮文本开始结束位置 功能描述： <br>
     * 创建者： yangn<br>
     * 创建日期：2013-4-23<br>
     *
     * @param
     */
    public void setup(GBTextPosition start, GBTextPosition end) {
        myStartPosition = new GBTextFixedPosition(start);
        myEndPosition = new GBTextFixedPosition(end);
    }

    public void setup(int id, GBTextPosition start, GBTextPosition end, String text) {
        mId = id;
        mText = text;
        setup(start, end);
    }

    public boolean clear() {
        if (isEmpty()) {
            return false;
        }
        myStartPosition = null;
        myEndPosition = null;
        return true;
    }

    public boolean isEmpty() {
        return myStartPosition == null;
    }

    public GBTextPosition getStartPosition() {
        return myStartPosition;
    }

    public GBTextPosition getEndPosition() {
        return myEndPosition;
    }

    @Override
    public GBTextElementArea getStartArea(GBTextPage page) {
        return page.TextElementMap.getFirstAfter(myStartPosition, true, -1);
    }

    @Override
    public GBTextElementArea getEndArea(GBTextPage page) {
        return page.TextElementMap.getLastBefore(myEndPosition, true, page.TextElementMap.size());
    }
    /**
     * 获取当前页的开始元素
     */
    public GBTextElementArea getStartArea(GBTextPage page, boolean isLeft) {
        return page.TextElementMap.getFirstAfter(myStartPosition, isLeft, page.tempElementIndex);
    }
    /**
     * 获取页面的结束元素
     */
    public GBTextElementArea getEndArea(GBTextPage page, boolean isLeft) {
        return page.TextElementMap.getLastBefore(myEndPosition, isLeft, page.tempElementIndex);
    }
    /**
     * 功能描述：判断触点是否在区域内 <br>
     * 创建者： jack<br>
     * 创建日期：2013-8-15<br>
     *
     * @param page
     * @param x
     * @param y
     * @return
     */
    public boolean isContainPoint(GBTextPage page, int x, int y, int screenW) {
        GBTextElementArea start = getStartArea(page, x < screenW);
        GBTextElementArea end = getEndArea(page, x < screenW);
        if (x > screenW)
            x -= screenW;
        if (start == null && end == null)
            return false;
        if (start == null)
            return (((y >= 0) && (y <= end.YEnd) && (x >= 0) && (x <= end.XEnd)) || ((y >= 0) && (y <= end.YStart)
                    && (x > end.XStart) && (x <= page.OldWidth)));
        else if (end == null)
            return (((y >= start.YStart) && (y <= page.OldHeight) && (x >= start.XStart) && (x <= page.OldWidth)) || ((y >= start.YEnd)
                    && (y <= page.OldHeight) && (x >= 0) && (x < start.XStart)));
        else
            return (((y >= start.YStart) && (y <= end.YEnd) && (x >= start.XStart) && (x <= end.XEnd))
                    || ((y >= start.YEnd) && (y <= end.YEnd) && (x > 0) && (x <= start.XStart)) || ((y >= start.YStart)
                    && (y <= end.YStart) && (x >= end.XEnd) && (x < page.OldWidth)));
    }

    /*
     * 高亮模式
     */
    public enum LIGHTMODEL {
        LEFT, // 仅左页
        RIGHT, // 仅右页
        BRIDGE // 跨越两页
    }

}
