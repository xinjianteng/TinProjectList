package com.tin.projectlist.app.library.reader.controller;

import com.geeboo.read.view.ReadView;

/**
 * 类名： FindNextAction.java<br>
 * 描述： 文字查找功能，查找下一个业务处理<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class FindNextAction extends ReadAction {
	FindNextAction(ReaderApplication fbreader) {
		super(fbreader);
	}

	@Override
	public boolean isEnabled() {
		ReadView view = Reader.getTextView();
		return (view != null) && view.canFindNext();
	}

	@Override
	public void run(Object... params) {
		Reader.getTextView().findNext();
	}
}
