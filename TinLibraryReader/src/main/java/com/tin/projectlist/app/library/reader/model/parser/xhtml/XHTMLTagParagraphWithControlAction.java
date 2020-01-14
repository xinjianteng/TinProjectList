package com.tin.projectlist.app.library.reader.model.parser.xhtml;


import com.tin.projectlist.app.library.reader.model.bookmodel.BookReader;
import com.tin.projectlist.app.library.reader.model.bookmodel.GBTextKind;
import com.tin.projectlist.app.library.reader.parser.text.model.GBTextParagraph;
import com.tin.projectlist.app.library.reader.parser.xml.GBStringMap;

/**
 * 类名： XHTMLTagParagraphWithControlAction.java<br>
 * 描述： 段落和非普通样式的控制标签处理类，如 h1 h2 title<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class XHTMLTagParagraphWithControlAction extends XHTMLTagAction {
    final byte myControl;

    XHTMLTagParagraphWithControlAction(byte control) {
        myControl = control;
    }

    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        final BookReader modelReader = reader.getModelReader();
        switch (myControl) {
            case GBTextKind.TITLE :
            case GBTextKind.H1 :
            case GBTextKind.H2 :
                // if
                // (modelReader.Model.BookTextModel.getTotalParagraphsNumber() >
                // 1) {
                // modelReader.insertEndOfSectionParagraph();
                // }
                modelReader.enterTitle();
                break;
        }
        modelReader.pushKind(myControl);
        if (myControl == GBTextKind.TR)
            modelReader.beginParagraph(GBTextParagraph.Kind.TREE_PARAGRAPH);
        else
            modelReader.beginParagraph();
    }

    protected void doAtEnd(XHTMLReader reader) {
        final BookReader modelReader = reader.getModelReader();
        modelReader.endParagraph();
        modelReader.popKind();
        switch (myControl) {
            case GBTextKind.TITLE :
            case GBTextKind.H1 :
            case GBTextKind.H2 :
                modelReader.exitTitle();
                break;
        }
    }
}
/*
 * void XHTMLTagParagraphWithControlAction::doAtStart(XHTMLReader &reader, const
 * char**) { if ((myControl == TITLE) &&
 * (bookReader(reader).model().bookTextModel()->paragraphsNumber() > 1)) {
 * bookReader(reader).insertEndOfSectionParagraph(); }
 * bookReader(reader).pushKind(myControl); bookReader(reader).beginParagraph();
 * }
 */
