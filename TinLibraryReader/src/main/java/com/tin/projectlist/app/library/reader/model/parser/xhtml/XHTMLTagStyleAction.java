package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

public class XHTMLTagStyleAction extends XHTMLTagAction {

    final String TAG = "XHTMLTagStyleAction";
    @Override
    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {

        reader.getModelReader().settingMetaMode(BookReader.Meta.STYLE);
    }

    @Override
    protected void doAtEnd(XHTMLReader reader) {
        // L.e(TAG, "content==" + reader.myModelReader.getCutHeadBuffer());
        reader.styleReader.addStyleInternal(reader.myModelReader.getCutHeadBuffer());
        reader.getModelReader().settingMetaMode(BookReader.Meta.EMPTY);
    }

}
