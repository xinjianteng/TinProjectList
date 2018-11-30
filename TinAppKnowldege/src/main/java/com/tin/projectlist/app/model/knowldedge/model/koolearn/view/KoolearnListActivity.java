package com.tin.projectlist.app.model.knowldedge.model.koolearn.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseListAct;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.adapter.KoolearnListAdapter;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.KoolearnDetailBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.imp.IKoolearnContract;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.presenter.KoolearnPresenter;

import java.util.List;

/**
 * 列表页
 */
public class KoolearnListActivity extends BaseListAct implements IKoolearnContract.View{

    private final  String Tag=getClass().getName();
    KoolearnListAdapter koolearnListAdapter;
    private IKoolearnContract.Presenter mPresent;


    @Override
    protected void setValue() {
        bindPersenter();
        title=getIntent().getStringExtra("title");
        url=getIntent().getStringExtra("url");
        tvTitle.setText(title);
        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_list.setItemAnimator(new DefaultItemAnimator());
        rcv_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        koolearnListAdapter = new KoolearnListAdapter(R.layout.activity_koolean_list_item);
        koolearnListAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(KoolearnListActivity.this,KoolearnDetailActivity.class);
                intent.putExtra("title",((TypeClassilyBean) koolearnListAdapter.getItem(position)).getName());
                intent.putExtra("url",((TypeClassilyBean) koolearnListAdapter.getItem(position)).getHerf());
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresent.getTypeClassildyList(url);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(200);
            }
        });
        rcv_list.setAdapter(koolearnListAdapter);
        refreshLayout.autoRefresh();

    }

    @Override
    protected void setListener() {

    }

    @Override
    public void showTypeList(List<TypeBean> typeBeanList) {

    }

    @Override
    public void showTypeClassilyLisxt(List<TypeClassilyBean> typeClassilyBeanList) {
        koolearnListAdapter.refresh(typeClassilyBeanList);
        refreshLayout.finishRefresh();
    }

    @Override
    public void showTypeClassilyDetail(List<KoolearnDetailBean> typeClassilyBeanList) {

    }

    @Override
    public void setPersenter(IKoolearnContract.Presenter presenter) {
        mPresent=presenter;
    }

    @Override
    public IKoolearnContract.Presenter bindPersenter() {
        return KoolearnPresenter.bind(this).attahView(this);
    }




}
