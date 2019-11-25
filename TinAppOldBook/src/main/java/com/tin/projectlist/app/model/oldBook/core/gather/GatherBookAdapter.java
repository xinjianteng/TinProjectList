package com.tin.projectlist.app.model.oldBook.core.gather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.entity.Dynasty;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class GatherBookAdapter extends BaseRecyclerViewAdapter<Book, GatherBookAdapter.ViewHolder> {

    public GatherBookAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public GatherBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new GatherBookAdapter.ViewHolder(viewGroup, R.layout.item_fragment_gather_book);
    }

    @Override
    public void onBindViewHolder(@NonNull GatherBookAdapter.ViewHolder holder, int position) {
        holder.bookName.setText(getItem(position).getBook_name());
        holder.bookAuthor.setText("作者："+getItem(position).getBook_name());
        holder.bookDynasty.setText("朝代："+getItem(position).getBook_name());
        x.image().bind(holder.bookCover,getItem(position).getBook_name());
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        @ViewInject(R.id.iv_bookCover)
        private ImageView bookCover;

        @ViewInject(R.id.tv_bookName)
        private TextView bookName;

        @ViewInject(R.id.tv_bookAuthor)
        private TextView bookAuthor;

        @ViewInject(R.id.tv_bookDynasty)
        private TextView bookDynasty;

        private ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            x.view().inject(this, getItemView());
        }
    }


}
