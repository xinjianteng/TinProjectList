package com.tin.projectlist.app.library.base.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {


    /**
     * 隐藏软键盘
     */
    public static void hideSoftKeyboard(Activity activity) {
        // 隐藏软键盘，避免软键盘引发的内存泄露
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }




}
