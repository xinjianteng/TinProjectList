package com.tin.projectlist.app.model.oldBook.utils;

import android.content.Context;


import com.tin.projectlist.app.library.base.utils.SharePreferenceUtil;

import java.util.Date;

/**
 * @package : com.cliff.utils
 * @description : sp 工具类
 * Created by chenhx on 2018/3/26 16:02.
 */

public class SpUtil {

    private static final String TAG = SpUtil.class.getSimpleName();

    private static final String GATHER_DYNASTY = "gather_dynasty";


    public static SharePreferenceUtil build(Context context) {
        return SharePreferenceUtil.getInstance(context, TAG);
    }

    public static void clear(Context context) {
        build(context).clear();
    }


    public static void addOnSharedChangeListener(Context context, String key, SharePreferenceUtil.IOnSharedChangeListener listener) {
        build(context).addOnSharedPreferenceChangListener(key, listener);
    }


    /***
     * 获取用户信息
     * @param context
     * @param user
     */
    public static void setUser(Context context, String user) {
        build(context).putString(GATHER_DYNASTY, user, true);
    }

    public static String getUser(Context context) {
        return build(context).getString(GATHER_DYNASTY, "");
    }



}


