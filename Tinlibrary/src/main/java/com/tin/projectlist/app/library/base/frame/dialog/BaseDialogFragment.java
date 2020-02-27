package com.tin.projectlist.app.library.base.frame.dialog;


import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.tin.projectlist.app.library.base.frame.providers.ViewModelProviders;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseViewModel;
import com.tin.projectlist.app.library.base.utils.LogUtils;


public abstract class BaseDialogFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends DialogFragment {

    private static final String TAG = BaseDialogFragment.class.getSimpleName();
    protected DB dataBinding;
    protected ViewModelProvider viewModelProvider;
    protected VM viewModel;
    private boolean initProviderFragment = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), null, false);
        initViewProvider();
        viewModel = initViewModel();
        initView();
        return dataBinding.getRoot();
    }


    public void initViewProvider() {
        initViewProvider(this);
    }

    protected void initViewProvider(Object object) {
        if (object instanceof FragmentActivity) {
            initProviderFragment = false;
            viewModelProvider = ViewModelProviders.of((FragmentActivity) object);
        } else if (object instanceof Fragment) {
            viewModelProvider = ViewModelProviders.of((Fragment) object);
        } else {
            LogUtils.d(TAG, "initProvider error ");
        }
    }

    public abstract @LayoutRes
    int getLayoutId();

    public abstract VM initViewModel();

    public abstract void initView();


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = getDimAmount();
        params.gravity = getGravity();
        params.width = getWidth(params.width);
        params.height = getHeight(params.height);
        params.y = getY(params.y);
        window.setAttributes(params); //设置背景透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onDestroyView() {
        if (initProviderFragment && viewModel != null) {
            viewModel.onCleared();
        }
        super.onDestroyView();
    }

    protected int getY(int y) {
        return y;
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected int getWidth(int width) {
        return getResources().getDisplayMetrics().widthPixels * 8 / 10;
    }

    protected int getHeight(int height) {
        return height;
    }

    protected float getDimAmount() {
        return 0.3f;
    }

    public FragmentManager getSupportFragmentManager() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return activity.getSupportFragmentManager();
        }
        return null;
    }

    public boolean showDialogFragment(DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            return showDialogFragment(dialogFragment, dialogFragment.getClass().getSimpleName());
        }
        return false;
    }

    public boolean showDialogFragment(DialogFragment dialogFragment, String tag) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            return DialogUtil.showDialogFragment(dialogFragment, activity.getSupportFragmentManager(), tag);
        }
        return false;
    }

    public void dismissDialogFragment(DialogFragment dialogFragment) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            DialogUtil.dismissDialogFragment(dialogFragment, activity.getSupportFragmentManager());
        }
    }


}
