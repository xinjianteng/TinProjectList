package com.tin.projectlist.app.model.oldBook.utils;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.utils.LogUtils;

public class TextViewUtils {


    public static void setText(TextView tv, String str) {
        if(tv==null){
            LogUtils.d("bookName为空");
        }else if(TextUtils.isEmpty(str)){
            LogUtils.d("book_name");
        }else {
            tv.setText(str);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setHtmlText(TextView tv, String str) {
        if(tv==null){
            LogUtils.d("bookName为空");
        }else if(TextUtils.isEmpty(str)){
            LogUtils.d("book_name");
        }else {
            tv.setText(Html.fromHtml(str,Html.FROM_HTML_MODE_COMPACT));
        }
    }

}
