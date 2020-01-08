package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;

/**
 * 类名： SlideAnimationProvider.java<br>
 * 描述： 平移滑动动画支持类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class SlideAnimationProvider extends SimpleAnimationProvider {
    private final Paint myPaint = new Paint();
    int[] mBackSpinerColors;
    GradientDrawable mBackSpinerDrawableLR;
    GradientDrawable mBackSpinerDrawableRL;

    SlideAnimationProvider(BitmapManager bitmapManager) {
        super(bitmapManager);

        mBackSpinerColors = new int[]{0xcc111111, 0x111111};
        mBackSpinerDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, mBackSpinerColors);
        mBackSpinerDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackSpinerDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mBackSpinerColors);
        mBackSpinerDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

    }

    @Override
    protected void drawInternal(Canvas canvas) {
        canvas.drawBitmap(getBitmapTo()[0], 0, 0, myPaint);
        myPaint.setColor(Color.rgb(127, 127, 127));
        if (myDirection.mIsHorizontal) {
            final int dX = myEndX - myStartX;
            canvas.drawBitmap(getBitmapFrom()[0], dX, 0, myPaint);
            if (dX > 0 && dX < myWidth) {
                // canvas.drawLine(dX, 0, dX, myHeight + 1, myPaint);
                mBackSpinerDrawableRL.setBounds(dX - 20, 0, dX, myHeight + 1);
                mBackSpinerDrawableRL.draw(canvas);

            } else if (dX < 0 && dX > -myWidth) {
                // canvas.drawLine(dX + myWidth, 0, dX + myWidth, myHeight + 1,
                // myPaint);
                mBackSpinerDrawableLR.setBounds(dX + myWidth, 0, dX + myWidth + 20, myHeight + 1);
                mBackSpinerDrawableLR.draw(canvas);
            }
        } else {
            final int dY = myEndY - myStartY;
            canvas.drawBitmap(getBitmapFrom()[0], 0, dY, myPaint);
            if (dY > 0 && dY < myHeight) {
                canvas.drawLine(0, dY, myWidth + 1, dY, myPaint);
            } else if (dY < 0 && dY > -myHeight) {
                canvas.drawLine(0, dY + myHeight, myWidth + 1, dY + myHeight, myPaint);
            }
        }
    }
}
