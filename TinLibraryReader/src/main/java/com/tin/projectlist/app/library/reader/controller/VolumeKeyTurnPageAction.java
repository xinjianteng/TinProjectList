package com.tin.projectlist.app.library.reader.controller;

import com.core.view.PageEnum;

/**
 * 类名： VolumeKeyTurnPageAction.java<br>
 * 描述： 使用音量键进行翻页操作<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class VolumeKeyTurnPageAction extends ReadAction {
	private final boolean myForward;

	VolumeKeyTurnPageAction(ReaderApplication fbreader, boolean forward) {
		super(fbreader);
		myForward = forward;
	}

	@Override
	public void run(Object... params) {
		final ScrollingPreferences preferences = ScrollingPreferences.Instance();
		//启动自动翻页
		Reader.getViewImp().startScroll(
				myForward ? PageEnum.PageIndex.NEXT : PageEnum.PageIndex.PREVIOUS,
				preferences.HorizontalOption.getValue()
						? PageEnum.DirectType.RTOL : PageEnum.DirectType.UP,
				preferences.AnimationSpeedOption.getValue()
		);
	}
}
