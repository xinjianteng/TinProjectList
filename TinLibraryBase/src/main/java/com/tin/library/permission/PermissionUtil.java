package com.tin.library.permission;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.tin.library.AppCoreSprite;
import com.tin.library.R;
import com.tin.library.widget.CommonDialogFragment;
import com.tin.library.widget.DialogUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.SettingService;

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
                        if (!AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            showSetting(context, permissions);
                        } else {
                            if (iPermission != null) {
                                iPermission.permissionResult(true);
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
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.str_msg_perm_always_failed, TextUtils.join("\n", permissionNames));
        final SettingService settingService = AndPermission.permissionSetting(context);
        DialogUtil.showConfirmDialogWithTitle(AppCoreSprite.getTopActivity().getSupportFragmentManager(), context.getResources().getString(R.string.str_kindly_reminder), message, context.getResources().getString(R.string.str_setting), context.getResources().getString(R.string.str_cancel),
                "showSetting", new CommonDialogFragment.OnBtnClickListener() {
                    @Override
                    public void onConfirm() {
                        settingService.execute();
                    }

                    @Override
                    public void onCancle() {
                        settingService.cancel();
                    }
                });

    }


}
