package com.tin.projectlist.app.library.reader.model.parser.css;

import com.core.text.model.style.GBTextStyleEntry;
import com.core.text.model.style.GBTextWordStyleEntry;

/**
 *
 * 类名： .java<br>
 * 描述： 文本样式处理
 * 创建者： yangn<br>
 * 创建日期：2013-6-8<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class AttrWordAction extends AttrAction {

    public static final String TEXT_ALIGN = "text-align", TEXT_INDENT = "text-indent";

    private GBTextWordStyleEntry wordStyle = null;

    @Override
    protected GBTextStyleEntry create(String attrName, String attrVal) {
        wordStyle = new GBTextWordStyleEntry();
        this.doIt(attrName, attrVal, wordStyle);
        return wordStyle;
    }

    @Override
    protected void doIt(String attrName, String attrVal, GBTextStyleEntry entry) {
        if (null == attrName || "".equals(attrVal)) {
            return;
        } else {
            this.wordStyle = (GBTextWordStyleEntry) entry;
            if (attrName.equalsIgnoreCase(TEXT_ALIGN)) {
                this.wordStyle.setTextAlign(attrVal);
            }else if(attrName.equalsIgnoreCase(TEXT_INDENT)){
                this.wordStyle.setTextIndent(attrVal);
            }
        }

    }

    @Override
    protected Class<?> getEntryType() {
        return GBTextWordStyleEntry.class;
    }



}
