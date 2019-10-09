package com.tin.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.tin.library.mvp.IBasePresenter;
import com.tin.library.mvp.IBaseView;
import com.tin.library.utils.toast.ToastUtils;
import com.tin.library.widget.DialogUtil;


/**
 * 基础Activity
 *
 * @package : com.cliff.libs.base
 * @description :
 * Created by chenhx on 2018/4/9 16:16.
 */

public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity implements IBaseView {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        x.view().inject(this);
//        setView();
//        setValue();
//        setListener();
//    }


    public static final int NO_ID = 0;
    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = bindPresenter();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 如果通知栏的权限被手动关闭了
        if (!ToastUtils.isNotificationEnabled(this) && "XToast".equals(ToastUtils.getToast().getClass().getSimpleName())) {
            // 因为吐司只有初始化的时候才会判断通知权限有没有开启，根据这个通知开关来显示原生的吐司还是兼容的吐司
            ToastUtils.init(getApplication());
            recreate();
            ToastUtil.showShort(this, R.string.toast_init_tips);
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
    }


    public void showTips(String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = getString(com.cliff.libs.base.R.string.module_unknow_wrong);
        }
        ToastUtil.showShort(this, msg);
    }

    public void showTips(int resId) {
        ToastUtil.showShort(this, resId);
    }


    public void startActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startActivityForResult(Context context, Class<?> clazz, int requestCode) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, requestCode);
    }


    @Override
    public void showDialogFragment(DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            showDialogFragment(dialogFragment, dialogFragment.getClass().getSimpleName());
        }
    }

    @Override
    public void showDialogFragment(DialogFragment dialogFragment, String tag) {
        DialogUtil.showDialogFragment(dialogFragment, getSupportFragmentManager(), tag);
    }

    @Override
    public void dismissDialogFragment(DialogFragment dialogFragment) {
        DialogUtil.dismissDialogFragment(dialogFragment, getSupportFragmentManager());
    }


    public Context getContext() {
        return this;
    }


    @Override
    public void showToast(String message) {
        ToastUtil.showShort(this, message);
    }

    @Override
    public void showLongToast(String message) {
        ToastUtil.showLong(this, message);
    }

    @Override
    public void showToast(int resId) {
        ToastUtil.showShort(this, resId);
    }

    @Override
    public void showLongToast(int resId) {
        ToastUtil.showLong(this, resId);
    }

}
