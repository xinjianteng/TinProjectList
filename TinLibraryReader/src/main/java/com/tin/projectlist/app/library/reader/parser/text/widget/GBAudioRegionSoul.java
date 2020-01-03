package com.tin.projectlist.app.library.reader.parser.text.widget;
/**
 * 类名： GBAudioRegionSoul.java<br>
 * 描述： 音频对象区域信息<br>
 * 创建者： jack<br>
 * 创建日期：2013-12-2<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBAudioRegionSoul extends GBTextRegion.Soul {
    public final GBAudioElement AudioElement;
    public GBTextElementArea mArea;
    public GBAudioRegionSoul(GBTextElementArea position, GBAudioElement audioElement) {
        super(position.getChpFileIndex(), position.getParagraphIndex(), position.getElementIndex(), position
                .getElementIndex());
        mArea = position;
        AudioElement = audioElement;
    }
}
