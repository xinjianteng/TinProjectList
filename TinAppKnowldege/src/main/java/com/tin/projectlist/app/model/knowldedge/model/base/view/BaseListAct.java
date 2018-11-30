package com.tin.projectlist.app.model.knowldedge.model.base.view;

import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tin.projectlist.app.model.knowldedge.R;

public abstract class BaseListAct extends BaseAppAct   {

    private static final String TAG = BaseListAct.class.getSimpleName();
    protected RecyclerView rcv_list;
    protected SmartRefreshLayout refreshLayout;
    protected String title;
    protected String url;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_list;
    }


    @Override
    protected void initView() {
        rcv_list=findViewById(R.id.rcv_list);
        refreshLayout=findViewById(R.id.refreshLayout);
    }


}
