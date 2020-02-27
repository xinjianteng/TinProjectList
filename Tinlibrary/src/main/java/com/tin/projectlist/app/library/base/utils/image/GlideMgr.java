package com.tin.projectlist.app.library.base.utils.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * @package : com.cliff.libs.util.image
 * @description : Glide 管理类
 * Created by chenhx on 2018/4/9 14:14.
 */

public class GlideMgr {

    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 80;

    /***
     * application  onTrimMemory调用
     * @param context
     * @param level
     */
    public static void onTrimMemory(Context context, int level) {
        GlideApp.get(context.getApplicationContext()).onTrimMemory(level);
    }

    /***
     * application onLowMemory调用
     * @param context
     */
    public static void onLowMemory(Context context) {
        GlideApp.get(context.getApplicationContext()).onLowMemory();

    }

    /***
     * 清理内存缓存
     * @param context
     */
    public static void clearMemory(Context context) {
        GlideApp.get(context.getApplicationContext()).clearMemory();

    }

    /***
     * 清理文件缓存 注意此方法需要在io 线程中执行
     * @param context
     */
    public static void clearDiskCache(Context context) {
        GlideApp.get(context.getApplicationContext()).clearDiskCache();
    }

    /***
     * 加载图片的一个例子
     * @param context
     * @param imageView
     * @param url
     * @param palceId
     * @param errorId
     */
    public static void loadImage(Context context, String url, int palceId, int errorId, ImageView imageView) {
        GlideApp.with(context.getApplicationContext())
                .load(url)
                .placeholder(palceId)
                .error(errorId)
                .into(imageView);

    }

    public static void loadImage(Context context, String imgUrl, ImageView imageView) {
        GlideApp.with(context.getApplicationContext())
                .load(imgUrl)
                .into(imageView);
    }


    public static void loadImage(Context context, int resId, ImageView imageView) {
        GlideApp.with(context.getApplicationContext())
                .load(resId)
                .into(imageView);
    }


    public static void loadImage(Context context, int resId, boolean skip, ImageView imageView) {
        GlideApp.with(context.getApplicationContext())
                .load(resId)
                .skipMemoryCache(skip)
                .into(imageView);
    }

    public static void loadImage(Activity activity, int resId, boolean skip, ImageView imageView) {
        GlideApp.with(activity)
                .load(resId)
                .skipMemoryCache(skip)
                .into(imageView);
    }

    /****
     * 加载bitmap
     * @param context
     * @param url
     * @return
     */
    public static Bitmap loadImage(Context context, String url) {
        return loadImage(context, url, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /***
     * 加载bitmap
     * @param context
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap loadImage(Context context, String url, int width, int height) {
        Bitmap bitmap = null;
        if (width <= 0) {
            width = DEFAULT_WIDTH;
        }
        if (height < 0) {
            height = DEFAULT_HEIGHT;
        }
        try {
            bitmap = GlideApp.with(context.getApplicationContext())
                    .asBitmap()
                    .load(url)
                    .into(width, height)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /***
     * 清理imageholder
     * @param context
     */
    public static void clearImageHolder(Context context, ImageView view) {
        try {
            if (context != null) {
                Glide.with(context.getApplicationContext()).clear(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 清理imageholder
     * @param activity
     */
    public static void clearImageHolder(Activity activity, ImageView view) {
        try {
            if (activity != null && !activity.isFinishing()) {
                Glide.with(activity).clear(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 清理imageholder
     * @param fragment
     */
    public static void clearImageHolder(Fragment fragment, ImageView view) {
        try {
            if (fragment != null) {
                Glide.with(fragment).clear(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****
     * 加载圆角图片
     * @param context
     * @param url
     * @param roundingRadius
     * @param view
     */
    public static void loadRoundImage(Context context, String url, int roundingRadius, ImageView view) {
        loadRoundImage(context, url, -1, -1, roundingRadius, view);
    }

    /****
     * @param imageView
     * @param url
     * @param
     */
    public static void loadImage(ImageView imageView, String url, Transformation transformation) {
        GlideApp.with(imageView)
                .load(url)
                .centerCrop()
                .skipMemoryCache(false)
                .transform(transformation)
                .dontAnimate()
                .into(imageView);
    }

    /****
     * @param imageView
     * @param url
     * @param
     */
    public static void loadRoundImage(ImageView imageView, String url, boolean skip, Transformation transformation) {
        GlideApp.with(imageView)
                .load(url)
                .centerCrop()
                .skipMemoryCache(skip)
                .transform(transformation)
                .dontAnimate()
                .into(imageView);
    }


    /****
     * 加载圆角图片
     * @param context
     * @param url
     * @param roundingRadius
     * @param view
     */
    public static void loadRoundImage(Context context, String url, int placeId, int errorId, int roundingRadius, ImageView view) {
        GlideApp.with(context.getApplicationContext())
                .load(url)
                .centerCrop()
                .placeholder(placeId)
                .error(errorId)
                .skipMemoryCache(false)
                .transform(new GlideRoundTransform(roundingRadius))
                .dontAnimate()
                .into(view);


    }

    /****
     * 加载圆角图片
     * @param context
     * @param url
     * @param roundingRadius
     * @param view
     */
    public static void loadRoundImage(Context context, String url, int placeId, int errorId, int roundingRadius, ImageView view, boolean shipMemory) {
        GlideApp.with(context.getApplicationContext())
                .load(url)
                .centerCrop()
                .placeholder(placeId)
                .error(errorId)
                .skipMemoryCache(shipMemory)
                .transform(new GlideRoundTransform(roundingRadius))
                .dontAnimate()
                .into(view);


    }

    /****
     * 加载圆角图片
     * @param fragment
     * @param url
     * @param roundingRadius
     * @param view
     */
    public static void loadRoundImage(Fragment fragment, String url, int placeId, int errorId, int roundingRadius, ImageView view, boolean shipMemory) {
        GlideApp.with(fragment)
                .load(url)
                .centerCrop()
                .placeholder(placeId)
                .error(errorId)
                .skipMemoryCache(shipMemory)
                .transform(new GlideRoundTransform(roundingRadius))
                .dontAnimate()
                .into(view);


    }

    /***
     * 加载圆形图片
     * @param path
     * @param palceId
     * @param errorId
     * @param view
     */
    public static void loadCircleImage(String path, int palceId, int errorId, ImageView view) {
        GlideApp.with(view.getContext().getApplicationContext())
                .asBitmap()
                .load(path)
                .placeholder(palceId)
                .error(errorId)
                .skipMemoryCache(false)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .dontAnimate()
                .into(view);
    }

    /***
     * 加载圆形图片
     * @param path
     * @param palceId
     * @param errorId
     * @param view
     */
    public static void loadCircleImage(Context Context, String path, int palceId, int errorId, ImageView view) {

        GlideApp.with(view.getContext())
                .asBitmap()
                .load(path)
                .placeholder(palceId)
                .error(errorId)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .skipMemoryCache(false)
                .dontAnimate()
                .into(view);

    }

    public static void loadImage(Context context, String path, Target target) {
        GlideApp.with(context).load(path).into(target);
    }

    public static void loadImage(Context context, String path, int radius, Target target) {
        GlideApp.with(context).load(path)
                .transform(new GlideRoundTransform(radius)).into(target);
    }


    public static void loadImageAsBitmap(Context context, String path, Target<Bitmap> target) {
        GlideApp.with(context).asBitmap().load(path).into(target);
    }

    public static void loadImage(Context context, String path, Target target, boolean skipMemory) {
        GlideApp.with(context).load(path).skipMemoryCache(skipMemory).into(target);
    }


    // 使用构造方法 RoundedCornersTransformation(Context context, int radius, int margin, CornerType cornerType)
    // radius ：圆角半径
    // margin ：填充边界
    // cornerType ：边角类型（可以指定4个角中的哪几个角是圆角，哪几个不是）
    public static void loadRoundCornerImage(ImageView view, int resId, int radius, RoundedCornersTransformation.CornerType cornerType) {
        GlideApp.with(view.getContext())
                .load(resId)
                .transform(new RoundedCornersTransformation(radius, 0, cornerType))
                .into(view);
    }

    public static void loadRoundCornerImage(Fragment fragment, ImageView view, String path, int radius, RoundedCornersTransformation.CornerType cornerType) {
        GlideApp.with(fragment)
                .load(path)
                .transform(new RoundedCornersTransformation(radius, 0, cornerType))
                .into(view);
    }

    public static void loadRoundCornerImage(Fragment fragment, ImageView view, int resId, int radius, RoundedCornersTransformation.CornerType cornerType) {
        GlideApp.with(fragment)
                .load(resId)
                .transform(new RoundedCornersTransformation(radius, 0, cornerType))
                .into(view);
    }

    public static void loadRoundCornerImage(ImageView view, String path, int radius, RoundedCornersTransformation.CornerType cornerType) {
        GlideApp.with(view.getContext())
                .load(path)
                .transform(new RoundedCornersTransformation(radius, 0, cornerType))
                .into(view);
    }

    public static void loadRoundCornerImage(ImageView view, String path, int radius, int palceId, int errorId, RoundedCornersTransformation.CornerType cornerType) {
        GlideApp.with(view.getContext())
                .load(path)
                .placeholder(palceId)
                .error(errorId)
                .transform(new RoundedCornersTransformation(radius, 0, cornerType))
                .into(view);
    }


    public static void loadRoundCornerImage(Context context, int resId, int radius, RoundedCornersTransformation.CornerType cornerType, View view) {
        GlideApp.with(context)
                .load(resId)
                .transform(new RoundedCornersTransformation(radius, 0, cornerType))
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

    /***
     * 加载gif 动画
     * @param imageView
     * @param resId
     * @param loopCount
     */
    public static void loadGif(ImageView imageView, int resId, final int loopCount) {
        GlideApp.with(imageView.getContext()).load(resId).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    ((GifDrawable) resource).setLoopCount(loopCount);
                }
                return false;
            }
        }).into(imageView);

    }
}
