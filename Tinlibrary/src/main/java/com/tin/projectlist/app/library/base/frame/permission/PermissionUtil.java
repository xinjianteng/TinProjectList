package com.tin.projectlist.app.library.base.frame.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.cliff.libs.base.AppCoreSprite;
import com.cliff.libs.base.R;
import com.cliff.libs.base.widget.dialog.DialogUtil;
import com.cliff.libs.util.ListUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.SettingService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by chenhx on 2018/4/12 9:25.
 * @package : com.cliff.libs.base.permission
 * @description :
 */

public class PermissionUtil {


    public interface IPermission {
        /***
         * 权限请求返回
         * @param result 权限是否开通
         * @return
         */
        void permissionResult(boolean result);

    }

    /***
     * 请求权限
     * @param context
     * @param permissions
     */
    public static void requestPermissions(final Context context, String... permissions) {
        requestPermissions(context, null, permissions);
    }

    /***
     * 请求权限
     * @param context
     * @param permissions
     */
    public static void requestPermissions(final Context context, final IPermission iPermission, String... permissions) {
        DefaultRationale mRationale = new DefaultRationale();
        AndPermission.with(context)
                .permission(permissions)
                .rationale(mRationale)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (iPermission != null) {
                            iPermission.permissionResult(true);
                        }

                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        ArrayList<String> newPermissions = new ArrayList<>();
                        for (String permission : permissions) {
                            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                                newPermissions.add(permission);
                            }
                        }
                        if (ListUtil.isListNull(newPermissions)) {
                            if (iPermission != null) {
                                iPermission.permissionResult(true);
                            }
                        } else if (AndPermission.hasAlwaysDeniedPermission(context, newPermissions)) {
                            showSetting(context, newPermissions);
                        } else {
                            if (iPermission != null) {
                                iPermission.permissionResult(false);
                            }
                        }
                    }
                })
                .start();

    }

    /****
     * 打开手机设置权限界面
     * @param context
     * @param permissions
     */
    private static void showSetting(Context context, final List<String> permissions) {
        final SettingService settingService = AndPermission.permissionSetting(context);
        AppCompatActivity topActivity = AppCoreSprite.getTopActivity();
        if (topActivity == null || topActivity.isFinishing()) {
            settingService.cancel();
            return;
        }
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.str_msg_perm_always_failed, TextUtils.join("\n", permissionNames));
        PermissionDialogFragment fragment = PermissionDialogFragment.getInstance(message, context.getResources().getString(R.string.str_setting));
        fragment.setOnClickListener(new PermissionDialogFragment.OnClickListener() {
            @Override
            public void onConfirmClick() {
                settingService.execute();
            }

            @Override
            public void onCancelClick() {
                settingService.cancel();
            }
        });
        DialogUtil.showDialogFragment(fragment, topActivity.getSupportFragmentManager(),"showSetting");

    }


}
