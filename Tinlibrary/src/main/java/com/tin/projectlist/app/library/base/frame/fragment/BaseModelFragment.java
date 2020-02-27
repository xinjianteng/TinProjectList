package com.tin.projectlist.app.library.base.frame.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tin.projectlist.app.library.base.frame.providers.ViewModelProviders;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseViewModel;
import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.base.widget.toast.ToastUtils;


/**
 * 2019/9/23
 * author : chx
 * description :
 */
public abstract class BaseModelFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseFragment<DB> {

    private static final String TAG = BaseModelFragment.class.getSimpleName();
    protected VM viewModel;
    protected ViewModelProvider viewModelProvider;
    private boolean initProviderFragment = true;


    @Override
    protected DB initDataBinding(LayoutInflater inflater, int layoutId, ViewGroup container) {
        DB db = super.initDataBinding(inflater, layoutId, container);
        initProvider();
        viewModel = initViewModel();
        initObserver();
        return db;
    }

    /**
     * 初始化ViewModel
     */
    protected abstract VM initViewModel();

    protected void initProvider() {
        initProvider(this);
    }

    protected void initProvider(Object object) {
        if (object instanceof FragmentActivity) {
            initProviderFragment = false;
            viewModelProvider = ViewModelProviders.of((FragmentActivity) object);
        } else if (object instanceof Fragment) {
            viewModelProvider = ViewModelProviders.of((Fragment) object);
        } else {
            LogUtils.d(TAG, "initProvider error ");
        }
    }

    private void initObserver() {
        if (viewModel != null) {
            viewModel.getError(this, new Observer<Object>() {
                @Override
                public void onChanged(@Nullable Object o) {
                    ToastUtils.show(o.toString());
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (initProviderFragment && viewModel != null) {
            viewModel.onCleared();
        }

    }

    public DB getViewDataBinding() {
        return dataBinding;
    }

}
