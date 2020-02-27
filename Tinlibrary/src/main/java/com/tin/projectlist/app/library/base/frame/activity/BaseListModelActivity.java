package com.tin.projectlist.app.library.base.frame.activity;

import android.databinding.ViewDataBinding;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tin.projectlist.app.library.base.frame.BaseConstant;
import com.tin.projectlist.app.library.base.frame.PageEntity;
import com.tin.projectlist.app.library.base.frame.http.model.DataResult;
import com.tin.projectlist.app.library.base.frame.result.ApiPageResult;
import com.tin.projectlist.app.library.base.utils.AdapterUtil;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseViewModel;
import com.tin.projectlist.app.library.base.utils.ListUtil;
import com.tin.projectlist.app.library.base.utils.LoadLayoutUtil;
import com.tin.projectlist.app.library.base.widget.MultiStateView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 2019/10/9
 * author : chx
 * description :
 */
public abstract class BaseListModelActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseAppModelActivtiy<VM, DB> {

    protected PageEntity mPageEntity = new PageEntity(getPageSize());

    @Override
    public void setStateViewAndRefreshLayout(MultiStateView multiStateView, SmartRefreshLayout smartRefreshLayout) {
        this.multiStateView = new WeakReference<>(multiStateView);
        this.smartRefreshLayout = new WeakReference<>(smartRefreshLayout);
        if (smartRefreshLayout != null) {
            smartRefreshLayout.setOnLoadMoreListener(view -> loadData(mPageEntity));
            smartRefreshLayout.setOnRefreshListener(view -> loadData(new PageEntity(getPageSize())));
        }
        if (multiStateView != null) {
            multiStateView.setRetryListener(view -> loadData(new PageEntity(getPageSize())));
        }
    }


    public int getPageSize() {
        return BaseConstant.PAGE_SIZE_15;
    }

    protected abstract void loadData(PageEntity pageEntity);

    /***
     * 加载数据完成
     * @param apiPageResult
     * @param itemAdapter
     * @param <Model>
     */
    protected <Model> void onSmartRefreshComplete(ApiPageResult<DataResult<Model>> apiPageResult, BaseQuickAdapter itemAdapter) {
        PageEntity pageEntity = apiPageResult.getPageEntity();
        boolean isFirstPage = pageEntity.isFirstPage();
        boolean success = apiPageResult.getApiResult().isOk();
        DataResult<Model> data = apiPageResult.getApiResult().getData();
        List<Model> list = new ArrayList<>();
        if (success && data != null) {
            pageEntity.setTotal(data.getTotal());
            pageEntity.setPages(data.getPages());
            mPageEntity = pageEntity;
            pageEntity.incrementPageNum();
            list = data.getDatas();
        }
        AdapterUtil.setAdapterData(isFirstPage, success, list, itemAdapter);
        MultiStateView multiStateView = this.multiStateView != null ? this.multiStateView.get() : null;
        if (multiStateView != null) {
            LoadLayoutUtil.showResult(success, ListUtil.size(itemAdapter.getData()) > 0, multiStateView);
        }
        SmartRefreshLayout smartRefreshLayout = this.smartRefreshLayout != null ? this.smartRefreshLayout.get() : null;
        if (smartRefreshLayout != null) {
            boolean enableRefresh = success || ListUtil.size(itemAdapter.getData()) > 0;
            LoadLayoutUtil.refreshOrLoadComplete(isFirstPage, success, enableRefresh, pageEntity.isHasMore(), smartRefreshLayout);
        }
    }


}
