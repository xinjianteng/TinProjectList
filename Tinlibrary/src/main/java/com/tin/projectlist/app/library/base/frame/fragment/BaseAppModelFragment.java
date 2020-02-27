package com.tin.projectlist.app.library.base.frame.fragment;

import android.databinding.ViewDataBinding;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseViewModel;
import com.tin.projectlist.app.library.base.widget.MultiStateView;

import java.lang.ref.WeakReference;

/**
 * 2019/10/9
 * author : chx
 * description :
 */
public abstract class BaseAppModelFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseModelFragment<VM, DB> {

    protected WeakReference<MultiStateView> multiStateView;
    protected WeakReference<SmartRefreshLayout> smartRefreshLayout;



    public void setMultiStateView(MultiStateView multiStateView) {
        setStateViewAndRefreshLayout(multiStateView, null);
    }

    public void setSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout) {
        setStateViewAndRefreshLayout(null, smartRefreshLayout);
    }

    public void setStateViewAndRefreshLayout(MultiStateView multiStateView, SmartRefreshLayout smartRefreshLayout) {
        this.multiStateView = new WeakReference<>(multiStateView);
        this.smartRefreshLayout = new WeakReference<>(smartRefreshLayout);
        if (smartRefreshLayout != null) {
            smartRefreshLayout.setOnLoadMoreListener(view -> loadData());
            smartRefreshLayout.setOnRefreshListener(view -> loadData());
        }
        if (multiStateView != null) {
            multiStateView.setRetryListener(view -> loadData());
        }
    }

    protected void loadData() {

    }


}
