package com.tin.projectlist.app.library.reader.controller;

import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextRegion;
import com.tin.projectlist.app.library.reader.parser.text.widget.GBTextWordRegionSoul;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;
import com.tin.projectlist.app.library.reader.view.ReadView;

/**
 * 类名： MoveCursorAction.java<br>
 * 描述： 翻页动作业务响应<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class MoveCursorAction extends ReadAction {
	//翻页模式
	private final PageEnum.DirectType myDirection;

	public MoveCursorAction(ReaderApplication fbreader, PageEnum.DirectType direction) {
		super(fbreader);
		myDirection = direction;
	}

	@Override
	public void run(Object... params) {
		final ReadView fbView = Reader.getTextView();
		GBTextRegion region = fbView.getSelectedRegion();
		final GBTextRegion.Filter filter =
				(region != null && region.getSoul() instanceof GBTextWordRegionSoul)
						|| Reader.NavigateAllWordsOption.getValue()
						? GBTextRegion.AnyRegionFilter : GBTextRegion.ImageOrHyperlinkFilter;
		region = fbView.nextRegion(myDirection, filter);
		if (region != null) {
			fbView.selectRegion(region);
		} else {
			switch (myDirection) {
				case DOWN:
					fbView.scrollPage(true, ReadView.ScrollingMode.SCROLL_LINES, 1);
					break;
				case UP:
					fbView.scrollPage(false, ReadView.ScrollingMode.SCROLL_LINES, 1);
					break;
			}
		}

		Reader.getViewImp().reset();
		Reader.getViewImp().repaint();
	}
}
