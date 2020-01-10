package com.tin.projectlist.app.library.reader.model.parser.css;

import com.core.text.model.style.GBTextFontStyleEntry;
import com.core.text.model.style.GBTextStyleEntry;

/**
 *
 * 类名： .java<br>
 * 描述：字体样式处理 创建者： yangn<br>
 * 创建日期：2013-5-17<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class AttrFontAction extends AttrAction {

    final String TAG = "AttrFontAction";

    private GBTextFontStyleEntry fontStyle = null;

    public static final String COLOR = "color", FONT_SIZE = "font-size",FAMILY="font-family",LINE_HEIGHT = "line-height",
            FONT_WEIGHT="font-weight";

    @Override
    protected GBTextStyleEntry create(String attrName, String attrVal) {
        fontStyle = new GBTextFontStyleEntry();
        this.doIt(attrName, attrVal, fontStyle);
        return fontStyle;
    }

    /*
     * @Override protected GBTextStyleEntry getStyleEntry() { return fontStyle;
     * }
     */

    @Override
    protected void doIt(String attrName, String attrVal, GBTextStyleEntry entry) {
        fontStyle = (GBTextFontStyleEntry) entry;
        if (null == attrVal || "".equals(attrVal)) {
            return;
        }
        // L .e(TAG, attrName+"==="+attrVal);

        if (attrName.equalsIgnoreCase(COLOR)) {
            fontStyle.setColor(attrVal);
        } else if (attrName.trim().equalsIgnoreCase(FONT_SIZE)) {
            fontStyle.setFontSize(attrVal);
        }else if(attrName.trim().equalsIgnoreCase(FAMILY)){
            fontStyle.setFamily(attrVal);
        }else if(attrName.trim().equalsIgnoreCase(LINE_HEIGHT)){
            this.fontStyle.setLineHeight(attrVal);
        }else if(attrName.trim().equalsIgnoreCase(FONT_WEIGHT)){
            this.fontStyle.setFontWeight(attrVal);
        }
    }

    @Override
    protected Class<?> getEntryType() {
        return GBTextFontStyleEntry.class;
    }


}
