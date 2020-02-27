package com.tin.projectlist.app.library.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.cliff.libs.base.permission.PermissionKeys;
import com.cliff.libs.base.permission.PermissionUtil;
import com.cliff.libs.util.FileUtil;
import com.cliff.libs.util.ScreenUtil;
import com.cliff.libs.util.ToastUtil;

import java.io.File;

/**
 * @package : com.cliff.libs.base.util
 * @description :
 * Created by chenhx on 2018/4/27 15:26.
 */
public class PhotoUtil {
    /**
     * 打开相册
     *
     * @param mContext    上下文
     * @param requestCode 请求code标识符
     */
    public static void Album(Activity mContext, int requestCode) {
        //判断是否有权限
        PermissionUtil.requestPermissions(mContext, result -> {
            if (result) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_DEFAULT);
                galleryIntent.setType("image/*");
                try {
                    mContext.startActivityForResult(galleryIntent, requestCode);
                } catch (ActivityNotFoundException e) {
                    galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    galleryIntent.setType("image/*");
                    try {
                        mContext.startActivityForResult(galleryIntent, requestCode);
                    } catch (ActivityNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }, PermissionKeys.Group.CAMERAS);

    }

    /**
     * 打开相机
     *
     * @param requestCode
     */
    public static void Camera(Activity activity, Uri uri, int requestCode) {
        PermissionUtil.requestPermissions(activity, result -> {
            if (result) {
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                if (activity.getPackageManager().resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    try {
                        activity.startActivityForResult(cameraIntent, requestCode);
                    } catch (Exception e) {
                        ToastUtil.showLong(activity,"找不到合适系统的功能组件");
                    }
                }
            }
        }, PermissionKeys.Group.CAMERAS);

    }


    /***
     * 启动手机裁剪
     * @param context
     * @param uri
     * @param outputUri
     * @param resizeCode
     * @return
     */
    public static String resizeImage(Activity context, Uri uri, Uri outputUri, int resizeCode) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX,outputY 是剪裁图片的宽高
            int screenWidth = (int) (0.5 * ScreenUtil.getScreenWidth(context));
            intent.putExtra("outputX", screenWidth);
            intent.putExtra("outputY", screenWidth);
            /**
             * 此方法返回的图片只能是小图片（sumsang测试为高宽160dp的图片）
             * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
             */
            //intent.putExtra("return-data", true);
            //uritempFile为Uri类变量，实例化uritempFile

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            context.startActivityForResult(intent, resizeCode);
            return outputUri.getPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static Uri getImageUri(Context context, String fileName) {
        File image = FileUtil.getExternalFilesDir(context, "image");
        return image == null ? null : Uri.fromFile(new File(image, fileName));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (TextUtils.isDigitsOnly(id)) {
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {
                column
        };
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        Uri uri = null;
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            uri = Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                uri = context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return uri;
    }


    public static Uri getImageUri(Context context, File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static Intent getSelectedPictureIntent(Application application) {
        PackageManager packageManager = application.getPackageManager();

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (galleryIntent.resolveActivity(packageManager) != null) {
            return galleryIntent;
        }
        galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_DEFAULT);
        galleryIntent.setType("image/*");

        if (galleryIntent.resolveActivity(packageManager) != null) {
            return galleryIntent;
        }
        galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        galleryIntent.setType("image/*");
        if (galleryIntent.resolveActivity(packageManager) != null) {
            return galleryIntent;
        }
        return null;
    }

    public static Intent getCameraIntent(Application application, Uri uri) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        if (cameraIntent.resolveActivity(application.getPackageManager()) != null) {
            return cameraIntent;
        }
        return null;
    }

    public static Intent getCropIntent(Application application, Uri inputUri, Uri outputUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        int screenWidth = (int) (0.5 * ScreenUtil.getScreenWidth(application));
        intent.putExtra("outputX", screenWidth);
        intent.putExtra("outputY", screenWidth);
        /**
         * 此方法返回的图片只能是小图片（sumsang测试为高宽160dp的图片）
         * 故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
         */
        //intent.putExtra("return-data", true);
        //uritempFile为Uri类变量，实例化uritempFile

        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        if (intent.resolveActivity(application.getPackageManager()) != null) {
            return intent;
        }
        return null;
    }
}
