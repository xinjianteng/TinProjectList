package com.tin.projectlist.app.library.reader.controller;

import com.core.text.widget.GBTextView;

/**
 * 类名： FindPreviousAction.java<br>
 * 描述： 查找上一个文本的业务处理类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class FindPreviousAction extends ReadAction {

	FindPreviousAction(ReaderApplication fbreader) {
		super(fbreader);
	}

	@Override
	public boolean isEnabled() {
		GBTextView view = Reader.getTextView();
		return (view != null) && view.canFindPrevious();
	}

	@Override
	public void run(Object... params) {
		Reader.getTextView().findPrevious();
	}
}
