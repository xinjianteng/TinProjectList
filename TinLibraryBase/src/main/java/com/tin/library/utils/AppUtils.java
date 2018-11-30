package com.tin.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2016/11/9.
 */

public class AppUtils {


    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode(Context mContext) {
        if (mContext == null)
            return 0;
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取版本名
     * @return
     */
    public static String getVersionName(Context mContext) {
        if (mContext == null)
            return "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName + "";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0.0";
        }
    }


}
