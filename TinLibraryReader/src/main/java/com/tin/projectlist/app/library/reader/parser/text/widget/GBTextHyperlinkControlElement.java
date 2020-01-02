package com.core.text.widget;

import com.core.text.model.GBTextHyperlink;

/**
 * 界面元素  超链接
 * original:ZLTextHyperlinkControlElement
 * 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-11<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextHyperlinkControlElement extends GBTextControlElement {
	public final GBTextHyperlink Hyperlink;

	public GBTextHyperlinkControlElement(byte kind, byte type, String id) {
		super(kind, true);
		Hyperlink = new GBTextHyperlink(type, id);
	}
}
