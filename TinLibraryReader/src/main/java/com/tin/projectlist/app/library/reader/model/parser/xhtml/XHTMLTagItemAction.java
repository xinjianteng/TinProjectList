package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagItemAction.java<br>
 * 描述： 选项标签处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagItemAction extends XHTMLTagAction {
	private final char[] BULLET = { '\u2022', '\240' };

	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
		final BookReader modelReader = reader.getModelReader();
		modelReader.endParagraph();
		// TODO: increase left indent
		modelReader.beginParagraph();
		// TODO: replace bullet sign by number inside OL tag
		modelReader.addData(BULLET);
	}

	protected void doAtEnd(XHTMLReader reader) {
		reader.getModelReader().endParagraph();
	}
}
