package com.tin.projectlist.app.library.reader.controller;


import com.tin.projectlist.app.library.reader.parser.domain.GBAction;

/**
 * 类名： ReadAction.java#FBAction<br>
 * 描述： 阅读业务处理类抽象定义<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-25<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class ReadAction extends GBAction {
	protected final ReaderApplication Reader;

	public ReadAction(ReaderApplication fbreader) {
		Reader = fbreader;
	}
}
