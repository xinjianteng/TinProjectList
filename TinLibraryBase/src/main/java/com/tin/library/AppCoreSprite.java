package com.tin.library;

import android.app.Activity;
import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.tin.library.utils.AppSprite;
import com.tin.library.utils.ListUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @Author: chenhx
 * @Date: 2018/4/17 17:22
 * @Description:
 */
public class AppCoreSprite extends AppSprite {

    private static final int INTERVAL = 2_000;

    private static String sLastTips = "";
    private static long sLastTipsTime;
    private static List<WeakReference<AppCompatActivity>> activities = new ArrayList<>();

    private static List<WeakReference<AppCompatActivity>> destroyActivities = new ArrayList<>();
    public static Application getApp() {
        return INSTANCE;
    }

    public static void showTips(int resId, Object... formatArgs) {
        showTips(INSTANCE.getString(resId, formatArgs));
    }

    public static void showTips(int resId) {
        showTips(INSTANCE.getString(resId));
    }

    public static void showTips(String msg) {
        if (INSTANCE == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - sLastTipsTime < INTERVAL) {
            if (msg.equals(sLastTips)) {
                return;
            }
        }
        sLastTipsTime = now;
        sLastTips = msg;
        ToastUtil.showShort(INSTANCE, msg);
    }

    public static void addActivity(AppCompatActivity activity) {
        WeakReference<AppCompatActivity> weakReference = new WeakReference<>(activity);
        if (ListUtil.isListNull(activities)) {
            activities = new ArrayList<>();
        }
        activities.add(weakReference);
    }

    public static void removeActivity(AppCompatActivity activity) {
        List<WeakReference<AppCompatActivity>> removeList = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).get() == activity) {
                removeList.add(activities.get(i));
            }
        }
        activities.removeAll(removeList);
        Iterator<WeakReference<AppCompatActivity>> it = destroyActivities.iterator();
        while (it.hasNext()) {
            WeakReference<AppCompatActivity> next = it.next();
            if (next.get() == null) {
                it.remove();
            }
        }
        destroyActivities.add(new WeakReference<>(activity));
    }

    public static String getActivities() {
        Iterator<WeakReference<AppCompatActivity>> it = activities.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (it.hasNext()) {
            WeakReference<AppCompatActivity> next = it.next();
            AppCompatActivity appCompatActivity = next.get();
            if (appCompatActivity == null) {
                it.remove();
            } else {
                stringBuilder.append(appCompatActivity);
            }
        }
        return stringBuilder.toString();
    }


    public static String getLeakActivity() {
        Iterator<WeakReference<AppCompatActivity>> it = destroyActivities.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (it.hasNext()) {
            WeakReference<AppCompatActivity> next = it.next();
            AppCompatActivity appCompatActivity = next.get();
            if (appCompatActivity == null) {
                it.remove();
            } else {
                stringBuilder.append(appCompatActivity);
            }
        }
        return stringBuilder.toString();
    }

    public static void removeActivity(String activityName) {
        List<WeakReference<AppCompatActivity>> removeList = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).get().getClass().getSimpleName().contains(activityName)) {
                removeList.add(activities.get(i));
                activities.get(i).get().finish();
            }
        }
        activities.removeAll(removeList);
    }

    public static AppCompatActivity getTopActivity() {
        if (ListUtil.isListNull(activities)) {
            return null;
        }
        return activities.get(activities.size() - 1).get();
    }


    public static boolean hasActivity(String activityName) {
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i).get();
            if (activity != null && activityName.contains(activity.getClass().getName()) && !activity.isFinishing()) {
                return true;
            }
        }
        return false;
    }

    public static void exit() {
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).get().finish();
        }
        activities.clear();
    }

    public static void exit(Class clazz) {
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i).get();
            if (activity.getClass().equals(clazz)) {
                continue;
            } else {
                activity.finish();
            }
        }
    }

    public static void exit(Class clazz1, Class clazz2) {
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i).get();
            if (activity.getClass().equals(clazz1) || activity.getClass().equals(clazz2)) {
                continue;
            } else {
                activity.finish();
            }
        }
    }
}

