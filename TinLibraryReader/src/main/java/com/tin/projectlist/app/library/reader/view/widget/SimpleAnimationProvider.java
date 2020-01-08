package com.tin.projectlist.app.library.reader.view.widget;

import com.core.view.PageEnum.PageIndex;

/**
 * 类名： SimpleAnimationProvider.java<br>
 * 描述： 简单动画支持类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本：  <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
abstract class SimpleAnimationProvider extends AnimationProvider {
	private float mySpeedFactor;

	SimpleAnimationProvider(BitmapManager bitmapManager) {
		super(bitmapManager);
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

	@Override
	protected void setupAnimatedScrollingStart(Integer x, Integer y) {
		if (x == null || y == null) {
			if (myDirection.mIsHorizontal) {
				x = mySpeed < 0 ? myWidth : 0;
				y = 0;
			} else {
				x = 0;
				y = mySpeed < 0 ? myHeight : 0;
			}
		}
		myEndX = myStartX = x;
		myEndY = myStartY = y;
	}

	@Override
	protected void startAnimatedScrollingInternal(int speed) {
		mySpeedFactor = (float) Math.pow(1.5, 0.25 * speed);
		doStep();
	}

	@Override
	void doStep() {
		if (!getMode().Auto) {
			return;
		}

		switch (myDirection) {
			case LTOR:
				myEndX -= (int)mySpeed;
				break;
			case RTOL:
				myEndX += (int)mySpeed;
				break;
			case UP:
				myEndY += (int)mySpeed;
				break;
			case DOWN:
				myEndY -= (int)mySpeed;
				break;
		}
		final int bound;
		if (getMode() == Mode.AnimatedScrollingForward) {
			bound = myDirection.mIsHorizontal ? myWidth : myHeight;
		} else {
			bound = 0;
		}
		if (mySpeed > 0) {
			if (getScrollingShift() >= bound) {
				if (myDirection.mIsHorizontal) {
					myEndX = myStartX + bound;
				} else {
					myEndY = myStartY + bound;
				}
				terminate();
				return;
			}
		} else {
			if (getScrollingShift() <= -bound) {
				if (myDirection.mIsHorizontal) {
					myEndX = myStartX - bound;
				} else {
					myEndY = myStartY - bound;
				}
				terminate();
				return;
			}
		}
		mySpeed *= mySpeedFactor;
	}
}
