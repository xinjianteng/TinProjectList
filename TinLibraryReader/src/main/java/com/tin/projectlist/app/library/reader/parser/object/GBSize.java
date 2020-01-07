package com.tin.projectlist.app.library.reader.parser.object;
/**
 * 类名： GBSize.java<br>
 * 描述： 自定义尺寸实体<br>
 * 创建者： jack<br>
 * 创建日期：2013-3-28<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBSize {
    public final int mWidth;
    public final int mHeight;

    public GBSize(int w, int h) {
        mWidth = w;
        mHeight = h;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GBSize)) {
            return false;
        }
        final GBSize s = (GBSize) other;
        return mWidth == s.mWidth && mHeight == s.mHeight;
    }
}