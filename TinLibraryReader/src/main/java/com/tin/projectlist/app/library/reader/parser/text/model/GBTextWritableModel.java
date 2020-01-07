package com.tin.projectlist.app.library.reader.parser.text.model;

import com.tin.projectlist.app.library.reader.parser.text.model.style.GBTextStyleEntryProxy;

import java.util.Stack;

/**
 * 数据写入类 original:ZLTextWritableModel 类名： .java<br>
 * 描述： <br>
 * 创建者： yangn<br>
 * 创建日期：2013-4-16<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 * styleMarkStack
 */
public interface GBTextWritableModel extends GBTextModel {
	void createParagraph(byte kind);

	void addText(char[] text);

	void addText(char[] text, int offset, int length);

	void addImage(String id, short vOffset, boolean isCover);

	void addControl(byte textKind, boolean isStart);

	void addHyperlinkControl(byte textKind, byte hyperlinkType, String id);

	// void addStyleEntry(GBTextStyleEntry entry);
	int addStyleEntry(GBTextStyleEntryProxy entryProxy);

	void addHtml5AudioControl(char[] label);

	void addHtml5VideoControl(char[] label);

	void addHtml5NoteControl(char[] label);

	void addHtml5Control(byte kindType,char[] label);

	void addStyleColse();

	void addFixedHSpace(short length);

	void addBidiReset();

	void stopReading();

	void addStylePossible(Stack<Integer> stylePossible);

	int getWrithChpFileIndex();

	void setWrithChpFiliNum(int writhChpFileIndex);
	/**
	 * 设置章节之间是否累加文本长度
	 * @param isSumSize
	 */
	void settingSumSize(boolean isSumSize);

}
