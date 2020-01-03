package com.tin.projectlist.app.library.reader.parser.text.widget;

public class GBTextImageRegionSoul extends GBTextRegion.Soul {
    public final GBTextImageElement ImageElement;

    public GBTextImageRegionSoul(GBTextPosition position, GBTextImageElement imageElement) {
        super(position.getChpFileIndex(), position.getParagraphIndex(), position.getElementIndex(), position
                .getElementIndex());
        ImageElement = imageElement;
    }
}
