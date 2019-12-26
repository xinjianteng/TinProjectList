package com.tin.projectlist.app.model.oldBook.core.gather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.Dynasty;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class GatherDynastyAdapter extends BaseRecyclerViewAdapter<Dynasty, GatherDynastyAdapter.ViewHolder> {

    public GatherDynastyAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_fragment_gather_dynasty);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mTextView.setText(getItem(position).getName());
        holder.mTextView.setBackgroundResource(getItem(position).isSelect()?R.color.item_bg_select :R.color.item_bg_default);
        holder.mTextView.setTextColor(getItem(position).isSelect()?getColor(R.color.item_tv_select) :getColor(R.color.item_tv_default));

    }

     class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        @ViewInject(R.id.tv_dynasty)
        private TextView mTextView;

        private ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            x.view().inject(this, getItemView());
        }
    }

}
