package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagBodyAction.java<br>
 * 描述： body标签业务处理<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagBodyAction extends XHTMLTagAction {
	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
		reader.myInsideBody = true;
	}

	protected void doAtEnd(XHTMLReader reader) {
		reader.getModelReader().endParagraph();
		reader.myInsideBody = false;
	}
}
