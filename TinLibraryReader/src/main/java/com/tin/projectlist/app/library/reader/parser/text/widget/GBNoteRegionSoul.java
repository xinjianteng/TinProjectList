package com.tin.projectlist.app.library.reader.parser.text.widget;
/**
 * 类名： GBNoteRegionSoul.java<br>
 * 描述： 批注对象区域信息<br>
 * 创建者： 周波<br>
 * 创建日期：2015-6-2<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBNoteRegionSoul extends GBTextRegion.Soul {
    public final GBNoteElement NoteElement;
    public GBTextElementArea mArea;
    public GBNoteRegionSoul(GBTextElementArea position, GBNoteElement noteElement) {
        super(position.getChpFileIndex(), position.getParagraphIndex(), position.getElementIndex(), position
                .getElementIndex());
        mArea = position;
        NoteElement = noteElement;
    }
}
