package com.tin.projectlist.app.library.reader.parser.text.widget;

public class GBTextWordRegionSoul extends GBTextRegion.Soul {
    public final GBTextWord Word;

    GBTextWordRegionSoul(GBTextPosition position, GBTextWord word) {
        super(position.getChpFileIndex(), position.getParagraphIndex(), position.getElementIndex(), position
                .getElementIndex());
        Word = word;
    }
}
