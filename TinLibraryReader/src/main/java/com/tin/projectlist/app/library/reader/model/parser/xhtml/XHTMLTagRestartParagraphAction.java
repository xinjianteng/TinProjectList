package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.parser.text.model.GBTextParagraph;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagRestartParagraphAction.java<br>
 * 描述： 换行标签处理类 《br》<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagRestartParagraphAction extends XHTMLTagAction {
    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        if (reader.getModelReader().getMyTextBufferLength() == 0) {
            reader.getModelReader().beginParagraph(GBTextParagraph.Kind.EMPTY_LINE_PARAGRAPH);
        } else {
            reader.getModelReader().beginParagraph();
        }
        reader.getModelReader().endParagraph();
    }

    protected void doAtEnd(XHTMLReader reader) {
    }
}
