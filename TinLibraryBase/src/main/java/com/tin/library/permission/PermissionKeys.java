package com.tin.library.permission;

/**
 * @package : com.cliff.libs.base.permission
 * @description :
 * Created by chenhx on 2018/4/12 10:58.
 */

public class PermissionKeys {


    //简单的权限
    public static final String WIFI_STATE = "android.permission.ACCESS_WIFI_STATE";
    public static final String ACCESS_NETWORK_STATE = "android.permission.ACCESS_NETWORK_STATE";


    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String WRITE_READ_PHONE = "android.permission.READ_PHONE_STATE";
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    public static final String CAMERA = "android.permission.CAMERA";
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String READ_LOGS = "android.permission.READ_LOGS";
    public static final String SET_DEBUG_APP = "android.permission.SET_DEBUG_APP";
    public static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";
    public static final String GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";
    public static final String WRITE_APN_SETTINGS = "android.permission.WRITE_APN_SETTINGS";


    public static final class Group {

        public static final String[] STORAGE = new String[]{
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE};


        public static final String[] CAMERAS = new String[]{
                CAMERA,
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
        };

        public static final String[] NEEDED = new String[]{
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE,
                WRITE_READ_PHONE,
                WIFI_STATE};

        public static final String[] UMENG = new String[]{
                ACCESS_FINE_LOCATION,
                CALL_PHONE,
                READ_LOGS,
                SET_DEBUG_APP,
                SYSTEM_ALERT_WINDOW,
                GET_ACCOUNTS,
                WRITE_APN_SETTINGS};
    }

}
