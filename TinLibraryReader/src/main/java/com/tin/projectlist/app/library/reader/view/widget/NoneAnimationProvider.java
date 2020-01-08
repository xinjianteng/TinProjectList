package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.core.view.PageEnum.PageIndex;

/**
 * 类名： NoneAnimationProvider.java<br>
 * 描述： 无动画效果支持<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class NoneAnimationProvider extends AnimationProvider {
	private final Paint myPaint = new Paint();

	NoneAnimationProvider(BitmapManager bitmapManager) {
		super(bitmapManager);
	}

	@Override
	protected void drawInternal(Canvas canvas) {
		canvas.drawBitmap(getBitmapFrom()[0], 0, 0, myPaint);
	}

	@Override
	void doStep() {
		if (getMode().Auto) {
			terminate();
		}
	}

	@Override
	protected void setupAnimatedScrollingStart(Integer x, Integer y) {
		if (myDirection.mIsHorizontal) {
			myStartX = mySpeed < 0 ? myWidth : 0;
			myEndX = myWidth - myStartX;
			myEndY = myStartY = 0;
		} else {
			myEndX = myStartX = 0;
			myStartY = mySpeed < 0 ? myHeight : 0;
			myEndY = myHeight - myStartY;
		}
	}

	@Override
	protected void startAnimatedScrollingInternal(int speed) {
	}

	@Override
	PageIndex getPageToScrollTo(int x, int y) {
		if (myDirection == null) {
			return PageIndex.CURRENT;
		}

		switch (myDirection) {
			case RTOL:
				return myStartX < x ? PageIndex.PREVIOUS : PageIndex.NEXT;
			case LTOR:
				return myStartX < x ? PageIndex.NEXT : PageIndex.PREVIOUS;
			case UP:
				return myStartY < y ? PageIndex.PREVIOUS : PageIndex.NEXT;
			case DOWN:
				return myStartY < y ? PageIndex.NEXT : PageIndex.PREVIOUS;
		}
		return PageIndex.CURRENT;
	}

}
