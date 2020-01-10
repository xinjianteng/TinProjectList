package com.tin.projectlist.app.library.reader.model.parser.xhtml;

import com.core.xml.GBStringMap;
import com.geeboo.read.model.bookmodel.BookReader;
import com.geeboo.read.model.bookmodel.GBTextKind;

public class XHTMLTagTDAction extends XHTMLTagAction {

    @Override
    protected void doAtStart(XHTMLReader reader, GBStringMap xmlattributes) {
        final BookReader modelReader = reader.getModelReader();
        // 往控制标签栈中压入一个标示
        modelReader.pushKind(GBTextKind.TD);
        // 将控制标签写入缓存
        modelReader.addControl(GBTextKind.TD, true);
       /* Stack<Integer> styleSpoorStack =reader.myModelReader.getStylePossible();
         int chpFileIndex=((GBTextWritableModel)reader.myModelReader.Model.getTextModel()).getWrithChpFileIndex();
         int index=((GBTextWritableModel)reader.myModelReader.Model.getTextModel()).getParagraphsNumber(chpFileIndex);
        for(int i=0;i<styleSpoorStack.size();i++){
            int [] styleIndex=reader.myModelReader.Model.getTextModel().getStyleParagraphIncluded(chpFileIndex, index);
            for (int j : styleIndex) {
                if (j > -1) {
                    GBTextParagraphCursor ci = GBTextParagraphCursor.cursor((GBTextWritableModel)reader.myModelReader.Model.getTextModel(), chpFileIndex, i);
                    ci.get
                    int start = 0, end = ci.getParagraphLength();
                    for (; start != end; ++start) {
                        if (ci.getElement(start) instanceof GBTextStyleElement) {
                            final GBTextStyleElement gse = (GBTextStyleElement) ci.getElement(start);
                            if (gse.Entry != null) {
                                mView.mStyleStack.push(new GBTextCssDecoratedStyle(mView.getTextStyle(), gse.Entry, i));
                                }
                            }
                        }
                    }
                }
            }
           // styleSpoorStack.get(i)
        }*/
       /* ArrayList<GBTextStyleEntry> currentStyleList = reader.styleReader.getCurrentEntrys();
        if (null != currentStyleList) {
            for (GBTextStyleEntry entry : currentStyleList) {
                if (entry instanceof GBTextBorderStyleEntry) {
                    if (((GBTextBorderStyleEntry) entry).getHtmBorder()!=null) {
                        reader.styleReader.readStyleByAttr("border", ((GBTextBorderStyleEntry) entry).getHtmBorder()
                                + "");
                        break;
                    }
                }
            }
        }*/
    }

    @Override
    protected void doAtEnd(XHTMLReader reader) {
        final BookReader modelReader = reader.getModelReader();
        // 写入结束控制标到缓存
        modelReader.addControl(GBTextKind.TD, false);
        // 从栈顶移除标示
        modelReader.popKind();

    }

}
