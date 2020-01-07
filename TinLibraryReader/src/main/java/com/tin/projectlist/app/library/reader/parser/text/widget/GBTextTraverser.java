package com.tin.projectlist.app.library.reader.parser.text.widget;

import com.tin.projectlist.app.library.reader.parser.text.iterator.GBTextParagraphCursor;
/**
 * 类名： GBTextTraverser.java#ZLTextTraverser<br>
 * 描述： 选中文本内容提取工具抽象定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class GBTextTraverser {
    // 当前装载内容显示的控件
    private final GBTextView myView;

    public GBTextTraverser(GBTextView view) {
        myView = view;
    }
    /**
     * 功能描述： 选中的文字元素处理<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param word 要处理的文字元素
     */
    protected abstract void processWord(GBTextWord word);
    /**
     * 功能描述： 控制元素处理<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param control 控制元素
     */
    protected abstract void processControlElement(GBTextControlElement control);
    /**
     * 功能描述： 选中空格处理<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     */
    protected abstract void processSpace();
    /**
     * 功能描述： 段落结束处理<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     */
    protected abstract void processEndOfParagraph();
    /**
     * 功能描述： 提取选中文字<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-25<br>
     *
     * @param from 选择的开始位置
     * @param to 选择结束位置
     */
    public void traverse(GBTextPosition from, GBTextPosition to) {
        final int fromChpFileIndex = from.getChpFileIndex();
        int fromParagraph = from.getParagraphIndex();
        final int toChpFileIndex = to.getChpFileIndex();
        int toParagraph = to.getParagraphIndex();
        GBTextParagraphCursor cursor = GBTextParagraphCursor.cursor(myView.getModel(), fromChpFileIndex, fromParagraph);
        // if (fromChpFileIndex == toChpFileIndex) {
        // for (int i = fromParagraph; i <= toParagraph; ++i) {
        // // 计算段落开始和结束的元素下标
        // final int fromElement = i == fromParagraph ? from.getElementIndex() :
        // 0;
        // final int toElement = i == toParagraph ? to.getElementIndex() :
        // cursor.getParagraphLength() - 1;
        // // 循环处理每个选中的元素
        // for (int j = fromElement; j <= toElement; j++) {
        // final GBTextElement element = cursor.getElement(j);
        // if (element == GBTextElement.HSpace) {
        // processSpace();
        // } else if (element instanceof GBTextWord) {
        // processWord((GBTextWord) element);
        // }
        // }
        // // 切换段落
        // if (i < toParagraph) {
        // processEndOfParagraph();
        // cursor = cursor.next();
        // }
        // }
        // } else {
        for (int i1 = fromChpFileIndex; i1 <= toChpFileIndex; i1++) {
            fromParagraph = (i1 == fromChpFileIndex ? from.getParagraphIndex() : 0);
            toParagraph = (i1 == toChpFileIndex ? to.getParagraphIndex() : myView.getModel().getParagraphsNumber(
                    toChpFileIndex) - 1);
            for (int i = fromParagraph; i <= toParagraph; ++i) {
                // 计算段落开始和结束的元素下标
                final int fromElement = (i == fromParagraph ? from.getElementIndex() : 0);
                final int toElement = (i == toParagraph ? to.getElementIndex() : cursor.getParagraphLength() - 1);
                // 循环处理每个选中的元素
                for (int j = fromElement; j <= toElement; j++) {
                    final GBTextElement element = cursor.getElement(j);
                    if (element == GBTextElement.HSpace) {
                        processSpace();
                    } else if (element instanceof GBTextWord) {
                        processWord((GBTextWord) element);
                    }
                }
                // 切换段落
                if (i < toParagraph) {
                    if (cursor.wordNum > 0)
                        processEndOfParagraph();
                    cursor = cursor.next();
                }
            }
        }
        // }
    }
}
