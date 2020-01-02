package com.core.text.widget;

import com.core.file.image.GBFileImage;

/**
 * 类名： GBAnimObjElement.java<br>
 * 描述： 交互动画元素<br>
 * 创建者： jack<br>
 * 创建日期：2013-11-6<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBAnimObjElement extends GBTextElement {
    public final String Id;
    public final GBFileImage mAnimData;
    public final String mIndex;
    public final String URL;
    public boolean isLeft = false;

    public GBAnimObjElement(String id, GBFileImage file, String url,String index) {
        Id = id;
        mAnimData = file;
        mIndex = index;
        URL = url;
    }
    @Override
    public int hashCode() {
        return Id.hashCode();
    }
}
