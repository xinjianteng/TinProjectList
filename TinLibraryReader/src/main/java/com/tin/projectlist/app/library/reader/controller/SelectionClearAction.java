package com.tin.projectlist.app.library.reader.controller;
/**
 * 类名： SelectionClearAction.java<br>
 * 描述： 清除选中文字的业务处理<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class SelectionClearAction extends ReadAction {
	SelectionClearAction(ReaderApplication fbreader) {
		super(fbreader);
	}

	@Override
	public void run(Object... params) {
		Reader.getTextView().clearSelection();
	}
}
