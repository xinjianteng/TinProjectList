package com.tin.library.mvp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public interface IBaseView {

    Context getContext();

    void showDialogFragment(DialogFragment dialogFragment, String tag);

    void showDialogFragment(DialogFragment dialogFragment);

    void dismissDialogFragment(DialogFragment dialogFragment);

    FragmentManager getSupportFragmentManager();

    <T extends IBasePresenter> T bindPresenter();

    void startActivityForResult(Intent intent, int requestCode);

    void startActivity(Intent intent);

    void setResult(int resultCode);

    void setResult(int resultCode, Intent data);

    void showToast(String message);

    void showLongToast(String message);

    void showToast(@StringRes int resId);

    void showLongToast(@StringRes int resId);

    void finish();

}
