package com.tin.projectlist.app.model.oldBook.core.book;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.BookComment;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class BookCommentAdapter extends BaseRecyclerViewAdapter<BookComment, BookCommentAdapter.ViewHolder> {

    public BookCommentAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_comment);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tvContent.setText(getItem(position).getContent());
    }

     class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        @ViewInject(R.id.tv_content)
        private TextView tvContent;

        private ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            x.view().inject(this, getItemView());
        }
    }

}
