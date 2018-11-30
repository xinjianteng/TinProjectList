package com.tin.projectlist.app.model.knowldedge.model.koolearn.adapter;

import android.content.Context;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.BaseRecyclerAdapter;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.SmartViewHolder;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.utils.StringUtils;

public class KoolearnTypeAdapter extends BaseRecyclerAdapter<TypeBean> {

    private Context mContext;

    public KoolearnTypeAdapter(int resId) {
        super(resId);
    }


    @Override
    protected void onBindViewHolder(SmartViewHolder holder, TypeBean model, int position) {
        holder.text(R.id.tv_title, StringUtils.stringToDecoderUTF(model.getName()));


    }

}
