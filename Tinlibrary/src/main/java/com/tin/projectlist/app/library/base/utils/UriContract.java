package com.tin.projectlist.app.library.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tin.projectlist.app.library.base.R;
import com.tin.projectlist.app.library.base.frame.constants.ParcelableKey;

import java.net.MalformedURLException;
import java.net.URL;

public class UriContract {

    /***
     * url 相应跳转
     * @param context
     */
    public static void startActivityUri(Context context, Uri uri) {
        LogUtils.e("uri:   " + uri.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage(context.getPackageName());
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            IntentUtils.startActivity(context, intent);
        }

    }


}
