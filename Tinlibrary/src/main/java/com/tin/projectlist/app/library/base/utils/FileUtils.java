package com.tin.projectlist.app.library.base.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class FileUtils {

    /**
     * 创建根缓存目录
     *
     * @return
     */
    public static String createRootPath(Context context) {
        String cacheRootPath = "";
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            cacheRootPath = context.getExternalCacheDir().getPath();
        } else {
            // /data/data/<application package>/cache
            cacheRootPath = context.getCacheDir().getPath();
        }
        return cacheRootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    public static File getExternalFilesDir(Context context, String fileName) {
        File file = context.getExternalFilesDir(null);
        if (file == null) {
            return  null;
        }
        file = new File(file, fileName);
        if (file.exists() ||  file.mkdirs()) {
            return file;
        }
        return null;
    }


    public static void deleteFiles(File fileDir, String exceptFile) {
        if (fileDir.exists() && !fileDir.isDirectory()) {
            fileDir.delete();
        }
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File[] files = fileDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (TextUtils.isEmpty(exceptFile) || !exceptFile.equals(file.getAbsolutePath())) {
                    file.delete();
                }
            }
        }
    }
}
