package com.tin.projectlist.app.model.oldBook.common;

import android.app.ActivityManager;
import android.content.Context;

import com.fm.openinstall.OpenInstall;
import com.tin.projectlist.app.library.base.BaseApplication;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中的 Application 基类
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if (isMainProcess()) {
            OpenInstall.init(this);
        }
    }

    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }


}