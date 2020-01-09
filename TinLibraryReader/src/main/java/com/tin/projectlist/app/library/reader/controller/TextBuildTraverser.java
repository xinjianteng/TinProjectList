package com.tin.projectlist.app.library.reader.controller;

import com.core.text.widget.GBTextControlElement;
import com.core.text.widget.GBTextTraverser;
import com.core.text.widget.GBTextView;
import com.core.text.widget.GBTextWord;

/**
 * 类名： TextBuildTraverser.java<br>
 * 描述： 文字选择提取工具类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class TextBuildTraverser extends GBTextTraverser {
	//记录选中文字的对象
	protected final StringBuilder myBuffer = new StringBuilder();

	public TextBuildTraverser(GBTextView view) {
		super(view);
	}

	@Override
	protected void processWord(GBTextWord word) {
		//添加一个文本对象
		myBuffer.append(word.Data, word.Offset, word.Length);
	}

	@Override
	protected void processControlElement(GBTextControlElement control) {
		// does nothing  标签控制元素处理 空
	}

	@Override
	protected void processSpace() {
		//添加一个空格
		myBuffer.append(" ");
	}

	@Override
	protected void processEndOfParagraph() {
		//换行处理
		myBuffer.append("\n");
	}

	public String getText() {
		return myBuffer.toString();
	}
}
