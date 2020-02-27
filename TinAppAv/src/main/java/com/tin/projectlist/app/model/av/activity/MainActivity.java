package com.tin.projectlist.app.model.av.activity;

import android.support.v7.widget.LinearLayoutManager;

import com.tin.projectlist.app.library.base.frame.PageEntity;
import com.tin.projectlist.app.library.base.frame.activity.BaseListModelActivity;

import com.tin.projectlist.app.library.base.frame.adapter.QuickAdapter;
import com.tin.projectlist.app.model.av.R;
import com.tin.projectlist.app.model.av.databinding.ActivityMainBinding;
import com.tin.projectlist.app.model.av.databinding.ViewListBarBinding;
import com.tin.projectlist.app.model.av.entity.MainEntity;
import com.tin.projectlist.app.model.av.viewmodel.MainViewModel;


public class MainActivity extends BaseListModelActivity<MainViewModel, ActivityMainBinding> {

    private QuickAdapter<MainEntity> mainEntityQuickAdapter;

    @Override
    protected void loadData(PageEntity pageEntity) {
        viewModel.getList(pageEntity);
    }

    @Override
    protected MainViewModel initViewModel() {
        return viewModelProvider.get(MainViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setStateViewAndRefreshLayout(dataBinding.multiStateView, dataBinding.smartRefresh);
        mainEntityQuickAdapter = new QuickAdapter<>(R.layout.item_main);
        dataBinding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        dataBinding.recycler.setAdapter(mainEntityQuickAdapter);
    }


    @Override
    protected void initData() {
        viewModel.getList(mPageEntity);
        viewModel.getApiResult().observe(this, dataResultApiPageResult -> {
            onSmartRefreshComplete(dataResultApiPageResult,mainEntityQuickAdapter);
        });

    }


}
