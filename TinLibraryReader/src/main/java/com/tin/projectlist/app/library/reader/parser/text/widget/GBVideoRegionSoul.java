package com.tin.projectlist.app.library.reader.parser.text.widget;
/**
 * 类名： GBVideoRegionSoul.java<br>
 * 描述： 视频元素区域信息<br>
 * 创建者： jack<br>
 * 创建日期：2013-12-2<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBVideoRegionSoul extends GBTextRegion.Soul {
    public final GBVideoElement VideoElement;
    public final GBTextElementArea mArea;
    public GBVideoRegionSoul(GBTextElementArea position, GBVideoElement videoElement) {
        super(position.getChpFileIndex(), position.getParagraphIndex(), position.getElementIndex(), position
                .getElementIndex());
        mArea = position;
        VideoElement = videoElement;
    }
}
