package com.tin.projectlist.app.library.base.frame.permission;

import android.Manifest;

/**
 * @package : com.cliff.libs.base.permission
 * @description :
 * Created by chenhx on 2018/4/12 10:58.
 */

public class PermissionKeys {


    //简单的权限
    public static final String ACCESS_WIFI_STATE = Manifest.permission.ACCESS_WIFI_STATE;

    public static final String WRITE_EXTERNAL_STORAGE =  Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static final String WRITE_READ_PHONE = Manifest.permission.READ_PHONE_STATE;

    public static final String CAMERA = Manifest.permission.CAMERA;


    public static final class Group {

        public static final String[] CAMERAS = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};

    }

}
