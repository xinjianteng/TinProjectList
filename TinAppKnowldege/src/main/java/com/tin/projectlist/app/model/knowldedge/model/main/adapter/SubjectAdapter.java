package com.tin.projectlist.app.model.knowldedge.model.main.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.BaseRecyclerAdapter;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.SmartViewHolder;
import com.tin.projectlist.app.model.knowldedge.model.main.bean.SubjectBean;
import com.tin.projectlist.app.model.knowldedge.model.utils.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

public class SubjectAdapter extends BaseRecyclerAdapter<SubjectBean> {


    public SubjectAdapter(int resId) {
        super(resId);
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, SubjectBean model, int position) {
        holder.text(R.id.tv_title, StringUtils.stringToDecoderUTF(model.getSubjectName()));
    }


    public class RecyclerHolder extends SmartViewHolder {

        TextView tv_subject;

        public RecyclerHolder(View itemView, AdapterView.OnItemClickListener mListener) {
            super(itemView, mListener);
            tv_subject=itemView.findViewById(R.id.tv_subject);
            AutoUtils.auto(itemView);
        }
    }

}
