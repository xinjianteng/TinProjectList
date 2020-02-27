package com.tin.projectlist.app.model.oldBook.core.shelf;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.Book;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class ShelfBookAdapter extends BaseRecyclerViewAdapter<Book, ShelfBookAdapter.ViewHolder> {

    public ShelfBookAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_shelf_book);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bookName.setText(getItem(position).getBook_name());
        x.image().bind(holder.bookCover,getItem(position).getBook_cover());
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        @ViewInject(R.id.iv_bookCover)
        private ImageView bookCover;

        @ViewInject(R.id.tv_bookName)
        private TextView bookName;

        private ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            x.view().inject(this, getItemView());
        }
    }


}
