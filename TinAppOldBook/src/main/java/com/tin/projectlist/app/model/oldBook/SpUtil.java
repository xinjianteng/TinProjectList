package com.tin.projectlist.app.model.oldBook;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 2019/9/24
 * author : chx
 * description :
 */
public class SpUtil {
    public static volatile SpUtil instance;
    private final SharedPreferences mSharePreference;
    private static final String SP_USER = "user";
    private static final String SP_SEARCH_HISTORY = "search_history";
    private static final String SP_XINGE_TOKEN = "xingeToken";


    public static SpUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SpUtil.class) {
                if (instance == null) {
                    instance = new SpUtil(context);
                }
            }
        }
        return instance;

    }

    private SpUtil(Context context) {
        mSharePreference = context.getSharedPreferences("sp_app", Context.MODE_PRIVATE);
    }

    /***
     * 获取用户信息
     * @param user
     */
    public void setUser(String user) {
        mSharePreference.edit().putString(SP_USER, user).apply();
    }

    public String getUser() {
        return mSharePreference.getString(SP_USER, "");
    }

    public void setSearchHistory(String history) {
        mSharePreference.edit().putString(SP_SEARCH_HISTORY, history).apply();
    }

    public String getSearchHistory() {
        return mSharePreference.getString(SP_SEARCH_HISTORY, "");
    }

    public void setXingeToken(String xingeToken){
        mSharePreference.edit().putString(SP_XINGE_TOKEN,xingeToken).apply();

    }

    public String getXingeToken(){
        return mSharePreference.getString(SP_XINGE_TOKEN,"");
    }
}
