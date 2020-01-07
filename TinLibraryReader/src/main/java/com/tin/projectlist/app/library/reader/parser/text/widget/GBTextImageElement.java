package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.file.image.GBImageData;

/**
 * 界面元素  图片
 * original:ZLTextImageElement
 * 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-11<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBTextImageElement extends GBTextElement {
    public final String Id;
    public final GBImageData ImageData;
    public final String URL;
    public final boolean IsCover;

    public int width = -1;
    public int height = -1;

    public GBTextImageElement(String id, GBImageData imageData, String url, boolean isCover) {
        Id = id;
        ImageData = imageData;
        URL = url;
        IsCover = isCover;
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
