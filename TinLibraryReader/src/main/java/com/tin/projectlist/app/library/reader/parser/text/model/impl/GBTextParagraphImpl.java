package com.tin.projectlist.app.library.reader.parser.text.model.impl;

import com.tin.projectlist.app.library.reader.parser.text.model.GBTextParagraph;

/**
 *
 * 描述： 文本段 Kind.TEXT_PARAGRAPH<br>
 * 创建者： 燕冠楠<br>
 * 创建日期：2013-4-2<br>
 */
class GBTextParagraphImpl implements GBTextParagraph {
    private final GBTextPlainModel myModel;
    private final int myChpFileIndex; // 章节文件索引
    private final int myIndex; // 段落索引
    /**
     * 段落实体构造
     *
     * @param model mode实例
     * @param chpFileIndex 章节文件索引
     * @param index 章节段索引
     */
    GBTextParagraphImpl(GBTextPlainModel model, int chpFileIndex, int index) {
        myModel = model;
        myChpFileIndex = chpFileIndex;
        myIndex = index;
    }
    /**
     * 实例化一个段迭代器（根据当前index）
     */
    public EntryIterator iterator() {
        return myModel.new EntryIteratorImpl(myChpFileIndex, myIndex);
    }

    /**
     * 返回文本段类别
     */
    public byte getKind() {
        return Kind.TEXT_PARAGRAPH;
    }
}
