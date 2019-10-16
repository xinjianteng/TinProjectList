package com.tin.projectlist.app.model.oldBook.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.BookDTO;

public class HomeRecommendAdapter extends BaseRecyclerViewAdapter<BookDTO,HomeRecommendAdapter.ViewHolder> {

    public HomeRecommendAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HomeRecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new HomeRecommendAdapter.ViewHolder(viewGroup, R.layout.item_fragment_home_recommend);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecommendAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(getItem(position).getBookName());

        switch (position) {
            case 9:
                holder.mTextView.setBackgroundColor(0xFFD5D8DB);
                holder.mTextView.setVisibility(View.VISIBLE);
                holder.mImageView.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(0xFFECECEC);
                break;
            case 11:
                holder.mTextView.setBackgroundColor(0xFFD5D8DB);
                holder.mTextView.setVisibility(View.GONE);
                holder.mImageView.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundColor(0xFFE22CEC);
                break;
            default:
                break;
        }
    }

     class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        private TextView mTextView;
        private ImageView mImageView;

        private ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            mTextView = (TextView) findViewById(R.id.tv_dialog_pay_key);
            mImageView = (ImageView) findViewById(R.id.iv_dialog_pay_delete);
        }
    }

}
