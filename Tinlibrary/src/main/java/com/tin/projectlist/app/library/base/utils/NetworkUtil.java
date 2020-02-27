package com.tin.projectlist.app.library.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @package : com.cliff.libs.util
 * @description :
 * Created by chenhx on 2018/4/8 11:41.
 */

public class NetworkUtil {

    /**
     * 判断当前是否连接网络
     * 需要添加获取网络状态
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 判断当前是否具有wifi连接
     *
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 判断当前是否具有移动数据网络连接
     *
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (networkInfo != null && networkInfo.isConnected());
    }
}
