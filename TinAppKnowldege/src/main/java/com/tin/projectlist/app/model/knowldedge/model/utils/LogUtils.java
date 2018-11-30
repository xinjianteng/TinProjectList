package com.tin.projectlist.app.model.knowldedge.model.utils;


import android.util.Log;


/**
 *  日记输出打印
 * @author T_xin
 * @date 2015-05-13
 * @version 1.0
 */
public class LogUtils {




	    public static final String TAG = "cliff";
	    
	    public static boolean isDebug=true;

	    // 下面四个是默认tag的函数
	    public static void i(String msg)
	    {
	        if (isDebug)
				Log.i(TAG,msg);
	    }

	    public static void d(String msg)
	    {
	        if (isDebug)
				Log.d(TAG,msg);
	    }

	    public static void e(String msg)
	    {
	        if (isDebug)
				Log.e(TAG,msg);
	    }

	    public static void v(String msg)
	    {
	        if (isDebug)
				Log.v(TAG,msg);
	    }

	    // 下面是传入自定义tag的函数
	    public static void i(String tag, String msg)
	    {
	        if (isDebug)
	            Log.i(tag,msg);
	    }

	    public static void d(String tag, String msg)
	    {
	        if (isDebug)
	            Log.d(tag, msg);
	    }

	    public static void e(String tag, String msg)
	    {
	        if (isDebug)
	            Log.e(tag, msg);
	    }

	    public static void v(String tag, String msg)
	    {
	        if (isDebug)
	            Log.v(tag,msg);
	    }


}
