package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagParagraphAction.java<br>
 * 描述： 段落标签处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagParagraphAction extends XHTMLTagAction {
	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
		reader.getModelReader().beginParagraph();
	}

	protected void doAtEnd(XHTMLReader reader) {
		reader.getModelReader().endParagraph();
	}
}
