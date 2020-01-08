package com.tin.projectlist.app.library.reader.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.tin.projectlist.app.library.reader.R;


/**
 * 类名：ZoomView.java<br>
 * 描述： 支持放大镜的自定义视图<br>
 * 创建者： jack<br>
 * 创建日期：2014-9-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public abstract class ZoomView extends View {

    private int RADIUS = 58; // 放大镜半径
    private int SIZE = 131; // 放大镜尺寸
    // private static final int HANDLE_SIZE = 33;
    private static final long DELAY_TIME = 250;

    private Rect srcRect; // 放大镜图片显示区域
    private Point dstPoint; // 放大镜显示坐标

    private Bitmap magnifierBitmap; // 放大镜框
    // private Bitmap handleBitmap;

    private Bitmap resBitmap; // 要放大的原图
    private PopupWindow popup; // 放大镜容器
    private Magnifier magnifier; // 放大镜对象

    public ZoomView(Context context) {
        super(context);
        init(context);
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        BitmapDrawable magnifierDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.magnifier);
        magnifierBitmap = magnifierDrawable.getBitmap();
        float density = getResources().getDisplayMetrics().density;
        RADIUS *= density;
        SIZE *= density;
        magnifier = new Magnifier(context);
        popup = new PopupWindow(magnifier, SIZE, SIZE);
        popup.setAnimationStyle(android.R.style.Animation_Toast);
        // srcRect = new Rect(0, 0, RADIUS * 4 / 3, RADIUS * 4 / 3);
        // dstPoint = new Point(0, 0);
        srcRect = new Rect(0, 0, 2 * RADIUS, 2 * RADIUS);
        dstPoint = new Point(0, 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (resBitmap == null)
            return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            srcRect.offsetTo(x - RADIUS, y - RADIUS);
            dstPoint.set(x - RADIUS, y - 3 * RADIUS);
            if (srcRect.left < 0) {
                srcRect.offset(-srcRect.left, 0);
            } else if (srcRect.right > resBitmap.getWidth()) {
                srcRect.offset(resBitmap.getWidth() - srcRect.right, 0);
            }
            if (srcRect.top < 0) {
                srcRect.offset(0, -srcRect.top);
            } else if (srcRect.bottom > resBitmap.getHeight()) {
                srcRect.offset(0, resBitmap.getHeight() - srcRect.bottom);
            }
            if (y < 0) {
                popup.dismiss();
                invalidate();
                return true;
            }
            if (action == MotionEvent.ACTION_DOWN) {
                removeCallbacks(showZoom);
                postDelayed(showZoom, DELAY_TIME);
            } else if (!popup.isShowing()) {
                showZoom.run();
            }
            popup.update(getLeft() + dstPoint.x, getTop() + dstPoint.y, -1, -1);
            magnifier.invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
            removeCallbacks(showZoom);
            popup.dismiss();
        }
        invalidate();
        return true;
    }

    // 绘制放大镜内容
    protected void setResBitmap(Bitmap bm) {
        resBitmap = bm;
    }

    Runnable showZoom = new Runnable() {
        public void run() {
            popup.showAtLocation(ZoomView.this, Gravity.NO_GRAVITY, getLeft() + dstPoint.x, getTop() + dstPoint.y);
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 类名：ZoomView.java<br>
     * 描述： 放大镜对象<br>
     * 创建者： jack<br>
     * 创建日期：2014-9-26<br>
     * 版本： <br>
     * 修改者： <br>
     * 修改日期：<br>
     */
    class Magnifier extends View {
        private Paint mPaint;
        private Rect rect; // 绘制目标区
        private Path clip;

        public Magnifier(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(0xff008000);
            mPaint.setStyle(Style.STROKE);
            // rect = new Rect(0, 0, RADIUS * 3 / 2, RADIUS * 3 / 2);
            // clip = new Path();
            // clip.addCircle(2 + RADIUS * 2 / 3, 2 + RADIUS * 2 / 3, RADIUS * 2
            // / 3, Direction.CW);
            rect = new Rect(0, 0, RADIUS * 2, RADIUS * 2);
            clip = new Path();
            clip.addCircle(2 + RADIUS, 2 + RADIUS, RADIUS, Direction.CW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.clipPath(clip);
            // draw popup
            mPaint.setAlpha(255);
            canvas.drawBitmap(resBitmap, srcRect, rect, mPaint);
            canvas.restore();
            // draw popup frame
            mPaint.setAlpha(220);
            canvas.drawBitmap(magnifierBitmap, 0, 0, mPaint);
            // draw popup handle
            // mPaint.setAlpha(255);
            // canvas.drawBitmap(handleBitmap, SIZE-HANDLE_SIZE,
            // SIZE-HANDLE_SIZE, mPaint);
        }
    }
}
