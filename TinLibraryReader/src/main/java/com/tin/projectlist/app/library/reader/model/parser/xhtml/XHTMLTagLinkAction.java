package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.core.xml.GBStringMap;

public class XHTMLTagLinkAction extends XHTMLTagAction {

    @Override
    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        String attributeVal = xmlattributes.getValue("href");
        // 目前之解析css文件
        if (attributeVal != null && !attributeVal.toLowerCase().endsWith(".css"))
            return;

        if (attributeVal != null && attributeVal.contains("../")) {
            attributeVal = attributeVal.replace("../".intern(), "");
        }

        if (attributeVal != null && attributeVal.contains("./")) {
            attributeVal = attributeVal.replace("./".intern(), "");
        }

        String href = reader.myFilePrefix + attributeVal;

        reader.styleReader.addStyleExternal(reader.myReferencePrefix, href, xmlattributes.getValue("rel"),
                xmlattributes.getValue("type"));

    }

    @Override
    protected void doAtEnd(XHTMLReader reader) {
        // TODO Auto-generated method stub

    }

}
