package com.tin.library.utils;

import android.app.Application;

/**
 * @Author: chenhx
 * @Date: 2018/4/17 17:23
 * @Description:
 */
public class AppSprite {

    private static final String TAG = AppSprite.class.getSimpleName();

    public static final Application INSTANCE;

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals")
                    .getMethod("getInitialApplication")
                    .invoke(null);
            if (app == null) {
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
            }
        } catch (final Exception e) {
            LogUtils.e(TAG, "Failed to get current application from AppGlobals." + e.getMessage());
            try {
                app = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null);
            } catch (final Exception ex) {
                LogUtils.e(TAG, "Failed to get current application from ActivityThread." + e.getMessage());
            }
        } finally {
            INSTANCE = app;
        }
    }
}
