package com.core.text.model.impl;
/**
 * 类名： GBTextSpecialParagraphImpl.java<br>
 * 描述： 特殊段落实现类<br>
 * 创建者： jack<br>
 * 创建日期：2013-7-5<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextSpecialParagraphImpl extends GBTextParagraphImpl {
    private final byte myKind;

    /**
     * 构造方法
     * @param kind 段落类型
     * @param model 数据模型
     * @param chpFileIndex 段落所在章节索引
     * @param offset 段落索引
     */
    GBTextSpecialParagraphImpl(byte kind, GBTextPlainModel model, int chpFileIndex, int offset) {
        super(model, chpFileIndex, offset);
        myKind = kind;
    }

    public byte getKind() {
        return myKind;
    }
}
