package com.tin.projectlist.app.library.base.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * 这是一个Glide的加载图片的帮助类,用来把图片圆角
 * Created by Lizhanqi on 2016/9/16 0016.
 */
public class BlurRoundedCorner extends BlurTransformation {

    private int radius;

    public BlurRoundedCorner(int blurRadius, int sampling, int radius) {
        super(blurRadius, sampling);
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap transform = super.transform(context, pool, toTransform, outWidth, outHeight);
        Bitmap bitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        // 相当于清屏
        canvas.drawARGB(0, 0, 0, 0);
        //画圆角
        canvas.drawRoundRect(new RectF(0, 0, outWidth, outHeight), radius, radius, paint);
        // 取两层绘制，显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 把原生的图片放到这个画布上，使之带有画布的效果
        Matrix m = new Matrix();

        final int width = transform.getWidth();
        final int height = transform.getHeight();
        if (width != outWidth || height != outHeight) {
            final float sx = outWidth / (float) width;
            final float sy = outHeight / (float) height;
            m.setScale(sx, sy);
        }
        canvas.drawBitmap(transform, m, paint);
        return bitmap;
    }

    @Override
    public String key() {
        return "BlurRoundedCorner(radius=" + radius + ", sampling=" + radius + ")";
    }
}