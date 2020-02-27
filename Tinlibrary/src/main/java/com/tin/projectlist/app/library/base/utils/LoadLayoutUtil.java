package com.tin.projectlist.app.library.base.utils;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tin.projectlist.app.library.base.frame.BaseConstant;
import com.tin.projectlist.app.library.base.frame.http.exception.ApiException;
import com.tin.projectlist.app.library.base.widget.MultiStateView;

public class LoadLayoutUtil {

    public static void refreshComplete(boolean refreshResult, SmartRefreshLayout smartRefreshLayout) {
        refreshComplete(refreshResult, false, false, smartRefreshLayout);
    }

    public static void refreshComplete(boolean refreshResult, boolean enableRefresh, SmartRefreshLayout smartRefreshLayout) {
        refreshComplete(refreshResult, enableRefresh, false, smartRefreshLayout);
    }


    public static void refreshComplete(boolean refreshResult, boolean enableRefresh, boolean enableLoadMore, SmartRefreshLayout smartRefreshLayout) {
        smartRefreshLayout.finishRefresh(refreshResult); // 下拉刷新完成
        if (refreshResult) {
            smartRefreshLayout.setEnableRefresh(enableRefresh);  // 是否可以下拉刷新
            smartRefreshLayout.setEnableLoadMore(enableLoadMore); // 是否可以上拉加载更多
            smartRefreshLayout.setNoMoreData(!enableLoadMore); // 是否还有更多数据
        }

    }

    public static void refreshComplete(boolean refreshResult, boolean enableRefresh, SmartRefreshLayout smartRefreshLayout, int delay) {
        refreshComplete(refreshResult, enableRefresh, false, smartRefreshLayout, delay);
    }


    public static void refreshComplete(boolean refreshResult, boolean enableRefresh, boolean enableLoadMore, SmartRefreshLayout smartRefreshLayout, int delay) {
        smartRefreshLayout.finishRefresh(delay, refreshResult); // 下拉刷新完成
        if (refreshResult) {
            smartRefreshLayout.setEnableRefresh(enableRefresh);  // 是否可以下拉刷新
            smartRefreshLayout.setEnableLoadMore(enableLoadMore); // 是否可以上拉加载更多
            smartRefreshLayout.setNoMoreData(!enableLoadMore); // 是否还有更多数据
        }
    }

    public static void refreshOrLoadComplete(boolean refresh, boolean success, boolean enableRefresh, boolean hasMore, SmartRefreshLayout smartView) {
        if (refresh) {
            refreshComplete(success, enableRefresh, hasMore, smartView);
        } else {
            loadMoreComplete(success, !hasMore, hasMore, smartView);
        }
    }


    public static void refreshOrLoadComplete(boolean success, int pageNum, boolean hasMore, SmartRefreshLayout smartView) {
        if (pageNum == BaseConstant.PAGE_START_NUM) {
            refreshComplete(success, success, hasMore, smartView);
        } else {
            loadMoreComplete(success, !hasMore, hasMore, smartView);
        }
    }

    public static void loadMoreComplete(boolean result, boolean hasMoreData, SmartRefreshLayout smartRefreshLayout) {
        loadMoreComplete(result, hasMoreData, true, smartRefreshLayout);
    }


    public static void loadMoreComplete(boolean result, boolean noMoreData, boolean enableLoadMore, SmartRefreshLayout smartRefreshLayout) {
        smartRefreshLayout.finishLoadMore(result); // 上拉加载更多， 成功或者失败
        if (result) { // 加载成功才改变load的状态
            smartRefreshLayout.setNoMoreData(noMoreData); // 是否还有更多数据
            smartRefreshLayout.setEnableLoadMore(enableLoadMore); // 再次下来是否显示加载更多的布局
        }

    }

    public static void showResult(boolean result, MultiStateView multiStateView) {
        showResult(result, result, multiStateView);
    }

    public static void showResult(boolean result, boolean hasData, MultiStateView multiStateView) {
        showResult(result, hasData, null, multiStateView);
    }

    public static void showResult(boolean result, boolean hasData, ApiException apiException, MultiStateView multiStateView) {
        if (hasData) {
            multiStateView.showContent();
        } else if (result) {
            multiStateView.showEmpty();
        } else {
            if (apiException != null) {
                multiStateView.setErrorText(apiException.getMessage());
            }
            multiStateView.showError();
        }
    }


}
