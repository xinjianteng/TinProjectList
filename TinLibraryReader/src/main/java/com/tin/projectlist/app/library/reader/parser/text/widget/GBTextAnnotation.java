package com.tin.projectlist.app.library.reader.parser.text.widget;

/**
 * 类名： GBTextAnnotation.java<br>
 * 描述： 图书批注业务<br>
 * 创建者： jack<br>
 * 创建日期：2013-8-15<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextAnnotation extends GBTextHighlighting {
    private String mContent; // 批注内容
    public String getmContent() {
        return mContent == null ? "" : mContent;
    }
    private boolean mIsNotesClick = false;
    public boolean getIsNoteClick() {
        return mIsNotesClick;
    }

    public GBTextAnnotation() {

    }
    /**
     * @param id 数据id
     * @param start 开始位置
     * @param end 结束位置
     * @param content 批注内容
     * @param text 选中内容
     */
    public GBTextAnnotation(int id, GBTextFixedPosition start, GBTextFixedPosition end, String content, String text) {
        super(id, start, end, text);
        mContent = content;
        mText = text;
    }
    /**
     * 功能描述： 设置批注信息<br>
     * 创建者： jack<br>
     * 创建日期：2013-8-15<br>
     *
     * @param start
     * @param end
     * @param content
     */
    public void setup(int id, GBTextPosition start, GBTextPosition end, String content, String text) {
        super.setup(id, start, end, text);
        mContent = content;
    }
    @Override
    public boolean isContainPoint(GBTextPage page, int x, int y, int screenW) {
        mIsNotesClick = false;
        // 检查是否点击批注点
        if (mContent != null && !mContent.equals("")) {
            GBTextElementArea area = getEndArea(page, x < screenW);
            int xi = x;
            if (x > screenW)
                xi -= screenW;
            if (area != null && (y >= area.YEnd - 40) && (y <= area.YEnd + 20) && (xi >= area.XEnd - 10)
                    && (xi <= area.XEnd + 30)) {
                mIsNotesClick = true;
                return true;
            }
        }

        return super.isContainPoint(page, x, y, screenW);
    }
}
