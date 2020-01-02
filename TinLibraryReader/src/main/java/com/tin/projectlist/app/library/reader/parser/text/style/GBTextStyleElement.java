package com.core.text.style;

import com.core.text.model.style.GBTextStyleEntry;
import com.core.text.widget.GBTextElement;

/**
 * 类名： GBTextStyleElement#ZLTextStyleElement.<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-22<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextStyleElement extends GBTextElement {
	public final GBTextStyleEntry Entry;  //样式实体

	GBTextStyleElement(GBTextStyleEntry entry) {
		Entry = entry;
	}
}
