package com.tin.projectlist.app.model.knowldedge.model.base.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tin.projectlist.app.model.knowldedge.R;

public abstract class BaseListFrg extends BaseFrg {

    protected RecyclerView rcv_list;
    protected SmartRefreshLayout refreshLayout;

    @Override
    protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_list,null);
        rcv_list=view.findViewById(R.id.rcv_list);
        refreshLayout=view.findViewById(R.id.refreshLayout);

        return view;
    }


    //    @Override
//    protected void initView() {
//        initListView();
//    }


//    /***
//     * 初始化界面
//     */
//    protected abstract void initListView();


}
