package com.tin.projectlist.app.library.reader.controller;

import com.core.text.widget.GBTextControlElement;
import com.core.text.widget.GBTextTraverser;
import com.core.text.widget.GBTextView;
import com.core.text.widget.GBTextWord;

/**
 * 类名： WordCountTraverser.java<br>
 * 描述： 选中文字统计类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class WordCountTraverser extends GBTextTraverser {
	//计数器
	protected int myCount;

	public WordCountTraverser(GBTextView view) {
		super(view);
	}

	@Override
	protected void processWord(GBTextWord word) {
		++myCount;
	}

	@Override
	protected void processControlElement(GBTextControlElement control) {
		// does nothing
	}

	@Override
	protected void processSpace() {
		// does nothing
	}

	@Override
	protected void processEndOfParagraph() {
		// does nothing
	}

	public int getCount() {
		return myCount;
	}
}
