package com.tin.projectlist.app.library.reader.parser.text.widget;

/**
 * 类名： GBNoteElement.java<br>
 * 描述： 批注元素<br>
 * 创建者： 周波<br>
 * 创建日期：2015-6-4<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public final class GBNoteElement extends GBTextElement {
    public final String Value;
    public boolean isLeft = false;

    public GBNoteElement(String value) {
        Value = value;
    }

    @Override
    public int hashCode() {
        return Value.hashCode();
    }
}
