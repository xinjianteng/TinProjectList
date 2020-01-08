package com.tin.projectlist.app.library.reader.view.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.widget.Scroller;

import com.core.domain.GBApplication;
import com.core.platform.GBLibrary;
import com.core.view.PageEnum.PageIndex;

/**
 * 类名： PageCurlAnimationProvider.java<br>
 * 描述： 逼真翻页动画<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
class PageCurlAnimationProvider extends AnimationProvider {

    private int mCornerX = 0; // 拖拽点对应的页脚
    private int mCornerY = 0;
    private Path mPath0;
    private Path mPath1;

    PointF mTouch = new PointF(); // 拖拽点
    PointF mBezierStart1 = new PointF(); // 贝塞尔曲线起始点
    PointF mBezierControl1 = new PointF(); // 贝塞尔曲线控制点
    PointF mBeziervertex1 = new PointF(); // 贝塞尔曲线顶点
    PointF mBezierEnd1 = new PointF(); // 贝塞尔曲线结束点

    PointF mBezierStart2 = new PointF(); // 另一条贝塞尔曲线
    PointF mBezierControl2 = new PointF();
    PointF mBeziervertex2 = new PointF();
    PointF mBezierEnd2 = new PointF();

    float mMiddleX;
    float mMiddleY;
    float mDegrees;
    float mTouchToCornerDis;
    ColorMatrixColorFilter mColorMatrixFilter;
    Matrix mMatrix;
    float[] mMatrixArray = {0, 0, 0, 0, 0, 0, 0, 0, 1.0f};

    public boolean mIsRTandLB; // 是否属于右上左下
    float mMaxLength;
    int[] mBackShadowColors;
    int[] mFrontShadowColors;
    GradientDrawable mBackShadowDrawableLR;
    GradientDrawable mBackShadowDrawableRL;
    GradientDrawable mFolderShadowDrawableLR;
    GradientDrawable mFolderShadowDrawableRL;

    GradientDrawable mFrontShadowDrawableHBT;
    GradientDrawable mFrontShadowDrawableHTB;
    GradientDrawable mFrontShadowDrawableVLR;
    GradientDrawable mFrontShadowDrawableVRL;
    Paint mPaint;
    Scroller mScroller;
    private float mySpeedFactor = 1;

    PageCurlAnimationProvider(BitmapManager bitmapManager) {
        super(bitmapManager);

        mPath0 = new Path();// 初始化绘制路线对象
        mPath1 = new Path();
        createDrawable();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);

        ColorMatrix cm = new ColorMatrix();
        float array[] = {0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0, 0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0};
        cm.set(array);
        mColorMatrixFilter = new ColorMatrixColorFilter(cm);
        mMatrix = new Matrix();

        mTouch.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
        mTouch.y = 0.01f;

        mScroller = new Scroller(bitmapManager.myWidget.getContext());

    }

    /**
     * 翻页动画回调方法
     */
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            calculateExceed(x, y);
        }
    }
    /**
     * 功能描述：计算<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-20<br>
     *
     * @param
     */
    private void calculateExceed(float x, float y) {
        int tempX = mTouch.x > 0 ? 2 * myWidth : -myWidth;
        if (tempX == 2 * myWidth) {
            if (x < tempX) {
                mTouch.x = x;
            } else {
                mTouch.x = tempX - 1;
            }
        } else {
            if (x > tempX) {
                mTouch.x = x;
            } else {
                mTouch.x = tempX + 1;
            }

            if (mTouch.x == myWidth) {
                mTouch.x = mTouch.x - 0.1f;
            }

        }

        if (y < myHeight) {
            mTouch.y = y;
        } else {
            mTouch.y = myHeight - 1;
        }
        myBitmapManager.myWidget.postInvalidate();
    }

    private volatile boolean mIsDrawHack = false;

    @Override
    protected void drawInternal(Canvas canvas) {
        if (!getMode().Auto) {
            mCornerX = myStartX > myWidth / 2 ? myWidth : 0;
            mCornerY = myStartY > myHeight / 2 ? myHeight : 0;
            mTouch.x = myEndX;
            mTouch.y = myEndY;
            if ((mCornerX == 0 && mCornerY == myHeight) || (mCornerX == myWidth && mCornerY == 0))
                mIsRTandLB = true;
            else
                mIsRTandLB = false;
        }

        try {
            canvas.drawColor(GBAndroidColorUtil.rgb(GBApplication.Instance().getCurrentView().getPaint().getBgColor()));// 0xFFAAAAAA);
            calcPoints();
            if (GBLibrary.Instance().isGPU() || mIsDrawHack) {
                Bitmap bit = Bitmap.createBitmap(myWidth, myHeight, getBitmapFrom()[0].getConfig());
                final Canvas canvas1 = new Canvas(bit);
                drawCurrentPageArea(canvas1, getBitmapFrom()[0], mPath0);
                if (null != getBitmapTo())
                    drawNextPageAreaAndShadow(canvas1, getBitmapTo()[0]);
                drawCurrentPageShadow(canvas1);
                drawCurrentBackArea(canvas1, getBitmapFrom()[0]);
                // draw3D(canvas1);
                canvas.drawBitmap(bit, 0, 0, mPaint);
            } else {
                drawCurrentPageArea(canvas, getBitmapFrom()[0], mPath0);
                if (null != getBitmapTo())
                    drawNextPageAreaAndShadow(canvas, getBitmapTo()[0]);
                drawCurrentPageShadow(canvas);
                drawCurrentBackArea(canvas, getBitmapFrom()[0]);
                // draw3D(canvas);
            }
        } catch (UnsupportedOperationException e) {
            mIsDrawHack = true;
            drawInternal(canvas);
        }
    }
    /**
     * 判断翻页位置
     */
    @Override
    PageIndex getPageToScrollTo(int x, int y) {
        if (myDirection == null) {
            return PageIndex.CURRENT;
        }

        switch (myDirection) {
            case LTOR :
                return myStartX < myWidth / 2 ? PageIndex.NEXT : PageIndex.PREVIOUS;
            case RTOL :
                return myStartX < myWidth / 2 ? PageIndex.PREVIOUS : PageIndex.NEXT;
            case UP :
                return myStartY < myHeight / 2 ? PageIndex.PREVIOUS : PageIndex.NEXT;
            case DOWN :
                return myStartY < myHeight / 2 ? PageIndex.NEXT : PageIndex.PREVIOUS;
        }
        return PageIndex.CURRENT;
    }

    @Override
    protected void startAnimatedScrollingInternal(int speed) {
        mySpeedFactor = (float) Math.pow(2.0, 0.25 * speed);
        mySpeed *= 1.5;
        // doStep();

        int dx, dy;
        dx = dy = 0;
        if (getMode() == Mode.AnimatedScrollingForward) {
            if (mCornerX > 0) {
                dx = -(int) (myWidth + mTouch.x);
            } else {
                dx = (int) (myWidth - mTouch.x + myWidth);
            }
        } else {
            if (mCornerX > 0) {
                dx = (int) (myWidth - mTouch.x - 1);
            } else {
                dx = -(int) mTouch.x;
            }
        }
        if (mCornerY > 0) {
            dy = (int) (myHeight - mTouch.y);
        } else {
            dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
        }
        mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy, 300 * (10 - speed));

    }

    @Override
    protected void setupAnimatedScrollingStart(Integer x, Integer y) {
        if (x == null || y == null) {
            if (myDirection.mIsHorizontal) {
                x = mySpeed < 0 ? myWidth * 2 / 3 : myWidth / 3;
                y = myHeight / 7;
            } else {
                x = myWidth / 3;
                y = mySpeed < 0 ? myHeight * 6 / 7 : myHeight / 7;
            }

        } else {
            final int cornerX = x > myWidth / 2 ? myWidth : 0;
            final int cornerY = y > myHeight / 2 ? myHeight : 0;
            int deltaX = Math.min(Math.abs(x - cornerX), myWidth / 5);
            int deltaY = Math.min(Math.abs(y - cornerY), myHeight / 5);
            if (myDirection.mIsHorizontal) {
                deltaY = Math.min(deltaY, deltaX / 3);
            } else {
                deltaX = Math.min(deltaX, deltaY / 3);
            }
            x = Math.abs(cornerX - deltaX);
            y = Math.abs(cornerY - deltaY);
        }

        myEndX = myStartX = x;
        myEndY = myStartY = y;
        mTouch.x = myEndX;
        mTouch.y = myEndY;

        mCornerX = mTouch.x > myWidth / 2 ? myWidth : 0;
        mCornerY = mTouch.y > myHeight / 2 ? myHeight : 0;

        if ((mCornerX == 0 && mCornerY == myHeight) || (mCornerX == myWidth && mCornerY == 0))
            mIsRTandLB = true;
        else
            mIsRTandLB = false;
    }

    @Override
    void doStep() {
        if (!getMode().Auto) {
            return;
        }
        if (Math.abs(mTouch.y) <= 1 || Math.abs(mTouch.y) >= myHeight - 1) {
            terminate();
            mScroller.abortAnimation();
            return;
        }

        // final int speed = (int) Math.abs(mySpeed);
        // mySpeed *= mySpeedFactor;
        //
        // final int cornerX = myStartX > myWidth / 2 ? myWidth : 0;
        // final int cornerY = myStartY > myHeight / 2 ? myHeight : 0;
        //
        // final int boundX, boundY;
        // if (getMode() == Mode.AnimatedScrollingForward) {
        // boundX = cornerX == 0 ? 2 * myWidth : -myWidth;
        // boundY = cornerY == 0 ? 2 * myHeight : -myHeight;
        // } else {
        // boundX = cornerX;
        // boundY = cornerY;
        // }
        //
        // final int deltaX = Math.abs(myEndX - cornerX);
        // final int deltaY = Math.abs(myEndY - cornerY);
        // final int speedX, speedY;
        // if (deltaX == 0 || deltaY == 0) {
        // speedX = speed;
        // speedY = speed;
        // } else if (deltaX < deltaY) {
        // speedX = speed;
        // speedY = speed * deltaY / deltaX;
        // } else {
        // speedX = speed * deltaX / deltaY;
        // speedY = speed;
        // }
        //
        // final boolean xSpeedIsPositive, ySpeedIsPositive;
        // if (getMode() == Mode.AnimatedScrollingForward) {
        // xSpeedIsPositive = cornerX == 0;
        // ySpeedIsPositive = cornerY == 0;
        // } else {
        // xSpeedIsPositive = cornerX != 0;
        // ySpeedIsPositive = cornerY != 0;
        // }
        //
        // if (xSpeedIsPositive) {
        // myEndX += speedX;
        // if (myEndX >= boundX) {
        // terminate();
        // }
        // } else {
        // myEndX -= speedX;
        // if (myEndX <= boundX) {
        // terminate();
        // }
        // }
        //
        // if (ySpeedIsPositive) {
        // myEndY += speedY;
        // if (myEndY >= boundY) {
        // terminate();
        // }
        // } else {
        // myEndY -= speedY;
        // if (myEndY <= boundY) {
        // terminate();
        // }
        // }
    }

    /**
     * 创建阴影的GradientDrawable
     */
    private void createDrawable() {
        int[] color = {0x333333, 0xb0333333};
        mFolderShadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, color);
        mFolderShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFolderShadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, color);
        mFolderShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowColors = new int[]{0xff111111, 0x111111};
        mBackShadowDrawableRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
        mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mBackShadowDrawableLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowColors = new int[]{0x80111111, 0x111111};
        mFrontShadowDrawableVLR = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
        mFrontShadowDrawableVLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mFrontShadowDrawableVRL = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
        mFrontShadowDrawableVRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHTB = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
        mFrontShadowDrawableHTB.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        mFrontShadowDrawableHBT = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
        mFrontShadowDrawableHBT.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    /**
     * 功能描述： 求解直线P1P2和直线P3P4的交点坐标<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-20<br>
     *
     * @param
     */
    private PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
        PointF CrossP = new PointF();
        // 二元函数通式： y=ax+b
        float a1 = (P2.y - P1.y) / (P2.x - P1.x);
        float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

        float a2 = (P4.y - P3.y) / (P4.x - P3.x);
        float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
        CrossP.x = (b2 - b1) / (a1 - a2);
        CrossP.y = a1 * CrossP.x + b1;
        return CrossP;
    }
    /**
     * 功能描述： 根据触摸点计算绘制坐标点<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-20<br>
     *
     * @param
     */
    private void calcPoints() {
        if (mMaxLength <= 0.0f) {
            mMaxLength = (float) Math.hypot(myWidth, myHeight);
        }
        // 计算出手指触摸点到屏幕右下角点间直线中点的X轴，Y轴的坐标
        mMiddleX = (mTouch.x + mCornerX) / 2;
        mMiddleY = (mTouch.y + mCornerY) / 2;

        // 中点向下延长线于屏幕下边焦点的X轴坐标
        mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
        mBezierControl1.y = mCornerY;

        mBezierControl2.x = mCornerX;
        // 中点向上延长线于屏幕下边焦点的X轴坐标
        mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

        // 计算出左侧曲线起始点坐标
        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2;
        mBezierStart1.y = mCornerY;

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (mTouch.x > 0 && mTouch.x < myWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > myWidth) {
                if (mBezierStart1.x < 0)
                    mBezierStart1.x = myWidth - mBezierStart1.x;

                float f1 = Math.abs(mCornerX - mTouch.x);
                float f2 = myWidth * f1 / mBezierStart1.x;
                mTouch.x = Math.abs(mCornerX - f2);

                float f3 = Math.abs(mCornerX - mTouch.x) * Math.abs(mCornerY - mTouch.y) / f1;
                mTouch.y = Math.abs(mCornerY - f3);

                mMiddleX = (mTouch.x + mCornerX) / 2;
                mMiddleY = (mTouch.y + mCornerY) / 2;

                mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
                mBezierControl1.y = mCornerY;

                mBezierControl2.x = mCornerX;
                mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY);
                mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2;
            }
        }

        // 计算出右侧曲线起始点坐标
        mBezierStart2.x = mCornerX;
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y) / 2;

        // 计算出触摸点到屏幕右下角直线长度
        mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX), (mTouch.y - mCornerY));
        if (mTouch.x == mBezierControl1.x)
            mBezierEnd1 = new PointF(mTouch.x, mCornerY > 0 ? (mTouch.y + myHeight) / 2 : mTouch.y / 2);
        else
            mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1, mBezierStart2);

        mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1, mBezierStart2);

        mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
        mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
        mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
        mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
    }
    /**
     * 功能描述： 绘制当前页的阴影效果<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-20<br>
     *
     * @param
     */
    private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {

        mPath0.reset();
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x, mBezierEnd1.y);
        mPath0.lineTo(mTouch.x, mTouch.y);
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x, mBezierStart2.y);
        mPath0.lineTo(mCornerX, mCornerY);
        mPath0.close();

        canvas.save();
        canvas.clipPath(path, Region.Op.XOR);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.restore();

    }
    /**
     * 功能描述： 绘制下一页的阴影效果<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-20<br>
     *
     * @param
     */
    private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
        mPath1.reset();
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.lineTo(mCornerX, mCornerY);
        mPath1.close();

        mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x - mCornerX, mBezierControl2.y - mCornerY));
        int leftx;
        int rightx;
        GradientDrawable mBackShadowDrawable;
        if (mIsRTandLB) {
            leftx = (int) (mBezierStart1.x);
            rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 4);
            mBackShadowDrawable = mBackShadowDrawableLR;
        } else {
            leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 4);
            rightx = (int) mBezierStart1.x;
            mBackShadowDrawable = mBackShadowDrawableRL;
        }
        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx, (int) (mMaxLength + mBezierStart1.y));
        mBackShadowDrawable.draw(canvas);
        canvas.restore();
    }
    /**
     * 绘制翻起页背面
     */
    private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
        int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
        float f1 = Math.abs(i - mBezierControl1.x);
        int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
        float f2 = Math.abs(i1 - mBezierControl2.y);
        float f3 = Math.min(f1, f2);
        mPath1.reset();
        mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
        mPath1.close();
        GradientDrawable mFolderShadowDrawable;
        int left;
        int right;
        if (mIsRTandLB) {
            left = (int) (mBezierStart1.x - 1);
            right = (int) (mBezierStart1.x + f3 + 1);
            mFolderShadowDrawable = mFolderShadowDrawableLR;
        } else {
            left = (int) (mBezierStart1.x - f3 - 1);
            right = (int) (mBezierStart1.x + 1);
            mFolderShadowDrawable = mFolderShadowDrawableRL;
        }
        canvas.save();
        canvas.clipPath(mPath0);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);

        mPaint.setColorFilter(mColorMatrixFilter);

        float dis = (float) Math.hypot(mCornerX - mBezierControl1.x, mBezierControl2.y - mCornerY);
        float f8 = (mCornerX - mBezierControl1.x) / dis;
        float f9 = (mBezierControl2.y - mCornerY) / dis;
        mMatrixArray[0] = 1 - 2 * f9 * f9;
        mMatrixArray[1] = 2 * f8 * f9;
        mMatrixArray[3] = mMatrixArray[1];
        mMatrixArray[4] = 1 - 2 * f8 * f8;
        mMatrix.reset();
        mMatrix.setValues(mMatrixArray);
        mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
        mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        // canvas.drawBitmap(bitmap, mMatrix, null);
        mPaint.setColorFilter(null);
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
        mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right, (int) (mBezierStart1.y + mMaxLength));
        mFolderShadowDrawable.draw(canvas);
        canvas.restore();
    }
    /**
     * 功能描述：绘制3D 图书效果<br>
     * 创建者： jack<br>
     * 创建日期：2012-11-20<br>
     *
     * @param
     */
    public void draw3D(Canvas canvas) {
        // mPaint.setColor(Color.RED);
        if (myWidth == 0 || myHeight == 0) {
            myWidth = myBitmapManager.myWidget.getWidth() - 5;
            myHeight = myBitmapManager.myWidget.getHeight();
        }
        canvas.drawLine(myWidth, 0, myWidth, myHeight, mPaint);
        canvas.drawLine(myWidth + 6, 0, myWidth + 6, myHeight, mPaint);
        canvas.drawLine(myWidth + 4, 0, myWidth + 4, myHeight, mPaint);
        canvas.drawLine(myWidth + 2, 0, myWidth + 2, myHeight, mPaint);

        mPath0.reset();
        mPath0.moveTo(myWidth, 0);
        mPath0.lineTo(myWidth + 8, 0);
        mPath0.lineTo(myWidth + 8, 6f);
        mPath0.close();

        mPath1.reset();
        mPath1.moveTo(myWidth, myHeight);
        mPath1.lineTo(myWidth + 8, myHeight);
        mPath1.lineTo(myWidth + 8, myHeight - 6f);
        mPath1.close();

        canvas.save();
        canvas.drawPath(mPath0, mPaint);
        canvas.drawPath(mPath1, mPaint);
        canvas.restore();

    }
    /**
     * 绘制翻起页的阴影
     */
    public void drawCurrentPageShadow(Canvas canvas) {
        // 处理动画结束绘制阴影问题
        if (Math.abs(mTouch.x) > myWidth - 2.5)
            return;
        double degree;
        if (mIsRTandLB) {
            degree = Math.PI / 4 - Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x - mBezierControl1.x);
        } else {
            degree = Math.PI / 4 - Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x - mBezierControl1.x);
        }
        // 翻起页阴影顶点与touch点的距离
        double d1 = (float) 25 * 1.414 * Math.cos(degree);
        double d2 = (float) 25 * 1.414 * Math.sin(degree);
        float x = (float) (mTouch.x + d1);
        float y;
        if (mIsRTandLB) {
            y = (float) (mTouch.y + d2);
        } else {
            y = (float) (mTouch.y - d2);
        }
        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
        mPath1.close();
        float rotateDegrees;
        canvas.save();

        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);

        int leftx;
        int rightx;
        GradientDrawable mCurrentPageShadow;
        if (mIsRTandLB) {
            leftx = (int) (mBezierControl1.x);
            rightx = (int) mBezierControl1.x + 25;
            mCurrentPageShadow = mFrontShadowDrawableVLR;
        } else {
            leftx = (int) (mBezierControl1.x - 25);
            rightx = (int) mBezierControl1.x + 1;
            mCurrentPageShadow = mFrontShadowDrawableVRL;
        }

        rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x - mBezierControl1.x, mBezierControl1.y - mTouch.y));
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
        mCurrentPageShadow.setBounds(leftx, (int) (mBezierControl1.y - mMaxLength), rightx, (int) (mBezierControl1.y));
        mCurrentPageShadow.draw(canvas);
        canvas.restore();

        mPath1.reset();
        mPath1.moveTo(x, y);
        mPath1.lineTo(mTouch.x, mTouch.y);
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
        mPath1.close();
        canvas.save();

        canvas.clipPath(mPath0, Region.Op.XOR);
        canvas.clipPath(mPath1, Region.Op.INTERSECT);

        if (mIsRTandLB) {
            leftx = (int) (mBezierControl2.y);
            rightx = (int) (mBezierControl2.y + 25);
            mCurrentPageShadow = mFrontShadowDrawableHTB;
        } else {
            leftx = (int) (mBezierControl2.y - 25);
            rightx = (int) (mBezierControl2.y + 1);
            mCurrentPageShadow = mFrontShadowDrawableHBT;
        }
        rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y - mTouch.y, mBezierControl2.x - mTouch.x));
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
        float temp;
        if (mBezierControl2.y < 0)
            temp = mBezierControl2.y - myHeight;
        else
            temp = mBezierControl2.y;

        int hmg = (int) Math.hypot(mBezierControl2.x, temp);
        if (hmg > mMaxLength)
            mCurrentPageShadow.setBounds((int) (mBezierControl2.x - 25) - hmg, leftx,
                    (int) (mBezierControl2.x + mMaxLength) - hmg, rightx);
        else
            mCurrentPageShadow.setBounds((int) (mBezierControl2.x - mMaxLength), leftx, (int) (mBezierControl2.x),
                    rightx);

        mCurrentPageShadow.draw(canvas);
        canvas.restore();
    }
}
