package com.tin.projectlist.app.library.base.frame.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tin.projectlist.app.library.base.R;
import com.tin.projectlist.app.library.base.frame.providers.ViewModelProviders;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseViewModel;
import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.library.base.utils.event.EventBusMgr;
import com.tin.projectlist.app.library.base.widget.toast.ToastUtils;


/**
 * 2019/9/23
 * author : chx
 * description :
 */
public abstract class BaseModelActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseActivity<DB> {
    private static final String TAG=BaseModelActivity.class.getSimpleName();
    protected VM viewModel;
    protected ViewModelProvider viewModelProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(getEnterAnimator(), getExitAnimator());
    }

    @Override
    protected DB initDataBinding(int layoutId) {
        LogUtils.e(TAG,"initDataBinding");
        DB db = super.initDataBinding(layoutId);
        viewModelProvider = ViewModelProviders.of(this);
        viewModel = initViewModel();
        initObserver();
        return db;
    }


    protected abstract VM initViewModel();

    private void initObserver() {
        if (viewModel != null) {
            viewModel.getError(this, new Observer<Object>() {
                @Override
                public void onChanged(@Nullable Object o) {
                    if (o != null) {
                        ToastUtils.show(o.toString());
                    }
                }
            });
        }
    }

    public int getEnterAnimator() {
        return R.anim.slide_in_right;
    }

    public int getExitAnimator() {
        return R.anim.slide_out_right;
    }

    @Override
    protected void onDestroy() {
        EventBusMgr.ungister(this);
        super.onDestroy();
        if (viewModel != null) {
            viewModel.onCleared();
        }
    }
}
