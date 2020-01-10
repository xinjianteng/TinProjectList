package com.tin.projectlist.app.library.reader.model.parser.css;

import com.core.text.model.style.GBTextBackgroundStyleEntry;
import com.core.text.model.style.GBTextStyleEntry;

/**
 *
 * 类名： .java<br>
 * 描述：背景样式处理
 * 创建者： yangn<br>
 * 创建日期：2013-6-8<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class AttrBackgroundAction extends AttrAction {

    private GBTextBackgroundStyleEntry backStyle=null;

    public static final String BACKGROUND_COLOR="background-color",BACKGROUND_IMG="background-image";

    @Override
    protected GBTextStyleEntry create(String attrName, String attrVal) {
        backStyle=new GBTextBackgroundStyleEntry();
        this.doIt(attrName, attrVal, backStyle);
        return this.backStyle;
    }

    @Override
    protected void doIt(String attrName, String attrVal, GBTextStyleEntry entry) {
        this.backStyle=(GBTextBackgroundStyleEntry) entry;
        if (null == attrVal || "".equals(attrVal)) {
            return ;
        }
        //L.e("background", "name="+attrName+"val="+attrVal);
        if(BACKGROUND_COLOR.equalsIgnoreCase(attrName.trim())){
            this.backStyle.setBackgroundColor(attrVal.trim());

        }else if(BACKGROUND_IMG.equalsIgnoreCase(attrName.trim())){
            this.backStyle.setBackgroundImg(attrVal.trim());
        }

    }

    @Override
    protected Class<?> getEntryType() {
        return GBTextBackgroundStyleEntry.class;
    }



}
