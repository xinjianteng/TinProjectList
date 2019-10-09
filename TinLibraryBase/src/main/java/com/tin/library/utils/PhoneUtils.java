package com.tin.library.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * PhoneUtils 终端工具
 * @author T_xin
 * @date 2015-05-13
 * @version 1.0
 */
public class PhoneUtils {

    /***
     * DeviceId
        每台设备都有唯一的标识，这就是DeviceId
     * @param mContext  上下文
     * @return
     */
    public static String getDeviceId(Context mContext) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId=manager.getDeviceId();
            return TextUtils.isEmpty(deviceId)?"":deviceId;
        }else {
            return "";
        }
    }


    /***
     * 系统序列号
     * @param mContext  上下文
     * @return
     */
    public static String getAndroidId(Context mContext){
        try {
            return Settings.System.getString(mContext
                            .getApplicationContext().getContentResolver(),
                    Settings.System.ANDROID_ID);
        }catch(Exception e){
            return "";
        }
    }


    /***
     * 系统序列号
     * @param mContext  上下文
     * @return
     */
    public static String getAndroidVersion(Context mContext){
        try {
            return "android"+android.os.Build.VERSION.RELEASE;
        }catch(Exception e){
            return "";
        }
    }

    /**
     * 设备唯一码
     *
     * @param
     * @return PhoneSN
     */
    public static String getPhoneSN(Context context) {
        try {
            return getDeviceId(context)+ getAndroidId(context);//
        }catch (Exception e){
            return "";
        }
    }






}
