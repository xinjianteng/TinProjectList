package com.core.text.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名： GBTextTrParagraphImpl.java<br>
 * 描述： 表格结构-行(tr标签)段落实现类<br>
 * 创建者： jack<br>
 * 创建日期：2013-11-20<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBTextTrParagraphImpl extends GBTextParagraphImpl {
    private final byte myKind;
    public int mChildNumber = 0; // 单元格数目
    public List<Integer> mIndex; // 元素分割索引
    public List<Byte> mKinds; // 元素分割索引
    /**
     * 构造方法
     *
     * @param kind 段落类型
     * @param model 数据模型
     * @param chpFileIndex 段落所在章节索引
     * @param offset 段落索引
     */
    GBTextTrParagraphImpl(byte kind, GBTextPlainModel model, int chpFileIndex, int offset) {
        super(model, chpFileIndex, offset);
        myKind = kind;
        mIndex = new ArrayList<Integer>();
        mKinds = new ArrayList<Byte>();
    }

    public byte getKind() {
        return myKind;
    }
}
