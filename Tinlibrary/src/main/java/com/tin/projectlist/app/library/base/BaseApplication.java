package com.tin.projectlist.app.library.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;


import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.base.widget.toast.ToastUtils;


/**
 *    author : xinjianteng
 *    time   : 2020/02/03
 *    desc   : 项目中的 Application 基类
 */
public class BaseApplication extends Application {
    private static final String TAG = BaseApplication.class.getSimpleName();
    private static Context sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initSDK(this);
    }

    /**
     * 初始化一些第三方框架
     */
    public static void initSDK(Application application) {
        LogUtils.d(TAG, "BaseApplication attachBaseContext");
        // 初始化吐司工具类
        ToastUtils.init(application);


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 使用 Dex分包
        MultiDex.install(this);
    }

    public static Context getContext(){
        return sInstance;
    }


    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public static boolean isAppMainProcess(Context context, String mainProcess) {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(context, pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else return mainProcess.equalsIgnoreCase(process);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

}