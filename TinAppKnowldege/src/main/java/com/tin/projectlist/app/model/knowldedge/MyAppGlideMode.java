package com.tin.projectlist.app.model.knowldedge;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @package : com.cliff.libs.util.image
 * @description :
 * Created by chenhx on 2018/4/10 10:39.
 */

@GlideModule
public final class MyAppGlideMode extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
    }
}
