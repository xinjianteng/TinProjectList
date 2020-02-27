package com.tin.projectlist.app.library.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class AppActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof AppCompatActivity) {
            AppCoreSprite.addActivity((AppCompatActivity) activity);
        }

        Log.e("activity", activity.getClass().getName());
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
//        UmEventUtils.onPageStart(activity.getClass().getSimpleName());
//        UmEventUtils.onResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
//        UmEventUtils.onPageEnd(activity.getClass().getSimpleName());
//        UmEventUtils.onPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        EventBusMgr.ungister(activity);
//        RefWatcherUtils.startWatcher(activity);
        if (activity instanceof AppCompatActivity) {
            AppCoreSprite.removeActivity((AppCompatActivity) activity);
        }
    }
}
