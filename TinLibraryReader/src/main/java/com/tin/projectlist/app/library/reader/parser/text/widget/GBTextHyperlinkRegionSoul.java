package com.core.text.widget;

import java.util.List;

import com.core.text.model.GBTextHyperlink;

public class GBTextHyperlinkRegionSoul extends GBTextRegion.Soul {
    public final GBTextHyperlink Hyperlink;

    private static int startElementIndex(GBTextHyperlink hyperlink, int fallback) {
        final List<Integer> indexes = hyperlink.elementIndexes();
        return indexes.isEmpty() ? fallback : indexes.get(0);
    }

    private static int endElementIndex(GBTextHyperlink hyperlink, int fallback) {
        final List<Integer> indexes = hyperlink.elementIndexes();
        return indexes.isEmpty() ? fallback : indexes.get(indexes.size() - 1);
    }

    public GBTextHyperlinkRegionSoul(GBTextPosition position, GBTextHyperlink hyperlink) {
        super(position.getChpFileIndex(), position.getParagraphIndex(), startElementIndex(hyperlink,
                position.getElementIndex()), endElementIndex(hyperlink, position.getElementIndex()));
        Hyperlink = hyperlink;
    }
}
