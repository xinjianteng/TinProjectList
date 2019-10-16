package com.tin.projectlist.app.model.oldBook.mvp.recommend;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.library.base.widget.XCollapsingToolbarLayout;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.entity.BookDTO;
import com.tin.projectlist.app.model.oldBook.mvp.MvpLazyFragment;
import com.tin.projectlist.app.model.oldBook.ui.adapter.HomeRecommendAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目炫酷效果示例
 */
@ContentView(R.layout.fragment_recommend)
public final class RecommendFragment extends MvpLazyFragment<RecommendPresenter>
        implements RecommendContract.View, XCollapsingToolbarLayout.OnScrimsListener {

    @ViewInject(R.id.abl_test_bar)
    AppBarLayout mAppBarLayout;

    @ViewInject(R.id.ctl_test_bar)
    XCollapsingToolbarLayout mCollapsingToolbarLayout;

    @ViewInject(R.id.t_test_title)
    Toolbar mToolbar;

    @ViewInject(R.id.tb_test_a_bar)
    TitleBar mTitleBar;

    @ViewInject(R.id.tv_test_address)
    TextView mAddressView;

    @ViewInject(R.id.tv_test_search)
    TextView mSearchView;

    @ViewInject(R.id.rcv)
    RecyclerView recyclerView;

    HomeRecommendAdapter homeRecommendAdapter;

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    protected View getTitleId() {
        return mTitleBar;
    }


    @Override
    protected void initView() {
        // 给这个ToolBar设置顶部内边距，才能和TitleBar进行对齐
        ImmersionBar.setTitleBar(getBindingActivity(), mToolbar);
        //设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);


    }

    @Override
    protected void initData() {
        homeRecommendAdapter=new HomeRecommendAdapter(getContext());
        recyclerView.setAdapter(homeRecommendAdapter);

    }

    @Override
    protected void initFragment() {
        super.initFragment();
        onLoading();
        getPresenter().getRecommendList();
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public boolean statusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }

    /**
     * CollapsingToolbarLayout 渐变回调
     *
     * {@link XCollapsingToolbarLayout.OnScrimsListener}
     */
    @Override
    public void onScrimsStateChange(boolean shown) {
        if (shown) {
            mAddressView.setTextColor(getResources().getColor(R.color.black));
            mSearchView.setBackgroundResource(R.drawable.bg_home_search_bar_gray);
            getStatusBarConfig().statusBarDarkFont(true).init();
        }else {
            mAddressView.setTextColor(getResources().getColor(R.color.white));
            mSearchView.setBackgroundResource(R.drawable.bg_home_search_bar_transparent);
            getStatusBarConfig().statusBarDarkFont(false).init();
        }
    }


    @Override
    protected RecommendPresenter createPresenter() {
        return new RecommendPresenter();
    }


    @Override
    public void recommendResult(List<BookDTO> bookDTOList) {
        showComplete();
        homeRecommendAdapter.addData(bookDTOList);
    }



}