package com.tin.projectlist.app.model.oldBook.common;

import android.app.ActivityManager;
import android.content.Context;

import com.tin.projectlist.app.library.base.BaseApplication;
import com.tin.projectlist.app.model.oldBook.KeyConstant;

import cn.bmob.v3.Bmob;

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
            //提供以下两种方式进行初始化操作：
            //第一：默认初始化
            Bmob.initialize(this, KeyConstant.BMOB_APPLICATION_ID);
            // 注:自v3.5.2开始，数据sdk内部缝合了统计sdk，开发者无需额外集成，传渠道参数即可，不传默认没开启数据统计功能
            //Bmob.initialize(this, "Your Application ID","bmob");

            //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
            //BmobConfig config =new BmobConfig.Builder(this)
            ////设置appkey
            //.setApplicationId("Your Application ID")
            ////请求超时时间（单位为秒）：默认15s
            //.setConnectTimeout(30)
            ////文件分片上传时每片的大小（单位字节），默认512*1024
            //.setUploadBlockSize(1024*1024)
            ////文件的过期时间(单位为秒)：默认1800s
            //.setFileExpiration(2500)
            //.build();
            //Bmob.initialize(config);
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