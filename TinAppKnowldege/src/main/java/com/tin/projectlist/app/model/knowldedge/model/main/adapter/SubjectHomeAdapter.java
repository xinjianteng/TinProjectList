package com.tin.projectlist.app.model.knowldedge.model.main.adapter;

import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.BaseRecyclerAdapter;
import com.tin.projectlist.app.model.knowldedge.model.base.adapter.SmartViewHolder;
import com.tin.projectlist.app.model.knowldedge.model.main.bean.comMoyun.DbMoYunContent;

public class SubjectHomeAdapter extends BaseRecyclerAdapter<DbMoYunContent>{

    public SubjectHomeAdapter(int layoutId) {
        super(layoutId);
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, DbMoYunContent model, int position) {
        holder.text(R.id.tv_title, Html.fromHtml(model.getTitle()==null?"":model.getTitle()));

//        holder.tv_tag.setText(Html.fromHtml(dbContentT.getTag()==null?"":dbContentT.getTag()));
//        holder.tv_type.setText(Html.fromHtml(dbContentT.getType()==null?"":(dbContentT.getType()+"")));
//        holder.tv_title.setText(Html.fromHtml(dbContentT.getTitle()==null?"":dbContentT.getTitle()));
//        holder.tv_content.setText(Html.fromHtml(dbContentT.getContent()==null?"":dbContentT.getContent()));
//        holder.tv_des.setText(Html.fromHtml(dbContentT.getDesc()==null?"":dbContentT.getDesc()));
//        holder.tv_detailurl.setText(Html.fromHtml(dbContentT.getDetailUrl()==null?"":dbContentT.getDetailUrl()));

    }

    public class RecyclerHolder extends SmartViewHolder {

        TextView tv_type;
        TextView tv_tag;
        TextView tv_title;
        TextView tv_content;
        TextView tv_des;
        TextView tv_detailurl;

        public RecyclerHolder(View itemView, AdapterView.OnItemClickListener mListener) {
            super(itemView, mListener);
            tv_type=itemView.findViewById(R.id.tv_type);
            tv_tag=itemView.findViewById(R.id.tv_tag);
            tv_title=itemView.findViewById(R.id.tv_title);
            tv_content=itemView.findViewById(R.id.tv_content);
            tv_des=itemView.findViewById(R.id.tv_des);
            tv_detailurl=itemView.findViewById(R.id.tv_detailurl);
        }
    }

}
