package com.tin.projectlist.app.library.reader.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.core.object.GBColor;
import com.geeboo.read.controller.ReaderApplication;

/**
 * 类名： ImageView.java<br>
 * 描述： 可以移动图片空间封装<br>
 * 创建者： jack<br>
 * 创建日期：2013-5-21<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class ImageView extends View {

    private Bitmap myBitmap;
    public void setMyBitmap(Bitmap myBitmap) {
//        if (myBitmap != null) {
//            myBitmap.recycle();
//            myBitmap = null;
//        }
        this.myBitmap = myBitmap;
    }
    private GBColor myBgColor;
    public void setBg(GBColor background) {
        myBgColor = background;
    }

    private final Paint myPaint = new Paint();

    private volatile int myDx = 0;
    private volatile int myDy = 0;
    private volatile float myZoomFactor = 1.0f;

    ImageView(Context context) {
        super(context);
        myBgColor = ((ReaderApplication) ReaderApplication.Instance()).ImageViewBackgroundOption.getValue();
    }

    public ImageView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public ImageView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (myBgColor == null)
            myBgColor = ((ReaderApplication) ReaderApplication.Instance()).ImageViewBackgroundOption.getValue();

        myPaint.setColor(GBAndroidColorUtil.rgb(myBgColor));
        final int w = getWidth();
        final int h = getHeight();
        canvas.drawRect(0, 0, w, h, myPaint);
        if (myBitmap == null || myBitmap.isRecycled()) {
            return;
        }

        final int bw = (int) (myBitmap.getWidth() * myZoomFactor);
        final int bh = (int) (myBitmap.getHeight() * myZoomFactor);

        final Rect src = new Rect(0, 0, (int) (w / myZoomFactor), (int) (h / myZoomFactor));
        final Rect dst = new Rect(0, 0, w, h);
        if (bw <= w) {
            src.left = 0;
            src.right = myBitmap.getWidth();
            dst.left = (w - bw) / 2;
            dst.right = dst.left + bw;
        } else {
            final int bWidth = myBitmap.getWidth();
            final int pWidth = (int) (w / myZoomFactor);
            src.left = Math.min(bWidth - pWidth, Math.max((bWidth - pWidth) / 2 - myDx, 0));
            src.right += src.left;
        }
        if (bh <= h) {
            src.top = 0;
            src.bottom = myBitmap.getHeight();
            dst.top = (h - bh) / 2;
            dst.bottom = dst.top + bh;
        } else {
            final int bHeight = myBitmap.getHeight();
            final int pHeight = (int) (h / myZoomFactor);
            src.top = Math.min(bHeight - pHeight, Math.max((bHeight - pHeight) / 2 - myDy, 0));
            src.bottom += src.top;
        }
        canvas.drawBitmap(myBitmap, src, dst, myPaint);
    }

    private void shift(int dx, int dy) {
        if (myBitmap == null || myBitmap.isRecycled()) {
            return;
        }

        final int w = (int) (getWidth() / myZoomFactor);
        final int h = (int) (getHeight() / myZoomFactor);
        final int bw = myBitmap.getWidth();
        final int bh = myBitmap.getHeight();

        final int newDx, newDy;

        if (w < bw) {
            final int delta = (bw - w) / 2;
            newDx = Math.max(-delta, Math.min(delta, myDx + dx));
        } else {
            newDx = myDx;
        }
        if (h < bh) {
            final int delta = (bh - h) / 2;
            newDy = Math.max(-delta, Math.min(delta, myDy + dy));
        } else {
            newDy = myDy;
        }

        if (newDx != myDx || newDy != myDy) {
            myDx = newDx;
            myDy = newDy;
            postInvalidate();
        }
    }

    private boolean myMotionControl;
    private int mySavedX;
    private int mySavedY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getPointerCount()) {
            case 1 :
                return onSingleTouchEvent(event);
            case 2 :
                return onDoubleTouchEvent(event);
            default :
                return false;
        }
    }

    private boolean onSingleTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP :
                myMotionControl = false;
                break;
            case MotionEvent.ACTION_DOWN :
                myMotionControl = true;
                mySavedX = x;
                mySavedY = y;
                break;
            case MotionEvent.ACTION_MOVE :
                if (myMotionControl) {
                    shift((int) ((x - mySavedX) / myZoomFactor), (int) ((y - mySavedY) / myZoomFactor));
                }
                myMotionControl = true;
                mySavedX = x;
                mySavedY = y;
                break;
        }
        return true;
    }

    private float myStartPinchDistance2 = -1;
    private float myStartZoomFactor;
    private boolean onDoubleTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_UP :
                myStartPinchDistance2 = -1;
                break;
            case MotionEvent.ACTION_POINTER_DOWN : {
                final float diffX = event.getX(0) - event.getX(1);
                final float diffY = event.getY(0) - event.getY(1);
                myStartPinchDistance2 = Math.max(diffX * diffX + diffY * diffY, 10f);
                myStartZoomFactor = myZoomFactor;
                break;
            }
            case MotionEvent.ACTION_MOVE : {
                final float diffX = event.getX(0) - event.getX(1);
                final float diffY = event.getY(0) - event.getY(1);
                final float distance2 = Math.max(diffX * diffX + diffY * diffY, 10f);
                if (myStartPinchDistance2 < 0) {
                    myStartPinchDistance2 = distance2;
                    myStartZoomFactor = myZoomFactor;
                } else {
                    myZoomFactor = myStartZoomFactor * FloatMath.sqrt(distance2 / myStartPinchDistance2);
                    postInvalidate();
                }
            }
            break;
        }
        return true;
    }
    /*
     * 回收图片资源
     */
    public void recycle() {
        if (myBitmap != null) {
            myBitmap.recycle();
            myBitmap = null;
        }
    }
}
