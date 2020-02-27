package com.tin.projectlist.app.library.base.frame.activity;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ViewDataBinding;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tin.projectlist.app.library.base.R;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseViewModel;
import com.tin.projectlist.app.library.base.widget.MultiStateView;
import java.lang.ref.WeakReference;

/**
 * 2019/9/23
 * author : chx
 * description :
 */
public abstract class BaseAppModelActivtiy<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseModelActivity<VM, DB> {
    protected WeakReference<MultiStateView> multiStateView;
    protected WeakReference<SmartRefreshLayout> smartRefreshLayout;

    @Override
    protected void initPre() {
//        boolean needLogin = UriContract.getBooleanValue(getIntent(), ParcelableKey.KEY_NEED_LOGIN, false);
//        if (needLogin) {
//            UriContract.startActivityUri(this, UriContract.getLoginUri(this, getIntent().getData().toString()));
//            finish();
//            return;
//        }
    }

    public void setMultiStateView(MultiStateView multiStateView) {
        setStateViewAndRefreshLayout(multiStateView, null);
    }

    public void setSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout) {
        setStateViewAndRefreshLayout(null, smartRefreshLayout);
    }


    public void setStateViewAndRefreshLayout(MultiStateView multiStateView, SmartRefreshLayout smartRefreshLayout) {
        this.multiStateView = new WeakReference<>(multiStateView);
        this.smartRefreshLayout = new WeakReference<>(smartRefreshLayout);
        if (smartRefreshLayout != null) {
            smartRefreshLayout.setOnLoadMoreListener(view -> loadData());
            smartRefreshLayout.setOnRefreshListener(view -> loadData());
        }
        if (multiStateView != null) {
            multiStateView.setRetryListener(view -> loadData());
        }
    }


    protected void loadData() {

    }


//    protected ToolBarObservable getDefaultToolBarObservable(String title) {
//        return getDefaultToolBarObservable(title, true);
//    }
//
//    protected ToolBarObservable getDefaultToolBarObservable(String title, boolean showImgLeft) {
//        return new ToolBarObservable(this)
//                .setToolbarTitle(title)
//                .setShowImgLeft(showImgLeft);
//    }
//
//    public static class ToolBarObservable extends BaseObservable {
//
//        private BaseAppModelActivtiy mActivity;
//        private String toolbarTitle;
//        private String tvRightText;
//
//        protected boolean showToolbar = true;
//        private boolean showImgLeft;
//        private boolean showDividerLine;
//
//        private @DrawableRes
//        int imgLeftDrawableRes = R.drawable.default_back;
//
//        private boolean tvRightEnabled = true;
//
//        private int toolbarTitleColor;
//        private int toolbarBackground;
//
//        private boolean fitsSystemWindows = false;
//
//        public ToolBarObservable(BaseAppModelActivtiy activity) {
//            this.mActivity = activity;
//        }
//
//        @Bindable
//        public String getToolbarTitle() {
//            return toolbarTitle;
//        }
//
//        public ToolBarObservable setToolbarTitle(String toolbarTitle) {
//            if (!TextUtils.equals(this.toolbarTitle, toolbarTitle)) {
//                this.toolbarTitle = toolbarTitle;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.toolbarTitle);
//            }
//            return this;
//        }
//
//        @Bindable
//        public boolean isFitsSystemWindows() {
//            return fitsSystemWindows;
//        }
//
//        public ToolBarObservable setFitsSystemWindows(boolean fitsSystemWindows) {
//            if (this.fitsSystemWindows != fitsSystemWindows) {
//                this.fitsSystemWindows = fitsSystemWindows;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.fitsSystemWindows);
//            }
//            return this;
//        }
//
//        @Bindable
//        public int getImgLeftDrawableRes() {
//            return imgLeftDrawableRes;
//        }
//
//        public ToolBarObservable setImgLeftDrawableRes(int imgLeftDrawableRes) {
//            if (this.imgLeftDrawableRes != imgLeftDrawableRes) {
//                this.imgLeftDrawableRes = imgLeftDrawableRes;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.imgLeftDrawableRes);
//            }
//            return this;
//        }
//
//
//        @Bindable
//        public boolean isShowToolbar() {
//            return showToolbar;
//        }
//
//        public ToolBarObservable setShowToolbar(boolean showToolbar) {
//            if (this.showToolbar != showToolbar) {
//                this.showToolbar = showToolbar;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.showToolbar);
//            }
//            return this;
//        }
//
//
//        @Bindable
//        public boolean isShowImgLeft() {
//            return showImgLeft;
//        }
//
//        public ToolBarObservable setShowImgLeft(boolean showImgLeft) {
//            if (this.showImgLeft != showImgLeft) {
//                this.showImgLeft = showImgLeft;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.showImgLeft);
//            }
//            return this;
//        }
//
//
//        @Bindable
//        public String getTvRightText() {
//            return tvRightText;
//        }
//
//        public ToolBarObservable setTvRightText(String tvRightText) {
//            if (!TextUtils.equals(this.tvRightText, tvRightText)) {
//                this.tvRightText = tvRightText;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.tvRightText);
//            }
//            return this;
//        }
//
//        @Bindable
//        public boolean isShowDividerLine() {
//            return showDividerLine;
//        }
//
//        public ToolBarObservable setShowDividerLine(boolean showDividerLine) {
//            if (this.showDividerLine != showDividerLine) {
//                this.showDividerLine = showDividerLine;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.showDividerLine);
//            }
//            return this;
//        }
//
//        public void onImgLeftClick(View view) {
//            if (mActivity != null) {
//                mActivity.onImgLeftClick(view);
//            }
//        }
//
//
//        public void onTvRightClick(View view) {
//            if (mActivity != null) {
//                mActivity.onTvRightClick(view);
//            }
//        }
//
//        @Bindable
//        public boolean isTvRightEnabled() {
//            return tvRightEnabled;
//        }
//
//        public ToolBarObservable setTvRightEnabled(boolean tvRightEnabled) {
//            if (this.tvRightEnabled != tvRightEnabled) {
//                this.tvRightEnabled = tvRightEnabled;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.tvRightEnabled);
//            }
//            return this;
//        }
//
//        @Bindable
//        public int getToolbarTitleColor() {
//            return toolbarTitleColor;
//        }
//
//        public ToolBarObservable setToolbarTitleColor(int toolbarTitleColor) {
//            if (this.toolbarTitleColor != toolbarTitleColor) {
//                this.toolbarTitleColor = toolbarTitleColor;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.toolbarTitleColor);
//            }
//            return this;
//        }
//
//        @Bindable
//        public int getToolbarBackground() {
//            return toolbarBackground;
//        }
//
//        public ToolBarObservable setToolbarBackground(int toolbarBackground) {
//            if (this.toolbarBackground != toolbarBackground) {
//                this.toolbarBackground = toolbarBackground;
//                notifyPropertyChanged(com.tin.projectlist.app.library.base.BR.toolbarBackground);
//            }
//            return this;
//        }
//    }
//
//    public void onImgLeftClick(View view) {
//        onBackPressed();
//    }
//
//    public void onTvRightClick(View view) {
//
//    }


}
