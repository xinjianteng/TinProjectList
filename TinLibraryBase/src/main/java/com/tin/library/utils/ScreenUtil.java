package com.tin.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Byk on 2018/1/11.
 *
 * @author Byk
 */
public class ScreenUtil {

    public static final int SCREEN_TYPE_OTHER = -1;
    public static final int SCREEN_TYPE_HVGA = 0;
    public static final int SCREEN_TYPE_QVGA = 1;
    public static final int SCREEN_TYPE_WQVGA = 2;
    public static final int SCREEN_TYPE_WQVGA432 = 3;
    public static final int SCREEN_TYPE_WVGA = 4;
    public static final int SCREEN_TYPE_FWVGA = 5;
    public static final int SCREEN_TYPE_VGA = 6;
    public static final int SCREEN_TYPE_WSVGA_Tablet = 7;
    public static final int SCREEN_TYPE_WXGA_Tablet = 8;


    /**
     * 获取状态栏高度
     *
     * @param activity activity
     * @return 状态栏高度
     */
    public static int getStatusbarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow()
                .getDecorView()
                .getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }


    /**
     * 状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");    //使用反射获取实例
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取屏幕高度
     *
     * @param mContext mContext
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay()
                .getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param mContext mContext
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay()
                .getMetrics(dm);
        return dm.widthPixels;
    }


    /**
     * 获取手机屏幕类型
     *
     * @param act activity
     * @return 返回值对应类型-1:其它屏幕 0:HVGA 1:QVGA 2:WQVGA 400 3:WQVGA 432 4:WVGA 800
     * 5:WVGA 854 6:VGA
     */
    public static int getScreenType(Activity act) {
        int screenWidth, screenHeight;
        WindowManager windowManager = act.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        if (display.getWidth() > display.getHeight()) {
            screenHeight = display.getWidth();
            screenWidth = display.getHeight();
        } else {
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }
        if (screenHeight == 640 && screenWidth == 480)// VGA
        {
            return SCREEN_TYPE_VGA;
        } else if (screenHeight == 854 && screenWidth == 480)// WVGA 854
        {
            return SCREEN_TYPE_FWVGA;
        } else if (screenHeight == 800 && screenWidth == 480)// WVGA
        {
            return SCREEN_TYPE_WVGA;
        } else if (screenHeight == 432 && screenWidth == 240)// WQVGA 432
        {
            return SCREEN_TYPE_WQVGA432;
        } else if (screenHeight == 400 && screenWidth == 240)// WQVGA
        {
            return SCREEN_TYPE_WQVGA;
        } else if (screenHeight == 320 && screenWidth == 240)// qvga
        {
            return SCREEN_TYPE_QVGA;
        } else if (screenHeight == 480 && screenWidth == 320)// HVGA
        {
            return SCREEN_TYPE_HVGA;
        } else if (screenHeight == 600 && screenWidth == 1024) {
            return SCREEN_TYPE_WSVGA_Tablet;
        } else if (screenHeight == 800 && screenWidth == 1280) {
            return SCREEN_TYPE_WXGA_Tablet;
        }

        return SCREEN_TYPE_OTHER;
    }


    private static float getScreenDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay()
                .getMetrics(dm);
        return dm.density;
    }

    /**
     * screen全屏是否需要全屏
     *
     * @param screen Activity界面
     * @param enable true=全屏,false=非全屏
     */
    public static void fullScreen(Activity screen, boolean enable) {
        if (enable) {
            // go full screen
            WindowManager.LayoutParams attrs = screen.getWindow()
                    .getAttributes();
            // 添加全屏标志位到当前的flags
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            screen.getWindow()
                    .setAttributes(attrs);
            screen.getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            // go non-full screen
            WindowManager.LayoutParams attrs = screen.getWindow()
                    .getAttributes();
            // 从当前的flags清除全屏标志位
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            screen.getWindow()
                    .setAttributes(attrs);
            screen.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static int getSizeOfDip(Context mContext, int dipValue) {
        return (int) (dipValue * getScreenDensity(mContext) + 0.5);
    }

    public static int getSizeOfDip(Context mContext, float dipValue) {
        return (int) (dipValue * getScreenDensity(mContext) + 0.5);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device
     * density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on
     * device density
     */
    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent
     * pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(Context context, float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources()
                .getDisplayMetrics();
    }

    /**
     * 屏幕跟随系统亮度*
     *
     * @param activity activity
     * @return 返回系统亮度 0~255
     */
    public static int followSystemBrightness(Activity activity) {

        try {
            int screenBrightness = Settings.System.getInt(activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);

            Window localWindow = activity.getWindow();
            WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
            localLayoutParams.screenBrightness = screenBrightness;
            localWindow.setAttributes(localLayoutParams);

            return screenBrightness;

        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return -1;
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }


    public static int dp2Px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                .getDisplayMetrics());
    }

}
