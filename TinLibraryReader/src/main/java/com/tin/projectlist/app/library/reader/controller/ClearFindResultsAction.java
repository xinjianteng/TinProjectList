package com.tin.projectlist.app.library.reader.controller;
/**
 * 类名： ClearFindResultsAction.java<br>
 * 描述： 清楚查找结果<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class ClearFindResultsAction extends ReadAction {
	ClearFindResultsAction(ReaderApplication fbreader) {
		super(fbreader);
	}

	@Override
	public void run(Object... params) {
		Reader.getTextView().clearFindResults();
	}
}
