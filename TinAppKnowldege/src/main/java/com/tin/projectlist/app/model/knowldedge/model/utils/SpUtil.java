package com.tin.projectlist.app.model.knowldedge.model.utils;

import android.content.Context;


/**
 * @package : com.cliff.utils
 * @description : sp 工具类
 * Created by chenhx on 2018/3/26 16:02.
 */

public class SpUtil {

    private static final String TAG = SpUtil.class.getSimpleName();

    //s用来保存本地用户登录信息
    private static final String SUBJECTLIST = "subjectBean";


    public static SharePreferenceUtil build(Context context) {
        return SharePreferenceUtil.getInstance(context, TAG);
    }

    public static void addOnSharedChangeListener(Context context, String key, SharePreferenceUtil.IOnSharedChangeListener listener) {
        build(context).addOnSharedPreferenceChangListener(key, listener);
    }

    /**
     * @Author: chenhx
     * @Date: 2018/3/26 16:45
     * @Description:用来保存本地用户登录信息
     */
    public static void setSubjectlist(Context context, String value) {
        build(context).putString(SUBJECTLIST, value, true);

    }

    public static String getSubjectlist(Context context) {
        return build(context).getString(SUBJECTLIST, "");
    }

}
