package com.tin.projectlist.app.library.base.utils.image;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * 这是一个Glide的加载图片的帮助类,用来把图片圆角
 * Created by Lizhanqi on 2016/9/16 0016.
 */
public class GlideRoundTransform extends BitmapTransformation {

    private int radius;


    public GlideRoundTransform(int dp) {
        radius = dp;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtils.roundedCorners(pool, toTransform, radius);
    }



    public String getId() {
        return getClass().getName() + Math.round(radius);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}