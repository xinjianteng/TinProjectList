package com.tin.projectlist.app.library.reader.parser.log;

import android.util.Log;

/**
 * 类名： L.java<br>
 * 描述： 日志工具类<br>
 * 创建者： jack<br>
 * 创建日期：2012-11-13<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class L {

    public static boolean FLAG = true;

    public static void d(String tag, String msg) {
        if (FLAG)
            Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (FLAG)
            Log.d(tag, msg, tr);
    }

    public static void v(String tag, String msg) {
        if (FLAG)
            Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (FLAG)
            Log.v(tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        if (FLAG)
            Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (FLAG)
            Log.i(tag, msg, tr);
    }

    public static void i(String msg) {
        if (FLAG)
            Log.i("L", msg);
    }

    public static void e(String tag, String msg) {
        if (FLAG)
            Log.e(tag, msg);
    }
}
