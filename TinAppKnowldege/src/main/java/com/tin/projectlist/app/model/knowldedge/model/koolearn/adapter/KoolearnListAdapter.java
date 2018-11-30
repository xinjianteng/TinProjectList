package com.tin.projectlist.app.model.knowldedge.model.koolearn.adapter;

import android.content.Context;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.BaseRecyclerAdapter;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.SmartViewHolder;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;
import com.tin.projectlist.app.model.knowldedge.model.utils.StringUtils;

public class KoolearnListAdapter extends BaseRecyclerAdapter<TypeClassilyBean> {

    private Context mContext;

    public KoolearnListAdapter(int resId) {
        super(resId);
    }


    @Override
    protected void onBindViewHolder(SmartViewHolder holder, TypeClassilyBean model, int position) {
        holder.text(R.id.tv_title, StringUtils.stringToDecoderUTF(model.getName()));

    }

}
