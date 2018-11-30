package com.tin.projectlist.app.model.knowldedge.model.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

public class SnackbarUtils {

    private static Toast toast;

    private static Snackbar snackbar;


    public static void show(View view,String content){
        if(snackbar==null){
            snackbar=Snackbar.make(view,content,Snackbar.LENGTH_SHORT);
        }else {
            snackbar.setText(content);
        }
        snackbar.show();
    }




}
