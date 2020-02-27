package com.tin.projectlist.app.model.av.api;

import android.content.Context;
import android.os.Build;


import com.tin.projectlist.app.library.base.frame.BaseConstant;
import com.tin.projectlist.app.library.base.frame.http.model.ApiParams;

/**
 * @package : com.cliff.geebook.api
 * @description :
 * Created by chenhx on 2018/4/17 17:19.
 */

public class ApiParamsUtil {

    private static final String PAGE_NUM = "pageNo";
    public static final String PAGE_SIZE = "pageSize";
    public static final int pageSize = BaseConstant.PAGE_SIZE;

    public static ApiParams getApiParams(Context context) {
        ApiParams apiParams = new ApiParams();
        apiParams.put("appId", "1");
        apiParams.put("platformType", 2 + "");
        apiParams.put("terminalType", "1");//终端类型，例如：终端类型(1Android手机、2Android平板、3PC、4IOS手机、5IOS平板、6WEB)
        apiParams.put("terminalModel", Build.MODEL);//手机型号
        apiParams.put("terminalFactory", Build.MANUFACTURER);//终端厂商
        apiParams.put("terminalSn", PhoneUtils.getPhoneSN(context));//终端唯一
        apiParams.put("terminalName", Build.MANUFACTURER + " " + Build.MODEL);//终端名称
        apiParams.put("versionCode", AppVersionUtil.getVerCode(context, context.getPackageName()));//版本号
        apiParams.put("versionName", AppVersionUtil.getVersionName(context) + "");
        return apiParams;
    }

    public static ApiParams getApiLoginParams(Context context) {
        ApiParams apiParams = getApiParams(context);

        apiParams.put("ip", PhoneUtils.getIpAddress(context));//登录ip
        apiParams.put("remark", "android登陆");//登录说明
        apiParams.put("loginSource", 1);//登录来源:1藏书馆（默认1）,2借阅外放，3书城',
        apiParams.put("channelId", Integer.valueOf(WalleMgr.getAppChannelId(context)));//来源id

        return apiParams;
    }


    /***
     * 获取需要登录信息的params
     * @return
     */
    public static ApiParams getUserInfoParams(Context context) {
        ApiParams apiParams = getApiParams(context);
        return apiParams;
    }


    /****
     * 获取分页加载数据
     * @param context
     * @param pageNum
     * @return
     */
    public static ApiParams getPageParams(Context context, int pageNum) {
        ApiParams apiParams = getUserInfoParams(context);
        apiParams.put(ApiParamsUtil.PAGE_NUM, pageNum);
        apiParams.put(ApiParamsUtil.PAGE_SIZE, ApiParamsUtil.pageSize);
        return apiParams;
    }

    /****
     * 获取分页加载数据
     * @param context
     * @param pageEntity
     * @return
     */
    public static ApiParams getPageParams(Context context, PageEntity pageEntity) {
        ApiParams apiParams = getUserInfoParams(context);
        apiParams.put(ApiParamsUtil.PAGE_NUM, pageEntity.getPageNum());
        apiParams.put(ApiParamsUtil.PAGE_SIZE, pageEntity.getPageSize());
        return apiParams;
    }

    /****
     * 获取分页加载数据
     * @param context
     * @param pageNum
     * @return
     */
    public static ApiParams getPageParams(Context context, int pageNum, int pageSize) {
        ApiParams apiParams = getUserInfoParams(context);
        apiParams.put(ApiParamsUtil.PAGE_NUM, pageNum);
        apiParams.put(ApiParamsUtil.PAGE_SIZE, pageSize);
        return apiParams;
    }


}
