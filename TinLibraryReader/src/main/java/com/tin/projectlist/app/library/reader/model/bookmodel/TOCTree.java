package com.tin.projectlist.app.library.reader.model.bookmodel;

import com.core.text.model.GBTextModel;

import java.io.Serializable;

/**
 * 类名： TOCTree.java<br>
 * 描述： 目录树的结构定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-9<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TOCTree extends com.core.object.GBTree<TOCTree> implements Serializable {
    private String myText;
    private Reference myReference;

    public TOCTree() {
        super();
    }

    public TOCTree(TOCTree parent) {
        super(parent);
    }

    public final String getText() {
        return myText;
    }

    public final void setText(String text) {
        if (text != null) {
            myText = text.trim().replaceAll("[\t ]+", " ");
        } else {
            myText = null;
        }
    }

    public Reference getReference() {
        return myReference;
    }

    public void setReference(GBTextModel model, int chpFileIndex, int paragraphIndex) {
        myReference = new Reference(chpFileIndex, paragraphIndex, model);
    }

    public static class Reference {
        public final int ChpFileIndex;
        public final int ParagraphIndex;
        public final GBTextModel Model;

        public Reference(final int chpFileIndex, final int paragraphIndex, final GBTextModel model) {
            ChpFileIndex = chpFileIndex;
            ParagraphIndex = paragraphIndex;
            Model = model;
        }
    }
    /**
     * 功能描述： 获取该节点在树中的位置<br>
     * 创建者： jack<br>
     * 创建日期：2013-5-15<br>
     *
     * @param rootToc 根结点树
     * @return
     */
    public int getIndex(TOCTree rootToc) {
        if (rootToc == null)
            return 0;
        for (int i = 0; i < rootToc.getSize(); i++) {
            if (rootToc.getTreeByParagraphNumber(i) == this)
                return i;
        }
        return 0;
    }
}
