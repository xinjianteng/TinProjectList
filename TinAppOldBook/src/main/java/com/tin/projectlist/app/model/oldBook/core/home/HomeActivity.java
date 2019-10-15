package com.tin.projectlist.app.model.oldBook.core.home;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.tin.projectlist.app.library.base.BaseFragmentAdapter;
import com.tin.projectlist.app.library.base.helper.DoubleClickHelper;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.common.MyLazyFragment;
import com.tin.projectlist.app.model.oldBook.helper.ActivityStackManager;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;
import com.tin.projectlist.app.model.oldBook.mvp.recommend.RecommendFragment;
import com.tin.projectlist.app.model.oldBook.ui.fragment.HomeMeFragment;
import com.tin.projectlist.app.model.oldBook.ui.fragment.HomeShelfFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 主页界面
 */
@ContentView(R.layout.activity_home)
public final class HomeActivity extends MvpActivity<HomePresenter>
        implements ViewPager.OnPageChangeListener,
        BottomNavigationView.OnNavigationItemSelectedListener,HomeContract.View {

    @ViewInject(R.id.vp_home_pager)
    ViewPager mViewPager;
    @ViewInject(R.id.bv_home_navigation)
    BottomNavigationView mBottomNavigationView;

    private BaseFragmentAdapter<MyLazyFragment> mPagerAdapter;


    @Override
    protected View getTitleId() {
        return null;
    }

    @Override
    protected void initView() {
        mViewPager.addOnPageChangeListener(this);

        // 不使用图标默认变色
        mBottomNavigationView.setItemIconTintList(null);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        mPagerAdapter = new BaseFragmentAdapter<>(this);
        mPagerAdapter.addFragment(HomeShelfFragment.newInstance());
        mPagerAdapter.addFragment(RecommendFragment.newInstance());
        mPagerAdapter.addFragment(HomeMeFragment.newInstance());
        mViewPager.setAdapter(mPagerAdapter);
        // 限制页面数量
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
//        getPresenter().installFrom();
    }




    /**
     * {@link ViewPager.OnPageChangeListener}
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mBottomNavigationView.setSelectedItemId(R.id.menu_home);
                break;
            case 1:
                mBottomNavigationView.setSelectedItemId(R.id.home_recommend);
                break;
            case 2:
                mBottomNavigationView.setSelectedItemId(R.id.home_me);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        log("onPageScrollStateChanged   "+state);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                // 如果切换的是相邻之间的 Item 就显示切换动画，如果不是则不要动画
                mViewPager.setCurrentItem(0, mViewPager.getCurrentItem() == 1);
                return true;
            case R.id.home_recommend:
                mViewPager.setCurrentItem(1, mViewPager.getCurrentItem() == 0 || mViewPager.getCurrentItem() == 2);
                return true;
            case R.id.home_me:
                mViewPager.setCurrentItem(2, mViewPager.getCurrentItem() == 1 || mViewPager.getCurrentItem() == 3);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 回调当前 Fragment 的 onKeyDown 方法
        if (mPagerAdapter.getCurrentFragment().onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (DoubleClickHelper.isOnDoubleClick()) {
            //移动到上一个任务栈，避免侧滑引起的不良反应
            moveTaskToBack(false);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 进行内存优化，销毁掉所有的界面
                    ActivityStackManager.getInstance().finishAllActivities();
                    // 销毁进程
                    System.exit(0);
                }
            }, 300);
        } else {
            toast(getResources().getString(R.string.home_exit_hint));
        }
    }

    @Override
    protected void onDestroy() {
        mViewPager.removeOnPageChangeListener(this);
        mViewPager.setAdapter(null);
        mBottomNavigationView.setOnNavigationItemSelectedListener(null);
        super.onDestroy();
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    public boolean isSupportSwipeBack() {
        // 不使用侧滑功能
        return false;
    }


    @Override
    public void installResult(String appData) {
    }


}