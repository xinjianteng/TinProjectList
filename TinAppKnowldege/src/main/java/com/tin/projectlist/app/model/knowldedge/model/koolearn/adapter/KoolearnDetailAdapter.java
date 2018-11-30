package com.tin.projectlist.app.model.knowldedge.model.koolearn.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.BaseRecyclerAdapter;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.SmartViewHolder;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.KoolearnDetailBean;

public class KoolearnDetailAdapter extends BaseRecyclerAdapter<KoolearnDetailBean> {

    private Context mContext;

    public KoolearnDetailAdapter(int resId) {
        super(resId);
    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, KoolearnDetailBean model, int position) {
        holder.text(R.id.tv, Html.fromHtml(model.getContent(),1));



    }

}
