package com.tin.projectlist.app.library.reader.view.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.tin.projectlist.app.library.reader.parser.view.GBViewInter;
import com.tin.projectlist.app.library.reader.parser.view.PageEnum;

//import com.core.domain.GBApplication;
//import com.core.platform.GBLibrary;
//import com.core.view.GBView;
//import com.core.view.GBViewInter;
//import com.core.view.PageEnum;
//import com.core.view.PageEnum.DirectType;
//import com.core.view.PageEnum.PageIndex;
//import com.geeboo.R;
//import com.geeboo.read.controller.ActionCode;
//import com.geeboo.read.controller.ReaderApplication;
//import com.geeboo.read.controller.ScrollingPreferences;
//import com.geeboo.read.view.GBAndroidLibrary;
//import com.geeboo.read.view.ReaderActivity;
//import com.geeboo.read.view.poppanel.AudioPopup;
//import com.geeboo.utils.UIUtil;

/**
 * 类名： GBAndroidWidget.java<br>
 * 描述： 翻页控件业务实现类<br>
 * 创建者： jack<br>
 * 创建日期：2013-4-26<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class GBAndroidWidget extends ZoomView implements GBViewInter, View.OnLongClickListener {
    private final Paint myPaint = new Paint();
    private final BitmapManager myBitmapManager = new BitmapManager(this,
            GBAndroidLibrary.Instance().DoublePageOption.getValue());
    // private Bitmap myFooterBitmap;

    public GBAndroidWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GBAndroidWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GBAndroidWidget(Context context) {
        super(context);
        init();
    }

    private void init() {
        // next line prevent ignoring first onKeyDown DPad event
        // after any dialog was closed
        setFocusableInTouchMode(true);
        setDrawingCacheEnabled(false);
        setOnLongClickListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getAnimationProvider().terminate();
        if (myScreenIsTouched) {
            final GBView view = GBApplication.Instance().getCurrentView();
            myScreenIsTouched = false;
            view.onScrollEnd(PageEnum.PageIndex.CURRENT);
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        final Context context = getContext();
        if (context instanceof ReaderActivity) {
            // 唤醒屏幕
            // ((FBReader)context).createWakeLock();
        } else {
            System.err.println("A surprise: view's context is not an BYReader");
        }

        // final int w = getMainAreaWidth();
        // final int h = getMainAreaHeight();
        // 判断是否正在执行翻页动画
        if (getAnimationProvider().inProgress()) {
            onDrawInScrolling(canvas);
            isResetPage = true;
        } else {
            onDrawStatic(canvas);
            GBApplication.Instance().onRepaintFinished();
        }
        super.onDraw(canvas);
    }

    private AnimationProvider myAnimationProvider;
    private PageEnum.Anim myAnimationType;
    private Boolean myIsDouble;
    /**
     * 功能描述： 获取翻页动画对象<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     *
     * @return
     */
    private AnimationProvider getAnimationProvider() {
        final PageEnum.Anim type = GBApplication.Instance().getCurrentView().getAnimType();
        final boolean isDouble = getMainAreaWidth() > getMainAreaHeight()
                && ScrollingPreferences.Instance().DoublePageOption.getValue();
        if (myAnimationProvider == null || myAnimationType != type || myIsDouble != isDouble) {
            myAnimationType = type;
            myIsDouble = isDouble;

            switch (type) {
                case NONE :
                    myAnimationProvider = new NoneAnimationProvider(myBitmapManager);
                    break;
                case CURL :
                    if (getMainAreaWidth() > getMainAreaHeight()
                            && ScrollingPreferences.Instance().DoublePageOption.getValue()) {
                        myAnimationProvider = new TwoPageCurlAnimationProvider(myBitmapManager);
                        myAnimationProvider.setup(null, getMainAreaWidth() / 2, getMainAreaHeight());
                        GBLibrary.Instance().DoublePageOption.setValue(true);
                    } else {
                        myAnimationProvider = new PageCurlAnimationProvider(myBitmapManager);
                        GBLibrary.Instance().DoublePageOption.setValue(false);
                    }
                    break;
                case FLIP :
                    myAnimationProvider = new SlideAnimationProvider(myBitmapManager);
                    break;
                case FLIP_FRAME :
                    myAnimationProvider = new ShiftAnimationProvider(myBitmapManager);
                    break;
            }
        } else if (myAnimationType == PageEnum.Anim.CURL) {
            GBLibrary.Instance().DoublePageOption.setValue(isDouble);
        }
        return myAnimationProvider;
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        myAnimationProvider.computeScroll();
    }
    /**
     * 功能描述： 页面滚动中绘制方法<br>
     * 创建者： jack<br>
     * 创建日期：2013-4-26<br>
     *
     * @param canvas
     */
    private void onDrawInScrolling(Canvas canvas) {
        final GBView view = GBApplication.Instance().getCurrentView();

        // final int w = getMainAreaWidth();
        // final int h = getMainAreaHeight();

        final AnimationProvider animator = getAnimationProvider();
        final AnimationProvider.Mode oldMode = animator.getMode();
        animator.doStep();
        if (animator.inProgress()) {
            animator.draw(canvas);
            if (animator.getMode().Auto) {
                postInvalidate();
            }
            // drawFooter(canvas);
        } else {
            switch (oldMode) {
                case AnimatedScrollingForward : {
                    final PageEnum.PageIndex index = animator.getPageToScrollTo();
                    myBitmapManager.shift(index == PageIndex.NEXT);
                    view.onScrollEnd(index);
                    GBApplication.Instance().onRepaintFinished();
                    break;
                }
                case AnimatedScrollingBackward :
                    view.onScrollEnd(PageIndex.CURRENT);
                    break;
            }
            onDrawStatic(canvas);
        }
    }

    public void reset() {
        myBitmapManager.reset();
    }

    public void repaint() {
        postInvalidate();
    }

    @Override
    public void fingerScroll(int x, int y, DirectType direction) {
        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getMainAreaWidth(), getMainAreaHeight());
        animator.startManualScrolling(x, y);
    }
    /*
     * 页头尾提示
     */
    private enum PageTip {
        NONE, LAST, FRIST, LOADING, OUTOFRANGE// 超出试读范围
    }
    PageTip mPageTip = PageTip.NONE;
    @Override
    public void clickScroll(int x, int y) {
        final GBView view = GBApplication.Instance().getCurrentView();
        final AnimationProvider animator = getAnimationProvider();
        PageIndex index = animator.getPageToScrollTo(x, y);
        if (index == PageIndex.NEXT && view.isReadRangeEnd()) {
            mPageTip = PageTip.OUTOFRANGE;
        } else if (view.isCanScroll(index)) {
            mPageTip = PageTip.NONE;
            animator.scrollTo(x, y);
            postInvalidate();
        } else {
            if (view.isLoading())
                mPageTip = PageTip.LOADING;
            else
                switch (index) {
                    case NEXT :
                        mPageTip = PageTip.LAST;
                        break;
                    case PREVIOUS :
                        mPageTip = PageTip.FRIST;
                        break;
                    default :
                        mPageTip = PageTip.NONE;
                        break;
                }
        }
    }
    @Override
    public void startScroll(PageIndex pageIndex, int x, int y, DirectType direction, int speed) {
        final GBView view = GBApplication.Instance().getCurrentView();
        if (pageIndex == PageIndex.CURRENT || !view.isCanScroll(pageIndex)) {
            if (view.isLoading())
                mPageTip = PageTip.LOADING;
            else
                switch (pageIndex) {
                    case NEXT :
                        mPageTip = PageTip.LAST;
                        break;
                    case PREVIOUS :
                        mPageTip = PageTip.FRIST;
                        break;
                    default :
                        mPageTip = PageTip.NONE;
                        break;
                }
            return;
        } else if (pageIndex == PageIndex.NEXT && view.isReadRangeEnd()) {
            mPageTip = PageTip.OUTOFRANGE;
            return;
        }
        mPageTip = PageTip.NONE;

        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getMainAreaWidth(), getMainAreaHeight());
        animator.startAnimatedScrolling(pageIndex, x, y, speed);
        if (animator.getMode().Auto) {
            postInvalidate();
        }
    }

    @Override
    public void startScroll(PageIndex pageIndex, DirectType direction, int speed) {
        final GBView view = GBApplication.Instance().getCurrentView();
        if (pageIndex == PageIndex.CURRENT || !view.isCanScroll(pageIndex)) {
            if (view.isLoading())
                mPageTip = PageTip.LOADING;
            else
                switch (pageIndex) {
                    case NEXT :
                        mPageTip = PageTip.LAST;
                        break;
                    case PREVIOUS :
                        mPageTip = PageTip.FRIST;
                        break;
                    default :
                        mPageTip = PageTip.NONE;
                        break;
                }
            return;
        } else if (pageIndex == PageIndex.NEXT && view.isReadRangeEnd()) {
            mPageTip = PageTip.OUTOFRANGE;
            return;
        }
        mPageTip = PageTip.NONE;

        final AnimationProvider animator = getAnimationProvider();
        animator.setup(direction, getMainAreaWidth(), getMainAreaHeight());
        animator.startAnimatedScrolling(pageIndex, null, null, speed);
        if (animator.getMode().Auto) {
            postInvalidate();
        }
    }

    @Override
    public void startScroll(int x, int y, int speed) {
        final GBView view = GBApplication.Instance().getCurrentView();
        final AnimationProvider animator = getAnimationProvider();
        PageIndex index = animator.getPageToScrollTo(x, y);
        if (index == PageIndex.NEXT && view.isReadRangeEnd()) {
            mPageTip = PageTip.OUTOFRANGE;
            return;
        } else if (!view.isCanScroll(index)) {
            animator.terminate();
            if (view.isLoading())
                mPageTip = PageTip.LOADING;
            else
                switch (index) {
                    case NEXT :
                        mPageTip = PageTip.LAST;
                        break;
                    case PREVIOUS :
                        mPageTip = PageTip.FRIST;
                        break;
                    default :
                        mPageTip = PageTip.NONE;
                        break;
                }
            return;
        }
        mPageTip = PageTip.NONE;
        animator.startAnimatedScrolling(x, y, speed);
        postInvalidate();
    }
    /*
     * 在页面上绘制内容
     */
    void drawOnBitmap(Bitmap[] bitmap, PageEnum.PageIndex index) {
        final GBView view = GBApplication.Instance().getCurrentView();
        if (view == null) {
            return;
        }
        final boolean isDouble = GBLibrary.Instance().DoublePageOption.getValue();
        try {
            if (isDouble) {
                GBAndroidPaintContext context1 = new GBAndroidPaintContext(new Canvas(bitmap[0]),
                        getMainAreaWidth() / 2, getMainAreaHeight(), view.isScrollbarShow()
                        ? getVerticalScrollbarWidth()
                        : 0);
                GBAndroidPaintContext context2 = new GBAndroidPaintContext(new Canvas(bitmap[1]),
                        getMainAreaWidth() / 2, getMainAreaHeight(), view.isScrollbarShow()
                        ? getVerticalScrollbarWidth()
                        : 0);
                view.paint(context1, index);
                view.paintRight(context2, index);
            } else {

                GBAndroidPaintContext context = new GBAndroidPaintContext(new Canvas(bitmap[0]), getMainAreaWidth(),
                        getMainAreaHeight(), view.isScrollbarShow() ? getVerticalScrollbarWidth() : 0);
                view.paint(context, index);
                // view.paintLoading(context);
            }
        } catch (RuntimeException ex) {

        }
    }

    void drawLoading(Bitmap[] bitmap) {
        final GBView view = GBApplication.Instance().getCurrentView();
        if (view == null) {
            return;
        }
        final boolean isDouble = GBLibrary.Instance().DoublePageOption.getValue();
        if (isDouble) {
            GBAndroidPaintContext context1 = new GBAndroidPaintContext(new Canvas(bitmap[0]), getMainAreaWidth() / 2,
                    getMainAreaHeight(), view.isScrollbarShow() ? getVerticalScrollbarWidth() : 0);
            GBAndroidPaintContext context2 = new GBAndroidPaintContext(new Canvas(bitmap[1]), getMainAreaWidth() / 2,
                    getMainAreaHeight(), view.isScrollbarShow() ? getVerticalScrollbarWidth() : 0);
            view.paintLoading(context1);
            view.paintLoading(context2);
        } else {

            GBAndroidPaintContext context = new GBAndroidPaintContext(new Canvas(bitmap[0]), getMainAreaWidth(),
                    getMainAreaHeight(), view.isScrollbarShow() ? getVerticalScrollbarWidth() : 0);
            view.paintLoading(context);
        }
    }
    /**
     * 功能描述： 清除页面缓存，移除活动窗口<br>
     * 创建者： jack<br>
     * 创建日期：2013-12-3<br>
     */
    public void clearPageCache() {
        final GBView view = GBApplication.Instance().getCurrentView();
        GBApplication.Instance().hideActivePopup();
        if (view != null) {
            view.clearCaches();
        }
    }
    /*
     * 绘制页脚信息
     */
    // private void drawFooter(Canvas canvas) {
    // final GBView view = GBApplication.Instance().getCurrentView();
    // final GBView.FooterArea footer = view.getFooterArea();
    //
    // if (footer == null) {
    // myFooterBitmap = null;
    // return;
    // }
    //
    // if (myFooterBitmap != null &&
    // (myFooterBitmap.getMainAreaWidth() != getMainAreaWidth() ||
    // myFooterBitmap.getHeight() != footer.getHeight())) {
    // myFooterBitmap = null;
    // }
    // if (myFooterBitmap == null) {
    // myFooterBitmap = Bitmap.createBitmap(getMainAreaWidth(),
    // footer.getHeight(),
    // Bitmap.Config.RGB_565);
    // }
    // final GBAndroidPaintContext context = new GBAndroidPaintContext(
    // new Canvas(myFooterBitmap),
    // getMainAreaWidth(),
    // footer.getHeight(),
    // view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
    // );
    // footer.paint(context);
    // canvas.drawBitmap(myFooterBitmap, 0, getHeight() - footer.getHeight(),
    // myPaint);
    // }
    /**
     * 功能描述：无滚动时的绘制方法 <br>
     * 创建者： jack<br>
     * 创建日期：2013-5-3<br>
     *
     * @param canvas
     */
    private void onDrawStatic(final Canvas canvas) {
        final boolean isDouble = (getMainAreaWidth() > getMainAreaHeight())
                && ScrollingPreferences.Instance().DoublePageOption.getValue();
        myBitmapManager.setSize(isDouble ? getMainAreaWidth() / 2 : getMainAreaWidth(), getMainAreaHeight(), isDouble);
        Bitmap[] b = myBitmapManager.getBitmap(PageIndex.CURRENT);
        try {
            canvas.drawBitmap(b[0], 0, 0, myPaint);
            setResBitmap(b[0]);
        } catch (RuntimeException ex) {
            return;
        }
        if (isDouble) {
            canvas.drawBitmap(b[1], getMainAreaWidth() / 2, 0, myPaint);
            // 双翻页绘制书脊
            getAnimationProvider().setup(null, getMainAreaWidth(), getMainAreaHeight());
            getAnimationProvider().drawSpiner(canvas);
        }
        if (isResetPage) {
            // 设置页码信息
            ReaderApplication.Instance().runAction(ActionCode.RESET_PAGEINFO);
            isResetPage = false;
            return;
        }
        // 绘制页脚信息
        // drawFooter(canvas);
        // new Thread() {
        // @Override
        // public void run() {
        // final GBView view = GBApplication.Instance().getCurrentView();
        // final GBAndroidPaintContext context = new
        // GBAndroidPaintContext(canvas, getMainAreaWidth(),
        // getMainAreaHeight(), view.isScrollbarShow() ?
        // getVerticalScrollbarWidth() : 0);
        // view.initPage(context, PageIndex.NEXT);
        // isResetPage = true;
        // }
        // }.start();
    }

    // 是否刷新页码
    boolean isResetPage = true;

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 关闭语音
            if (((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().mIsSpeech) {
                ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().stopSpeech();
            }

            onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, null);
        } else {
            GBApplication.Instance().getCurrentView()
                    .onTrackballRotated((int) (10 * event.getX()), (int) (10 * event.getY()));
        }
        return true;
    }
    /*
     * 长按业务实现
     */
    private class LongClickRunnable implements Runnable {
        public void run() {
            if (performLongClick()) {
                myLongClickPerformed = true;
            }
        }
    }
    private volatile LongClickRunnable myPendingLongClickRunnable;
    // 是否长按状态
    private volatile boolean myLongClickPerformed;
    /*
     * 发送长按请求
     */
    private void postLongClickRunnable() {
        myLongClickPerformed = false;
        myPendingPress = false;
        if (myPendingLongClickRunnable == null) {
            myPendingLongClickRunnable = new LongClickRunnable();
        }
        postDelayed(myPendingLongClickRunnable, 2 * ViewConfiguration.getLongPressTimeout());
    }

    private class ShortClickRunnable implements Runnable {
        public void run() {
            final GBView view = GBApplication.Instance().getCurrentView();
            view.onSingleClick(myPressedX, myPressedY);
            myPendingPress = false;
            myPendingShortClickRunnable = null;
        }
    }
    private volatile ShortClickRunnable myPendingShortClickRunnable;

    private volatile boolean myPendingPress;
    private volatile boolean myPendingDoubleTap;
    private int myPressedX, myPressedY;
    private boolean myScreenIsTouched;
    /**
     * 响应屏幕触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 关闭菜单
        if (((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().getIsShowMenu()) {
            ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().showDisMenu();
            return false;
        }
        // 关闭语音
        if (((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().mIsSpeech) {
            ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().stopSpeech();
            return false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();
        final GBView view = GBApplication.Instance().getCurrentView();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP :
                super.onTouchEvent(event);
                if (myPendingDoubleTap) {
                    view.onDoubleClick(x, y);
                }
                if (myLongClickPerformed) {
                    view.onReleaseAfterLongPress(x, y);
                } else {
                    if (myPendingLongClickRunnable != null) {
                        removeCallbacks(myPendingLongClickRunnable);
                        myPendingLongClickRunnable = null;
                    }
                    if (myPendingPress) {
                        if (view.isDoubleClickSupported()) {
                            if (myPendingShortClickRunnable == null) {
                                myPendingShortClickRunnable = new ShortClickRunnable();
                            }
                            postDelayed(myPendingShortClickRunnable, ViewConfiguration.getDoubleTapTimeout());
                        } else {
                            view.onSingleClick(x, y);
                        }
                    } else {
                        view.onRelease(x, y);// FingerRelease(x, y);
                        // 页末提示
                        if (mPageTip != PageTip.NONE) {
                            final Activity activity = ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity();
                            if (mPageTip == PageTip.LOADING) {
                                UIUtil.showMessageText(activity, activity.getString(R.string.loading_tip));
                            } else if (mPageTip == PageTip.OUTOFRANGE) {
                                ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().showPayDialog();
                            } else {
                                // UIUtil.showMessageText(activity,
                                // activity.getString(mPageTip == PageTip.FRIST
                                // ? R.string.frist_page_tip
                                // : R.string.last_page_tip));
                                if (mPageTip == PageTip.LAST) {
                                    ((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().showRecommView();
                                } else {
                                    UIUtil.showMessageText(activity, activity.getString(R.string.frist_page_tip));
                                }
                                repaint();
                            }
                        }
                    }
                }
                myPendingDoubleTap = false;
                myPendingPress = false;
                myScreenIsTouched = false;

                break;
            case MotionEvent.ACTION_DOWN :
                if (myPendingShortClickRunnable != null) {
                    removeCallbacks(myPendingShortClickRunnable);
                    myPendingShortClickRunnable = null;
                    myPendingDoubleTap = true;
                } else {
                    postLongClickRunnable();
                    myPendingPress = true;
                }
                myScreenIsTouched = true;
                myPressedX = x;
                myPressedY = y;
                break;
            case MotionEvent.ACTION_MOVE : {
                final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                final boolean isAMove = Math.abs(myPressedX - x) > slop || Math.abs(myPressedY - y) > slop;
                if (isAMove) {
                    myPendingDoubleTap = false;
                }
                if (myLongClickPerformed) {
                    view.onMoveAfterLongPress(x, y);
                } else {
                    if (myPendingPress) {
                        if (isAMove) {
                            if (myPendingShortClickRunnable != null) {
                                removeCallbacks(myPendingShortClickRunnable);
                                myPendingShortClickRunnable = null;
                            }
                            if (myPendingLongClickRunnable != null) {
                                removeCallbacks(myPendingLongClickRunnable);
                            }
                            view.onPress(myPressedX, myPressedY);
                            myPendingPress = false;
                        }
                    }
                    if (!myPendingPress) {
                        view.onMove(x, y);
                    }
                }
                break;
            }
        }
        if (view.isTxtSelectModel())
            super.onTouchEvent(event);
        return true;
    }

    public boolean onLongClick(View v) {
        final GBView view = GBApplication.Instance().getCurrentView();
        return view.onLongPress(myPressedX, myPressedY);
    }

    private int myKeyUnderTracking = -1;
    private long myTrackingStartTime;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final GBApplication application = GBApplication.Instance();

        if (((GBAndroidLibrary) GBAndroidLibrary.Instance()).getActivity().mIsSpeech
                || GBApplication.Instance().getActivePopup() instanceof AudioPopup) {
            // ((GBAndroidLibrary)
            // GBAndroidLibrary.Instance()).getActivity().stopSpeech();
            return super.onKeyDown(keyCode, event);
        }

        if (application.hasActionForKey(keyCode, true) || application.hasActionForKey(keyCode, false)) {
            if (myKeyUnderTracking != -1) {
                if (myKeyUnderTracking == keyCode) {
                    return true;
                } else {
                    myKeyUnderTracking = -1;
                }
            }
            if (application.hasActionForKey(keyCode, true)) {
                myKeyUnderTracking = keyCode;
                myTrackingStartTime = System.currentTimeMillis();
                return true;
            } else {
                return application.runActionByKey(keyCode, false);
            }
        } else {
            return false;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (myKeyUnderTracking != -1) {
            if (myKeyUnderTracking == keyCode) {
                final boolean longPress = System.currentTimeMillis() > myTrackingStartTime
                        + ViewConfiguration.getLongPressTimeout();
                GBApplication.Instance().runActionByKey(keyCode, longPress);
            }
            myKeyUnderTracking = -1;
            return true;
        } else {
            final GBApplication application = GBApplication.Instance();
            return application.hasActionForKey(keyCode, false) || application.hasActionForKey(keyCode, true);
        }
    }

    protected int computeVerticalScrollExtent() {
        final GBView view = GBApplication.Instance().getCurrentView();
        if (!view.isScrollbarShow()) {
            return 0;
        }
        final AnimationProvider animator = getAnimationProvider();
        if (animator.inProgress()) {
            final int from = view.getScrollbarThumbLength(PageIndex.CURRENT);
            final int to = view.getScrollbarThumbLength(animator.getPageToScrollTo());
            final int percent = animator.getScrolledPercent();
            return (from * (100 - percent) + to * percent) / 100;
        } else {
            return view.getScrollbarThumbLength(PageIndex.CURRENT);
        }
    }

    protected int computeVerticalScrollOffset() {
        final GBView view = GBApplication.Instance().getCurrentView();
        if (!view.isScrollbarShow()) {
            return 0;
        }
        final AnimationProvider animator = getAnimationProvider();
        if (animator.inProgress()) {
            final int from = view.getScrollbarThumbPosition(PageIndex.CURRENT);
            final int to = view.getScrollbarThumbPosition(animator.getPageToScrollTo());
            final int percent = animator.getScrolledPercent();
            return (from * (100 - percent) + to * percent) / 100;
        } else {
            return view.getScrollbarThumbPosition(PageIndex.CURRENT);
        }
    }

    protected int computeVerticalScrollRange() {
        final GBView view = GBApplication.Instance().getCurrentView();
        if (!view.isScrollbarShow()) {
            return 0;
        }
        return view.getScrollbarFullSize();
    }
    /*
     * 获取显示高度
     */
    private int getMainAreaHeight() {
        // final GBView.FooterArea footer =
        // GBApplication.Instance().getCurrentView().getFooterArea();
        return getHeight();// footer != null ? getHeight() - footer.getHeight()
        // :
        // getHeight();
    }
    /*
     * 获取显示宽度
     */
    private int getMainAreaWidth() {
        // final boolean isDouble =
        // GBAndroidLibrary.Instance().DoublePageOption.getValue();
        // return isDouble ? getWidth() / 2 : getWidth();
        return getWidth();
        // return myAnimationProvider instanceof PageCurlAnimationProvider ?
        // getWidth() - 5 : getWidth();
    }

    public boolean isAnimationInPogress() {
        return myAnimationProvider != null && myAnimationProvider.inProgress();
    }

    @Override
    public void repaintOnThread(boolean isReset) {
        int i = 0;
        while (myAnimationProvider.inProgress()) {
            i++;
            if (i > 8) {
                myAnimationProvider.terminate();
            }
            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            continue;
        }
        // if (i == 0) {
        // try {
        // Thread.currentThread().sleep(1000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        handler.sendEmptyMessage(isReset ? 0 : 1);
    }

    Handler handler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            if (msg.what == 0)
                myBitmapManager.reset();
            isResetPage = true;
            invalidate();
            // postInvalidate();
        };
    };
    // 试读图书限制支持
    public void setmReadRange(int mReadRange) {
        GBApplication.Instance().getCurrentView().setReadRange(mReadRange);
    }

    @Override
    public void destory() {
        myBitmapManager.finalize();
    }
    // 扩展支持放大镜
}
