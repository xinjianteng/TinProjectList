package com.tin.projectlist.app.model.oldBook.core.shelf;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.library.base.widget.MultiStateView;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.common.MyLazyFragment;
import com.tin.projectlist.app.model.oldBook.core.home.HomeActivity;
import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.mvp.MvpLazyFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目炫酷效果示例
 */

@ContentView(R.layout.fragment_shelf)
public final class ShelfFragment extends MvpLazyFragment<ShelfPresenter> implements ShelfContract.View{
    @ViewInject(R.id.titleBar)
    TitleBar mToolbar;

    @ViewInject(R.id.recycler)
    RecyclerView rcvList;

    @ViewInject(R.id.multi_state_view)
    MultiStateView multiStateView;

    ShelfBookAdapter shelfBookAdapter;

    public static ShelfFragment newInstance() {
        return new ShelfFragment();
    }

    @Override
    protected View getTitleId() {
        return mToolbar;
    }


    @Override
    protected void initView() {
        multiStateView.showLoading();
        shelfBookAdapter=new ShelfBookAdapter(getContext());
        rcvList.setAdapter(shelfBookAdapter);
        shelfBookAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                getPresenter().openBook(shelfBookAdapter.getItem(position));
            }
        });
    }

    @Override
    protected void initData() {
        getPresenter().getLocationBookList();
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


    @Override
    public void onLocationBookListResult(List<Book> bookList) {
        shelfBookAdapter.setData(bookList);
        multiStateView.showContent();
    }

    @Override
    protected ShelfPresenter createPresenter() {
        return new ShelfPresenter();
    }
}