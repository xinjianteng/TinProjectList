package com.tin.projectlist.app.library.reader.parser.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tin.projectlist.app.library.base.utils.SharePreferenceUtil;

/**
 * 描述：Cookie<br>
 * 创建者： T_xin<br>
 * 创建日期：2020-01-02<br>
 */

public class Cookie {
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	public static final String APP_CFG = "appConfig";
	public static final String USER_DATA = "initUserData";
	Context context;

	public Cookie(Context c, String name) {
		context = c;
		sp = context.getSharedPreferences(name, 0);
		editor = sp.edit();
	}

	public void putVal(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void putVal(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putVal(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void putVal(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public void putVal(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}
	public long getLong(String key) {
		return sp.getLong(key, 0L);
	}


	public float getFloat(String key) {
		return sp.getFloat(key, 0F);
	}

	public String getVal(String key) {
		return sp.getString(key, null);
	}

	public String getVal(String key, String defaultVal) {
		return sp.getString(key, defaultVal);
	}

	public int getInt(String key) {
		return sp.getInt(key, 0);
	}

	public void putBool(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public Boolean getBool(String key) {
		return sp.getBoolean(key, false);
	}

	public Boolean getBool(String key, boolean defaultVal) {
		return sp.getBoolean(key, defaultVal);
	}

}
