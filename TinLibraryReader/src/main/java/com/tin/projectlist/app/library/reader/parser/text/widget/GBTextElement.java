package com.tin.projectlist.app.library.reader.parser.text.widget;
/**
 * 界面元素  文本
 * original:ZLTextElement
 * 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-11<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextElement {
	public final static GBTextElement HSpace = new GBTextElement();
	public final static GBTextElement AfterParagraph = new GBTextElement();
	public final static GBTextElement Indent = new GBTextElement();
	public final static GBTextElement StyleClose = new GBTextElement();

	@Override
	public String toString() {
		if(this == HSpace)
			return "HSpace";
		if(this == AfterParagraph)
			return "AfterParagraph";
		if(this == Indent)
			return "Indent";
		if(this == StyleClose)
			return "StyleClose";
		return super.toString();
	}
}
