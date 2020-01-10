package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.core.xml.GBStringMap;
import com.geeboo.read.model.bookmodel.BookReader;
import com.geeboo.read.model.bookmodel.GBTextKind;

/**
 * 类名： XHTMLTagPreAction.java<br>
 * 描述： pre标签处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagPreAction extends XHTMLTagAction {
	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
		reader.myPreformatted = true;
		final BookReader modelReader = reader.getModelReader();
		modelReader.beginParagraph();
		modelReader.addControl(GBTextKind.CODE, true);
	}

	protected void doAtEnd(XHTMLReader reader) {
		final BookReader modelReader = reader.getModelReader();
		modelReader.addControl(GBTextKind.CODE, false);
		modelReader.endParagraph();
		reader.myPreformatted = false;
	}
}
