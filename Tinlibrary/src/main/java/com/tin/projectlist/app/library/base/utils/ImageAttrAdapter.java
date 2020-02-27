package com.tin.projectlist.app.library.base.utils;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tin.projectlist.app.library.base.R;
import com.tin.projectlist.app.library.base.utils.image.BlurRoundedCorner;
import com.tin.projectlist.app.library.base.utils.image.BlurTransformation;
import com.tin.projectlist.app.library.base.utils.image.GlideApp;
import com.tin.projectlist.app.library.base.utils.image.GlideMgr;
import com.tin.projectlist.app.library.base.utils.image.GlideRoundTransform;


public class ImageAttrAdapter {

    private static final String TAG = ImageAttrAdapter.class.getSimpleName();

    private static boolean isFinishingWhenIsActivity(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context).isFinishing();
        }
        return false;
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter({"android:src", "default"})
    public static void setSrcDefault(ImageView view, String path, Drawable defaultBitmap) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            GlideApp.with(view.getContext())
                    .load(path)
                    .centerCrop()
                    .placeholder(defaultBitmap)
                    .error(defaultBitmap)
                    .skipMemoryCache(false)
                    .dontAnimate()
                    .into(view);

        }
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    @BindingAdapter("android:background")
    public static void setBackground(ImageView view, @ColorInt int color) {
        view.setBackgroundColor(color);
    }

    @BindingAdapter({"android:src", "radius"})
    public static void setSrc(ImageView view, String path, float radius) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            GlideMgr.loadRoundImage(view.getContext(), path, (int) radius, view);
        }
    }

    @BindingAdapter({"android:src"})
    public static void setSrc(ImageView view, String path) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            CommonUtil.loadImage(view, path, R.drawable.default_img_bitmap, R.drawable.default_img_bitmap);
        }
    }


    @BindingAdapter({"src", "radius", "sampling"})
    public static void setSrc(ImageView view, String path, float radius, int sampling) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            CommonUtil.loadImage(view, path, new BlurRoundedCorner(5, sampling, (int) radius));
        }
    }

    @BindingAdapter({"src", "sampling"})
    public static void setBlurSrc(ImageView view, String path, int sampling) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            CommonUtil.loadImage(view, path, new BlurTransformation(view.getContext(), 5, sampling));
        }
    }


    @BindingAdapter({"android:src", "radius"})
    public static void setSrc(ImageView view, String path, int radius) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            CommonUtil.loadImage(view, path, R.drawable.default_img_bitmap, R.drawable.default_img_bitmap, radius);
        }
    }


    @BindingAdapter({"bookImage", "radius"})
    public static void setBookImage5(ImageView view, String imageUrl, float radius) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            CommonUtil.loadBookImage(view, imageUrl, radius);
        }
    }


    @BindingAdapter("photo")
    public static void setCircleSrc(ImageView view, String photo) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            CommonUtil.loadHeadCircleImage(view, photo);
        }
    }

    @BindingAdapter("app:background")
    public static void setBackground(View view, @DrawableRes int resId) {
        GlideApp.with(view.getContext())
                .load(resId)
                .into(new CustomViewTarget<View, Drawable>(view) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {

                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }

                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {

                    }
                });
    }



    @BindingAdapter({"android:src", "default", "radius"})
    public static void setSrcDefault(ImageView view, String path, Drawable defaultBitmap, float radius) {
        if (!isFinishingWhenIsActivity(view.getContext())) {
            GlideApp.with(view.getContext().getApplicationContext())
                    .load(path)
                    .centerCrop()
                    .placeholder(defaultBitmap)
                    .error(defaultBitmap)
                    .skipMemoryCache(false)
                    .transform(new GlideRoundTransform((int) radius))
                    .dontAnimate()
                    .into(view);
        }
    }
}