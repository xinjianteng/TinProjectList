package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.core.xml.GBStringMap;
import com.geeboo.read.model.bookmodel.BookReader;
import com.geeboo.read.model.bookmodel.GBTextKind;
import com.geeboo.read.model.parser.css.AttrBorderAction;

public class XHTMLTagTableAction extends XHTMLTagAction{

    @Override
    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        final BookReader modelReader = reader.getModelReader();
        //往控制标签栈中压入一个标示
        modelReader.pushKind(GBTextKind.TABLE);
        //将控制标签写入缓存
        modelReader.addControl(GBTextKind.TABLE, true);



    }

    @Override
    protected void doAtEnd(XHTMLReader reader) {
        final BookReader modelReader = reader.getModelReader();
        //写入结束控制标到缓存
        modelReader.addControl(GBTextKind.TABLE, false);
        //从栈顶移除标示
        modelReader.popKind();
        reader.styleReader.removeTempStyleInternal();

    }

}
