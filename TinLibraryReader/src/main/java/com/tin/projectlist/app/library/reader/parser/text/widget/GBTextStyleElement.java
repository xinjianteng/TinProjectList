package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.core.text.model.style.GBTextStyleEntryProxy;
/**
 * 样式元素 original:ZLTextStyleElement 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-16<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextStyleElement extends GBTextElement {
    public final GBTextStyleEntryProxy Entry;
    public final int myParaIndex;
    public GBTextStyleElement(GBTextStyleEntryProxy entry, int paraIndex) {
        Entry = entry;
        myParaIndex = paraIndex;
    }
}
