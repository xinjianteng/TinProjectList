package com.tin.library.utils;

import android.widget.ImageView;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

public class ImageLoadUtils {

    private static ImageOptions imageOptions=new ImageOptions.Builder()
            .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            .setIgnoreGif(false)
            .setParamsBuilder(new ImageOptions.ParamsBuilder() {
                @Override
                public RequestParams buildParams(RequestParams params, ImageOptions options) {
                    params.setCacheDirName("/cache");
                    return params;
                }
            })
            .build();



    public static void showImageForPath(ImageView imageView, String path){
        x.image().bind(imageView,path,imageOptions);
    }






}
