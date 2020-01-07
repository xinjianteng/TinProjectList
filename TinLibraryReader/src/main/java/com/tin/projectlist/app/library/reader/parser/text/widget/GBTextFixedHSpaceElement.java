package com.tin.projectlist.app.library.reader.parser.text.widget;
/**
 * 界面元素 空格 original:ZLTextFixedHSpaceElement 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-11<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextFixedHSpaceElement extends GBTextElement {
    private final static GBTextElement[] ourCollection = new GBTextElement[20];

    public static GBTextElement getElement(short length) {
        if (length >= 0 && length < 20) {
            GBTextElement cached = ourCollection[length];
            if (cached == null) {
                cached = new GBTextFixedHSpaceElement(length);
                ourCollection[length] = cached;
            }
            return cached;
        } else {
            return new GBTextFixedHSpaceElement(length);
        }
    }

    public final short Length;

    private GBTextFixedHSpaceElement(short length) {
        Length = length;
    }
}
