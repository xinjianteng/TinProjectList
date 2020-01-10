package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.core.xml.GBStringMap;
import com.geeboo.read.model.bookmodel.BookReader;

/**
 * 类名： XHTMLTagControlAction.java<br>
 * 描述： 控制标签业务处理<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagControlAction extends XHTMLTagAction {
	final byte myControl;

	XHTMLTagControlAction(byte control) {
		myControl = control;
	}

	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
		final BookReader modelReader = reader.getModelReader();
		//往控制标签栈中压入一个标示
		modelReader.pushKind(myControl);
		//将控制标签写入缓存
		modelReader.addControl(myControl, true);
	}

	protected void doAtEnd(XHTMLReader reader) {
		final BookReader modelReader = reader.getModelReader();
		//写入结束控制标到缓存
		modelReader.addControl(myControl, false);
		//从栈顶移除标示
		modelReader.popKind();
	}
}
