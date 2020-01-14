package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.parser.common.util.MimeType;
import com.tin.projectlist.app.library.reader.parser.common.util.MiscUtil;
import com.tin.projectlist.app.library.reader.parser.file.GBFile;
import com.tin.projectlist.app.library.reader.parser.file.image.GBFileImage;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagImageAction.java<br>
 * 描述： img标签处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagImageAction extends XHTMLTagAction {
    private final String myNamespace;
    private final String myNameAttribute;

    XHTMLTagImageAction(String namespace, String nameAttribute) {
        myNamespace = namespace;
        myNameAttribute = nameAttribute;
    }

    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        String fileName = reader.getAttributeValue(xmlattributes, myNamespace, myNameAttribute);
        final String height = xmlattributes.getValue("height");
        final String width = xmlattributes.getValue("width");
        if (fileName != null) {
            fileName = MiscUtil.decodeHtmlReference(fileName);
            final GBFile imageFile = GBFile.createFileByPath(reader.myPathPrefix + fileName);
            if (imageFile != null) {
                final BookReader modelReader = reader.getModelReader();
                boolean flag = modelReader.paragraphIsOpen() && !modelReader.paragraphIsNonEmpty();
                if (flag) {
                    modelReader.endParagraph();
                }
                final String imageName = imageFile.getFullName();
                modelReader.addImageReference(imageName, (short) 0, XHTMLReader.mIsCoverFile
                        || fileName.toLowerCase().contains("cover"));
                GBFileImage img = new GBFileImage(MimeType.IMAGE_AUTO, imageFile);
                modelReader.addImage(imageName, img);

                if (flag) {
                    modelReader.beginParagraph();
                }
            }
        }
    }

    protected void doAtEnd(XHTMLReader reader) {
    }
}
