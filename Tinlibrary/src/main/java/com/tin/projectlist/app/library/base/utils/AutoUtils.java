package com.tin.projectlist.app.library.base.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


public class AutoUtils {


    public final static int BASE_WIDTH = 360;
    public final static int BASE_HEIGHT = 640;
    public final static float BASE_DISPLAY = 3;


    /***
     * 屏幕宽高
     */
    public static final float SCREEN_WIDTH = 1080;
    public static final float SCREEN_HEIGHT = 1920;

    public final static int INDEX_BASE_WIDTH = 0;
    public final static int INDEX_BASE_HEIGHT = 1;

    public static boolean getIsBaseOnWidth(Context context) {
        float base = SCREEN_HEIGHT / SCREEN_WIDTH;
        float real = (float) ScreenUtil.getScreenHeight(context) / ScreenUtil.getScreenWidth(context);
        return real > base;
    }


    /***
     * 适配宽度
     * @param view
     * @param dp
     */
    public static void autoWidth(View view, float dp) {
        autoWidth(view, dp, INDEX_BASE_WIDTH);
    }


    /***
     * 适配高度
     * @param view
     * @param dp
     */
    public static void autoHeight(View view, float dp) {
        autoHeight(view, dp, INDEX_BASE_WIDTH);
    }


    /***
     * 适配宽度
     * @param view
     * @param dp
     * @param base
     */
    public static void autoWidth(View view, float dp, int base) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        switch (base) {
            case INDEX_BASE_WIDTH:
                lp.width = getBaseOnWidth(view.getContext(), dp);
                break;
            case INDEX_BASE_HEIGHT:
                lp.width = getBaseOnHeight(view.getContext(), dp);
                break;
        }
        view.setLayoutParams(lp);
    }


    /***
     * 适配高度
     * @param view
     * @param dp
     * @param base
     */
    public static void autoHeight(View view, float dp, int base) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        switch (base) {
            case INDEX_BASE_WIDTH:
                lp.height = getBaseOnWidth(view.getContext(), dp);
                break;
            case INDEX_BASE_HEIGHT:
                lp.height = getBaseOnHeight(view.getContext(), dp);
                break;
        }
        view.setLayoutParams(lp);
    }



    /***
     *
     * @param dp
     * @param base
     * @return
     */
    public static int getBase(float dp, int base) {
        int realDp = 0;
        switch (base) {
            case INDEX_BASE_WIDTH:
                realDp = getBaseOnWidth(AppSprite.INSTANCE, dp);
                break;
            case INDEX_BASE_HEIGHT:
                realDp = getBaseOnHeight(AppSprite.INSTANCE, dp);
                break;
        }
        return realDp;

    }


    /***
     * 根据宽度 获取
     * @param context
     * @param dp
     * @return
     */
    public static int getBaseOnWidth(Context context, float dp) {
        return (int) (ScreenUtil.getScreenWidth(context) / SCREEN_WIDTH * (dp * BASE_DISPLAY + 0.5f));
    }


    /***
     * 根据高度获取
     * @param context
     * @param dp
     * @return
     */
    public static int getBaseOnHeight(Context context, float dp) {
        return (int) (ScreenUtil.getScreenHeight(context) / SCREEN_HEIGHT * (dp * BASE_DISPLAY + 0.5f));
    }

}
