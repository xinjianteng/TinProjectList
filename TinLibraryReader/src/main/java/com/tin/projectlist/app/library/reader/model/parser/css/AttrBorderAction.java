package com.tin.projectlist.app.library.reader.model.parser.css;

import com.core.text.model.style.GBTextBorderStyleEntry;
import com.core.text.model.style.GBTextStyleEntry;

/**
 *
 * 类名： .java<br>
 * 描述：边框样式处理 创建者： yangn<br>
 * 创建日期：2013-6-8<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class AttrBorderAction extends AttrAction {

    final String TAG = "AttrBorderAction";

    public static final String BORDER = "border", HTM_BORDER = "htm-border", BORDER_BOTTOM = "border-bottom",
            BORDER_BOTTOM_COLOR = "border-bottom-color", BORDER_BOTTOM_STYLE = "border-bottom-style",
            BORDER_BOTTOM_WIDTH = "border-bottom-width", BORDER_COLOR = "border-color", BORDER_LEFT = "border-left",
            BORDER_LEFT_COLOR = "border-left-color", BORDER_LEFT_STYLE = "border-left-style",
            BORDER_LEFT_WIDTH = "border-left-width", BORDER_RIGHT = "border-right",
            BORDER_RIGHT_COLOR = "border-right-color", BORDER_RIGHT_STYLE = "border-right-style",
            BORDER_RIGHT_WIDTH = "border-right-width", BORDER_STYLE = "border-style", BORDER_TOP = "border-top",
            BORDER_TOP_COLOR = "border-top-color", BORDER_TOP_STYLE = "border-top-style",
            BORDER_TOP_WIDTH = "border-top-width", BORDER_WIDTH = "border-width";

    private GBTextBorderStyleEntry borderStyle = null;

    @Override
    protected GBTextStyleEntry create(String attrName, String attrVal) {
        this.borderStyle = new GBTextBorderStyleEntry();
        this.doIt(attrName, attrVal, borderStyle);
        this.isAlwaysNew = true;
        return this.borderStyle;
    }

    @Override
    protected void doIt(String attrName, String attrVal, GBTextStyleEntry entry) {
        if (null == attrName || "".equals(attrVal)) {
            return;
        } else {
            // L.e(TAG, "name=  "+attrName+"  val=  "+attrVal);
            this.borderStyle = (GBTextBorderStyleEntry) entry;
            if (attrName.trim().equalsIgnoreCase(BORDER)) {
                this.borderStyle.setBorder(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_BOTTOM)) {
                this.borderStyle.setBorderBottom(attrVal);
                // this.borderStyle.setborderb
            } else if (attrName.trim().equalsIgnoreCase(BORDER_BOTTOM_COLOR)) {
                this.borderStyle.setBorderBottomColor(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_BOTTOM_STYLE)) {
                this.borderStyle.setBorderBottomStyle(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_BOTTOM_WIDTH)) {
                this.borderStyle.setBorderBottomWidth(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_COLOR)) {
                this.borderStyle.setBorderColor(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_LEFT)) {
                this.borderStyle.setBorderLeft(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_LEFT_COLOR)) {
                this.borderStyle.setBorderLeftColor(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_LEFT_STYLE)) {
                this.borderStyle.setBorderLeftStyle(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_LEFT_WIDTH)) {
                this.borderStyle.setBorderLeftWidth(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_RIGHT)) {
                this.borderStyle.setBorderRight(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_RIGHT_COLOR)) {
                this.borderStyle.setBorderRightColor(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_RIGHT_STYLE)) {
                this.borderStyle.setBorderRightStyle(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_RIGHT_WIDTH)) {
                this.borderStyle.setBorderRightWidth(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_STYLE)) {
                this.borderStyle.setBorderStyle(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_TOP)) {
                this.borderStyle.setBorderTop(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_TOP_COLOR)) {
                this.borderStyle.setBorderTopColor(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_TOP_STYLE)) {
                this.borderStyle.setBorderTopStyle(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_TOP_WIDTH)) {
                this.borderStyle.setBorderTopWidth(attrVal);
            } else if (attrName.trim().equalsIgnoreCase(BORDER_WIDTH)) {
                this.borderStyle.setBorderWidth(attrVal);
            }else if(attrName.trim().equalsIgnoreCase(HTM_BORDER)){
                try {
                    this.borderStyle.setHtmBorder(attrVal);
                } catch (NumberFormatException e) {

                }

            }
        }

    }

    @Override
    protected Class<?> getEntryType() {
        return GBTextBorderStyleEntry.class;
    }

}
