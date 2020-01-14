package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.parser.css.AttrFontAction;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

public class XHTMLTagFontAction extends XHTMLTagStyleKindAction {

	@Override
	protected void readStyleKind(XHTMLReader reader, GBStringMap xmlattributes) {
		reader.styleReader.resetCurrentEntrys();
		reader.styleReader.readStyleByAttr(AttrFontAction.FONT_SIZE,
				xmlattributes.getValue("size"));

		reader.styleReader.readStyleByAttr(AttrFontAction.COLOR, xmlattributes.getValue("color"));

	}

}
