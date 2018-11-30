package com.tin.projectlist.app.model.knowldedge.model.koolearn.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.Constant;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseListAct;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.adapter.KoolearnTypeAdapter;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.KoolearnDetailBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.imp.IKoolearnContract;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.presenter.KoolearnPresenter;

import java.util.List;

/**
 * 新东方
 */
public class KoolearnTypeActivity extends BaseListAct implements IKoolearnContract.View{

    private final  String Tag=getClass().getName();
    KoolearnTypeAdapter koolearnTypeAdapter;
    private IKoolearnContract.Presenter mPresenter;

    @Override
    protected void setValue() {
        bindPersenter();
        rcv_list.setLayoutManager(new GridLayoutManager(this,2));
        rcv_list.setItemAnimator(new DefaultItemAnimator());
        rcv_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        koolearnTypeAdapter = new KoolearnTypeAdapter(R.layout.activity_koolean_item);
        koolearnTypeAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(KoolearnTypeActivity.this,KoolearnListActivity.class);
                intent.putExtra("title",((TypeBean) koolearnTypeAdapter.getItem(position)).getName());
                intent.putExtra("url",((TypeBean) koolearnTypeAdapter.getItem(position)).getHref());
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.getTypeList(Constant.koolean_chiness);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(200);
            }
        });
        rcv_list.setAdapter(koolearnTypeAdapter);
        refreshLayout.autoRefresh();

    }

    @Override
    protected void setListener() {
    }



    @Override
    public void showTypeList(List<TypeBean> typeBeanList) {
        koolearnTypeAdapter.refresh(typeBeanList);
        refreshLayout.finishRefresh();
    }

    @Override
    public void showTypeClassilyLisxt(List<TypeClassilyBean> typeClassilyBeanList) {

    }

    @Override
    public void showTypeClassilyDetail(List<KoolearnDetailBean> typeClassilyBeanList) {

    }

    @Override
    public void setPersenter(IKoolearnContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public IKoolearnContract.Presenter bindPersenter() {
        return KoolearnPresenter.bind(this).attahView(this);
    }


}
