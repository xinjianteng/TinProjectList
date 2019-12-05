package com.tin.projectlist.app.model.oldBook.core.read.epub;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.Book;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class EpubCatalogAdapter extends BaseRecyclerViewAdapter<Book, EpubCatalogAdapter.ViewHolder> {

    public EpubCatalogAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public EpubCatalogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EpubCatalogAdapter.ViewHolder(viewGroup, R.layout.item_read_catalog);
    }

    @Override
    public void onBindViewHolder(@NonNull EpubCatalogAdapter.ViewHolder holder, int position) {
//        Drawable drawable;
//        if (currentChapter == position + 1) {
//            tvTocItem.setTextColor(ContextCompat.getColor(mContext, R.color.light_red));
//            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_toc_item_activated);
//        } else if (isEpub || FileUtils.getChapterFile(bookId, position + 1).length() > 10) {
//            tvTocItem.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
//            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_toc_item_download);
//        } else {
//            tvTocItem.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
//            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_toc_item_normal);
//        }
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        tvTocItem.setCompoundDrawables(drawable, null, null, null);
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
