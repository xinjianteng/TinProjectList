package com.tin.projectlist.app.library.base.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;

/**
 * @package : com.cliff.libs.util
 * @description : json 解析类
 * Created by chenhx on 2018/4/2 17:38.
 */

public class JsonUtil {


    public static <T> T fromJson(String memberPermission, Class<T> clazz) {
        return new Gson().fromJson(memberPermission, clazz);
    }


    public static <T> T fromJsonAndCatchException(String memberPermission, Class<T> clazz) {
        try {
            return new Gson().fromJson(memberPermission, clazz);
        } catch (Exception e) {
        }
        return null;
    }


    public static <T> T fromJson(String memberPermission, Type type) {
        return new Gson().fromJson(memberPermission, type);
    }


    public static String toJson(Object obj) {
        return new Gson().toJson(obj);
    }

    public static int getIntValue(String jsonStr, String key) {
        if (TextUtils.isEmpty(jsonStr)) {
            return -1;
        }

        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsInt();
        }
        return -1;

    }



    public static long getLongValue(String jsonStr, String key) {
        if (TextUtils.isEmpty(jsonStr)) {
            return -1;
        }

        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsLong();
        }
        return -1;

    }


    public static String getJsonValue(String jsonStr, String key) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsString();
        }
        return null;
    }


    public static JsonObject getJsonObjectValue(String jsonStr, String key) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
        if (jsonObject.has(key)) {
            return jsonObject.get(key).getAsJsonObject();
        }
        return null;
    }
}
