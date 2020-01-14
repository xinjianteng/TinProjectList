package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.parser.text.model.GBNoteEntry;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * html5 笔记
 *
 * @author android
 *
 */
public class XHTMLTagNoteAction extends XHTMLTagAction {
	private final byte myControl;

	public XHTMLTagNoteAction(byte control) {
		myControl = control;
	}

	@Override
	protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {

		final String value = xmlattributes.getValue("value");

		final BookReader modelReader = reader.getModelReader();

		modelReader.pushKind(myControl);

		GBNoteEntry mediaEntry = new GBNoteEntry(value);
		//将控制标签写入缓存
		modelReader.addControl(myControl, true);
		modelReader.addHtml5NoteControl(mediaEntry.toChars());

	}

	@Override
	protected void doAtEnd(XHTMLReader reader) {
		final BookReader modelReader = reader.getModelReader();
//		写入结束控制标到缓存
		modelReader.addControl(myControl, false);
//		从栈顶移除标示
		modelReader.popKind();
	}

}
