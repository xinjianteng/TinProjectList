package com.core.text.widget;

import com.core.file.image.GBFileImage;

/**
 * 类名： GBAudioElement.java<br>
 * 描述： 音频元素<br>
 * 创建者： jack<br>
 * 创建日期：2013-11-6<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBAudioElement extends GBTextElement {
    public final String Id;
    public final GBFileImage mAudioFile;
    public final String URL;
    public boolean isLeft = false;

    public GBAudioElement(String id, GBFileImage audiofile, String url) {
        Id = id;
        mAudioFile = audiofile;
        URL = url;
    }
    @Override
    public int hashCode() {
        return Id.hashCode();
    }
}
