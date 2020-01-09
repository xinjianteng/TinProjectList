package com.tin.projectlist.app.library.reader.controller;

import com.core.view.PageEnum;

/**
 * 类名： TurnPageAction.java<br>
 * 描述： 阅读翻页动作处理<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class TurnPageAction extends ReadAction {
	private final boolean myForward;

	TurnPageAction(ReaderApplication fbreader, boolean forward) {
		super(fbreader);
		myForward = forward;
	}

	@Override
	public boolean isEnabled() {
		final ScrollingPreferences preferences = ScrollingPreferences.Instance();

		final ScrollingPreferences.FingerScrolling fingerScrolling =
				preferences.FingerScrollingOption.getValue();
		return
				fingerScrolling == ScrollingPreferences.FingerScrolling.byTap ||
						fingerScrolling == ScrollingPreferences.FingerScrolling.byTapAndFlick;
	}

	@Override
	public void run(Object... params) {
		final ScrollingPreferences preferences = ScrollingPreferences.Instance();
		if (params.length == 2 && params[0] instanceof Integer && params[1] instanceof Integer) {
			final int x = (Integer)params[0];
			final int y = (Integer)params[1];
			Reader.getViewImp().startScroll(
					myForward ? PageEnum.PageIndex.NEXT : PageEnum.PageIndex.PREVIOUS,
					x, y,
					preferences.HorizontalOption.getValue()
							? PageEnum.DirectType.RTOL : PageEnum.DirectType.UP,
					preferences.AnimationSpeedOption.getValue()
			);
		} else {
			Reader.getViewImp().startScroll(
					myForward ?  PageEnum.PageIndex.NEXT : PageEnum.PageIndex.PREVIOUS,
					preferences.HorizontalOption.getValue()
							?  PageEnum.DirectType.RTOL : PageEnum.DirectType.UP,
					preferences.AnimationSpeedOption.getValue()
			);
		}
	}
}
