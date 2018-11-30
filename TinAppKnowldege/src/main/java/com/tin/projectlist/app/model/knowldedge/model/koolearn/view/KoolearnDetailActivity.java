package com.tin.projectlist.app.model.knowldedge.model.koolearn.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseListAct;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.adapter.KoolearnDetailAdapter;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.KoolearnDetailBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.imp.IKoolearnContract;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.presenter.KoolearnPresenter;

import java.util.List;


/***
 * 详情页
 */
public class KoolearnDetailActivity extends BaseListAct implements IKoolearnContract.View{

    private final  String Tag=getClass().getName();
    KoolearnDetailAdapter adapter;
    private String title;
    private String url;
    private IKoolearnContract.Presenter mPresenter;


    @Override
    protected void setValue() {
        bindPersenter();
        title=getIntent().getStringExtra("title");
        url=getIntent().getStringExtra("url");
        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_list.setItemAnimator(new DefaultItemAnimator());
//        rcv_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        adapter = new KoolearnDetailAdapter(R.layout.activity_koolearn_detail_item);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent(KoolearnDetailActivity.this,KoolearnDetailActivity.class);
//                intent.putExtra("title",((TypeClassilyBean) koolearnListAdapter.getItem(position)).getName());
//                intent.putExtra("url",((TypeClassilyBean) koolearnListAdapter.getItem(position)).getHerf());
//                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.getTypeClassildyDetail(url);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(200);
            }
        });
        rcv_list.setAdapter(adapter);
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

    }

    @Override
    public void showTypeClassilyDetail(List<KoolearnDetailBean> typeClassilyBeanList) {
        adapter.refresh(typeClassilyBeanList);
        refreshLayout.finishRefresh();
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
