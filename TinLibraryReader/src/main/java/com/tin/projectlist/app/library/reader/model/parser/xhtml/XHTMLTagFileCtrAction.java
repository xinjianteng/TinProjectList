package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.core.common.util.MimeType;
import com.core.common.util.MiscUtil;
import com.core.file.GBFile;
import com.core.file.image.GBFileImage;
import com.core.text.model.GBFileCtrEntry;
import com.core.xml.GBStringMap;
import com.geeboo.read.model.bookmodel.BookReader;

public class XHTMLTagFileCtrAction extends XHTMLTagAction {

    @Override
    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        String path = xmlattributes.getValue("path");
        String pathTwo = xmlattributes.getValue("pathtwo");
        if (null != path) {
            path = MiscUtil.decodeHtmlReference(path);
        }
        if (null != pathTwo) {
            pathTwo = MiscUtil.decodeHtmlReference(pathTwo);
        }

        GBFile imageFile = GBFile.createFileByPath(reader.myPathPrefix
                + path);

        if (null == imageFile) {
            if (!path.startsWith("http://") && !path.startsWith("rtsp://")) {
                imageFile=null;
            }

        }

        GBFile imageFile2 = GBFile.createFileByPath(reader.myPathPrefix
                + pathTwo);

        if (null == imageFile2) {
            if (!pathTwo.startsWith("http://") && !pathTwo.startsWith("rtsp://")) {
                imageFile2=null;
            }

        }

        GBFileImage media = null;
        GBFileImage mediaTwo = null;

        final BookReader modelReader = reader.getModelReader();

        if (null != imageFile) {
            media = new GBFileImage(MimeType.MP3, imageFile);
            modelReader.addImage(path, media);

        }

        if (null != imageFile2) {
            mediaTwo = new GBFileImage(MimeType.MP3, imageFile2);
            modelReader.addImage(pathTwo, media);

        }

        GBFileCtrEntry ctr=new GBFileCtrEntry(path, pathTwo);


        modelReader.addHtml5FileControl(ctr.toChars());
    }

    @Override
    protected void doAtEnd(XHTMLReader reader) {
        // TODO Auto-generated method stub

    }

}
