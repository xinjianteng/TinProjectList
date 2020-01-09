package com.tin.projectlist.app.library.reader.controller;
/**
 * 类名： ExitAction.java<br>
 * 描述： <br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class ExitReadAction extends ReadAction {
	ExitReadAction(ReaderApplication fbreader) {
		super(fbreader);
	}

	@Override
	public void run(Object... params) {
		//判断如果没有在阅读页则返回阅读页面，否则退出
		if (Reader.getCurrentView() != Reader.BookTextView) {
			Reader.showBookTextView();
		} else {
			Reader.closeWindow();
		}
	}
}
