package com.tin.projectlist.app.library.base.frame.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tin.projectlist.app.library.base.frame.dialog.DialogUtil;
import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.base.widget.toast.ToastUtils;


/**
 * 2019/9/23
 * author : chx
 * description :
 */
public abstract class BaseFragment<DB extends ViewDataBinding> extends Fragment {


    private static final String TAG = BaseFragment.class.getSimpleName();
    protected DB dataBinding;

    protected abstract int getLayoutId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onCreateView");
        dataBinding = initDataBinding(inflater, getLayoutId(), container);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onActivityCreated");
        initView();
        initData();
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onAttach");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onHiddenChanged  : " + hidden);
    }

    /**
     * 初始化DataBinding
     */
    protected DB initDataBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup container) {
        return DataBindingUtil.inflate(inflater, layoutId, container, false);
    }

    protected abstract void initView();

    protected abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, this.getClass().getSimpleName() + "  onDestroy");
        if (dataBinding != null) {
            dataBinding.unbind();
        }
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

    public void setResult(int resultCode) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setResult(resultCode);
        }
    }

    public void setResult(int resultCode, Intent data) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setResult(resultCode, data);
        }
    }


    public void finish() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

}
