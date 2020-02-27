package com.tin.projectlist.app.library.base.frame.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.tin.projectlist.app.library.base.AppCoreSprite;
import com.tin.projectlist.app.library.base.frame.dialog.DialogUtil;
import com.tin.projectlist.app.library.base.frame.permission.PermissionKeys;
import com.tin.projectlist.app.library.base.frame.permission.PermissionUtil;
import com.tin.projectlist.app.library.base.utils.LogUtils;


/**
 * 2019/9/23
 * author : chx
 * description :
 */
public abstract class BaseActivity<DB extends ViewDataBinding> extends AppCompatActivity {
    private static final String TAG=BaseActivity.class.getSimpleName();
    protected DB dataBinding;
    private boolean requestPermission = true;
    private boolean hasRequestPermissions;

    protected abstract int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPre();
        int layoutId = getLayoutId();
        setContentView(layoutId);
        AppCoreSprite.addActivity(this);
        dataBinding = initDataBinding(layoutId);
        initView();
        initData();
    }


    protected DB initDataBinding(int layoutId) {
        LogUtils.e(TAG,"initDataBinding");
        return DataBindingUtil.setContentView(this, layoutId);
    }

    protected void initPre() {
    }

    protected abstract void initView();

    protected abstract void initData();


    @Override
    protected void onResume() {
        super.onResume();
        if (requestPermission && !hasRequestPermissions) {
            hasRequestPermissions = true;
            if (checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED || getSystemService(Context.TELEPHONY_SERVICE) == null) {
                PermissionUtil.requestPermissions(this, PermissionKeys.WRITE_EXTERNAL_STORAGE, PermissionKeys.ACCESS_WIFI_STATE);
            } else {
                PermissionUtil.requestPermissions(this, PermissionKeys.WRITE_READ_PHONE, PermissionKeys.WRITE_EXTERNAL_STORAGE, PermissionKeys.ACCESS_WIFI_STATE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        hasRequestPermissions = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppCoreSprite.removeActivity(this);
        if (dataBinding != null) {
            dataBinding.unbind();
        }
    }



    public boolean showDialogFragment(DialogFragment dialogFragment) {
        return DialogUtil.showDialogFragment(dialogFragment, getSupportFragmentManager(), dialogFragment.getClass().getSimpleName());
    }

    public void dismissDialogFragment(DialogFragment dialogFragment) {
        DialogUtil.dismissDialogFragment(dialogFragment, getSupportFragmentManager());
    }


    public Context getContext() {
        return this;
    }


    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        }
        return getSupportFragmentManager().isDestroyed();
    }

    public DisplayMetrics getDisplayMetrics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            return displayMetrics;
        } else {
            return getResources().getDisplayMetrics();
        }
    }
}
