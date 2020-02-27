package com.tin.projectlist.app.library.base.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.target.Target;
import com.cliff.business.base.R;
import com.cliff.libs.util.image.GlideMgr;

/**
 * 2019/12/19
 * author : chx
 * description :
 */
public class CommonUtil {
    /***
     * 加载圆形头像图片
     * @param view
     * @param path
     */
    public static void loadHeadCircleImage(ImageView view, String path) {
        GlideMgr.loadCircleImage(view.getContext(), path, R.drawable.head_default_round, R.drawable.head_default_round, view);
    }

    /***
     * 加载图书图片
     */
    public static void loadBookImage(ImageView view, String path, float radiusDp) {
        GlideMgr.loadRoundImage(view.getContext(), path, R.drawable.book_default_cover, R.drawable.book_error, (int) radiusDp, view);
    }


    /***
     * 加载图书图片
     */
    public static void loadImage(ImageView view, String path, int placeId, int errorId, float radiusDp) {
        GlideMgr.loadRoundImage(view.getContext(), path, placeId, errorId, (int) radiusDp, view);
    }

    /***
     * 加载图书图片
     */
    public static void loadImage(ImageView view, String path, int placeId, int errorId, float radiusDp, boolean shipMemory) {
        GlideMgr.loadRoundImage(view.getContext(), path, placeId, errorId, (int) radiusDp, view, shipMemory);
    }

    /***
     * 加载图书图片
     */
    public static void loadImage(ImageView view, String path, int placeId, int errorId) {
        GlideMgr.loadImage(view.getContext(), path, placeId, errorId, view);
    }

    public static void loadImage(ImageView imageView, String path, Transformation transformation) {
        GlideMgr.loadImage(imageView, path, transformation);
    }

    /***
     * 加载图书图片
     */
    public static void loadImage(Context context, String path, Target target) {
        GlideMgr.loadImage(context, path, target);
    }

    /***
     * 加载图书图片
     */
    public static void loadImage(Context context, String path, Target target, boolean skipMemory) {
        GlideMgr.loadImage(context, path, target, skipMemory);
    }

}
