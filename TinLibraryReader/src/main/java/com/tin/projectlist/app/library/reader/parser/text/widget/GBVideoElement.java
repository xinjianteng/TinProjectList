package com.core.text.widget;

import com.core.file.image.GBFileImage;

/**
 * 类名： GBVideoElement.java<br>
 * 描述： 视频元素<br>
 * 创建者： jack<br>
 * 创建日期：2013-11-6<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBVideoElement extends GBTextElement {
    public final String Id;
    public final GBFileImage mVideoData;
    public final String URL;
    public boolean isLeft = false;

    public int width = -1;
    public int height = -1;

    public GBVideoElement(String id, GBFileImage file, String url) {
        Id = id;
        mVideoData = file;
        URL = url;
    }

    @Override
    public int hashCode() {
        return Id.hashCode();
    }

    public void setWidth(String width) {
        try {
            this.width = Integer.parseInt(width);
        } catch (NumberFormatException ex) {

        }
    }
    public void setHeight(String height) {
        try {
            this.height = Integer.parseInt(height);
        } catch (NumberFormatException ex) {

        }
    }
}
