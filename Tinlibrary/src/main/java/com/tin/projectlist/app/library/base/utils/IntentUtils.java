package com.tin.projectlist.app.library.base.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;

public class IntentUtils {

    public static <T extends Parcelable> Parcelable getParcelableExtra(Intent intent, String name) {
        return intent == null ? null : intent.getParcelableExtra(name);
    }

    public static <T extends Parcelable> Parcelable getParcelableExtra(Bundle bundle, String name) {
        return bundle == null ? null : bundle.getParcelable(name);
    }

    public static <T extends Parcelable> ArrayList<Parcelable> getParcelableArrayList(Bundle bundle, String name) {
        return bundle == null ? null : bundle.getParcelableArrayList(name);
    }


    public static String getStringExtra(Intent intent, String name) {
        return intent == null ? null : intent.getStringExtra(name);
    }

    public static int getIntExtra(Intent intent, String name, int defaultValue) {
        return intent == null ? defaultValue : intent.getIntExtra(name, defaultValue);
    }

    public static boolean getBooleanExtra(Intent intent, String name, boolean defaultValue) {
        return intent == null ? defaultValue : intent.getBooleanExtra(name, defaultValue);
    }


    public static String getStringExtra(Bundle intent, String name) {
        return intent == null ? null : intent.getString(name);
    }

    public static String getStringExtra(Bundle intent, String name, String defaultValue) {
        return intent == null ? null : intent.getString(name, defaultValue);
    }

    public static int getIntExtra(Bundle intent, String name, int defaultValue) {
        return intent == null ? defaultValue : intent.getInt(name, defaultValue);
    }

    public static boolean getBooleanExtra(Bundle intent, String name, boolean defaultValue) {
        return intent == null ? defaultValue : intent.getBoolean(name, defaultValue);
    }

    public static void startActivity(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void startActivity(Application application, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        application.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(intent, requestCode);
    }

}
