package com.tin.projectlist.app.model.oldBook.oldBook.core.gather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.Book;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class GatherBookAdapter extends BaseRecyclerViewAdapter<Book, GatherBookAdapter.ViewHolder> {

    public GatherBookAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.view_item_book);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bookName.setText(getItem(position).getBook_name());
        holder.bookAuthor.setText(getItem(position).getBook_author());
        holder.bookSummary.setText(getItem(position).getBook_summary());
        x.image().bind(holder.bookCover,getItem(position).getBook_cover());
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        @ViewInject(R.id.iv_bookCover)
        private ImageView bookCover;

        @ViewInject(R.id.tv_bookName)
        private TextView bookName;

        @ViewInject(R.id.tv_bookAuthor)
        private TextView bookAuthor;

        @ViewInject(R.id.tv_bookSummary)
        private TextView bookSummary;

        private ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            x.view().inject(this, getItemView());
        }
    }


}
