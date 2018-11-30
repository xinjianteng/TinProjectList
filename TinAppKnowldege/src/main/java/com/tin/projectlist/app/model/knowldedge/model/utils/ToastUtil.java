package com.tin.projectlist.app.model.knowldedge.model.utils;

import android.content.Context;
import android.widget.Toast;



public class ToastUtil {

    private static Toast toast;

    /**
     * 显示Toast
     * @param context 上下文
     * @param content 要显示的内容
     */
    public static void show(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    /**
     * 显示Toast
     * @param context 上下文
     * @param resId 要显示的资源id
     */
    public static void show(Context context, int resId) {
        show(context, (String) context.getResources().getText(resId));
    }



}