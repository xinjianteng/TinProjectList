package com.tin.projectlist.app.model.oldBook.core.read.epub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.readingTool.BookMixAToc;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class EpubCatalogAdapter extends BaseRecyclerViewAdapter<BookMixAToc.mixToc.Chapters, EpubCatalogAdapter.ViewHolder> {

    public EpubCatalogAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_read_catalog);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTocItem.setText(getItem(position).title);
    }

    class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {

        @ViewInject(R.id.tvTocItem)
        private TextView tvTocItem;

        private ViewHolder(ViewGroup parent, int layoutId) {
            super(parent, layoutId);
            x.view().inject(this, getItemView());
        }
    }


}
