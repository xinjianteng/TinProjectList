package com.tin.projectlist.app.model.oldBook.core.gather;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.library.base.BaseRecyclerViewAdapter;
import com.tin.projectlist.app.library.base.utils.IntentUtils;
import com.tin.projectlist.app.library.base.widget.MultiStateView;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.constant.KeyConstant;
import com.tin.projectlist.app.model.oldBook.core.book.BookDetailActivity;
import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.entity.Dynasty;
import com.tin.projectlist.app.model.oldBook.mvp.MvpLazyFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 项目炫酷效果示例
 */
@ContentView(R.layout.fragment_gather)
public final class GatherFragment extends MvpLazyFragment<GatherPresenter>
        implements GatherContract.View {

    @ViewInject(R.id.tabBar)
    TitleBar mToolbar;

    @ViewInject(R.id.rcv_index)
    RecyclerView rcvIndex;

    @ViewInject(R.id.recycler)
    RecyclerView rcvList;

    @ViewInject(R.id.multi_state_view)
    MultiStateView multiStateView;

    GatherDynastyAdapter gatherDynastyAdapter;
    GatherBookAdapter gatherBookAdapter;

    public static GatherFragment newInstance() {
        return new GatherFragment();
    }

    @Override
    protected View getTitleId() {
        return mToolbar;
    }


    @Override
    protected void initView() {
        gatherDynastyAdapter = new GatherDynastyAdapter(getContext());
        gatherDynastyAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                for(int i=0;i<gatherDynastyAdapter.getItemCount();i++){
                    if(gatherDynastyAdapter.getItem(i).isSelect()){
                        gatherDynastyAdapter.getItem(i).setSelect(false);
                        gatherDynastyAdapter.notifyItemChanged(i);
                    }
                }
                gatherDynastyAdapter.getItem(position).setSelect(true);
                gatherDynastyAdapter.notifyItemChanged(position);
                getPresenter().getBookListForDynasty(gatherDynastyAdapter.getItem(position).getObjectId());
            }
        });
        gatherBookAdapter = new GatherBookAdapter(getContext());
        gatherBookAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View itemView, int position) {
                Intent intent=new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra(KeyConstant.ENTITY, (Parcelable) gatherBookAdapter.getItem(position));
                IntentUtils.startActivity(getContext(),intent);
            }
        });


        rcvIndex.setAdapter(gatherDynastyAdapter);
        rcvList.setAdapter(gatherBookAdapter);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initFragment() {
        super.initFragment();
        getPresenter().getGatherDynastyDatas();

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }


    @Override
    protected GatherPresenter createPresenter() {
        return new GatherPresenter();
    }


    @Override
    public void GatherDynastyResult(List<Dynasty> dynamicList) {
        gatherDynastyAdapter.addData(dynamicList);
        Dynasty dynastySelect = null;
        for(Dynasty dynasty:dynamicList){
            if(dynasty.isSelect()){
                dynastySelect=dynasty;
            }
        }
        getPresenter().getBookListForDynasty(dynastySelect.getObjectId());
    }

    @Override
    public void getBookListForDynasty(List<Book> bookList) {
        if(bookList==null){
            gatherBookAdapter.clearData();
            multiStateView.showEmpty();
        }else {
            gatherBookAdapter.setData(bookList);
            multiStateView.showContent();
        }
    }


}