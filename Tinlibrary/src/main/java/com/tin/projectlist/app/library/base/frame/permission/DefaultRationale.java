/*
 * Copyright © Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tin.projectlist.app.library.base.frame.permission;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.cliff.libs.base.AppCoreSprite;
import com.cliff.libs.base.R;
import com.cliff.libs.base.widget.dialog.DialogUtil;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

/**
 * @Author: chenhx
 * @Date: 2018/4/12 10:50
 * @Description:
 */
public final class DefaultRationale implements Rationale {

    @Override
    public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.str_msg_perm_rationale, TextUtils.join("\n", permissionNames));
        AppCompatActivity topActivity = AppCoreSprite.getTopActivity();
        if (topActivity != null) {
            PermissionDialogFragment instance = PermissionDialogFragment.getInstance(message, context.getResources().getString(R.string.str_resume));
            instance.setOnClickListener(new PermissionDialogFragment.OnClickListener() {
                @Override
                public void onConfirmClick() {
                    executor.execute();
                }

                @Override
                public void onCancelClick() {
                    executor.cancel();
                }
            });
            DialogUtil.showDialogFragment(instance, topActivity.getSupportFragmentManager(), "showRationale");
        } else {
            executor.cancel();
        }
    }
}