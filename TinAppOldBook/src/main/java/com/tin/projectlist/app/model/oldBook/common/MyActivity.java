package com.tin.projectlist.app.model.oldBook.common;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.tin.projectlist.app.model.oldBook.helper.ActivityStackManager;
import com.tin.projectlist.app.model.oldBook.helper.DebugUtils;
import com.tin.projectlist.app.model.oldBook.helper.StatusManager;


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目中的 Activity 基类
 */
public abstract class MyActivity extends UIActivity
        implements OnTitleBarListener {

    @Override
    protected void initActivity() {
        super.initActivity();
        // 初始化标题栏的监听
        if (getTitleId() !=null) {
            if (getTitleId() instanceof TitleBar) {
                ((TitleBar) getTitleId()).setOnTitleBarListener(this);
            }
        }
        initOrientation();
        ActivityStackManager.getInstance().onActivityCreated(this);
    }

    /**
     * 初始化横竖屏方向，会和 LauncherTheme 主题样式有冲突，注意不要同时使用
     */
    protected void initOrientation() {
        // 当前 Activity 不能是透明的并且没有指定屏幕方向，默认设置为竖屏
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        TitleBar titleBar = getTitleBar();
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }

    @Nullable
    public TitleBar getTitleBar() {
        if (getTitleId() !=null && getTitleId() instanceof TitleBar) {
            return (TitleBar) getTitleId();
        }
        return null;
    }

    @Override
    public boolean statusBarDarkFont() {
        //返回true表示黑色字体
        return true;
    }

    /**
     * {@link OnTitleBarListener}
     */

    // TitleBar 左边的View被点击了
    @Override
    public void onLeftClick(View v) {
        onBackPressed();
    }

    // TitleBar 中间的View被点击了
    @Override
    public void onTitleClick(View v) {}

    // TitleBar 右边的View被点击了
    @Override
    public void onRightClick(View v) {}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStackManager.getInstance().onActivityDestroyed(this);
    }

    /**
     * 显示吐司
     */
    public void toast(CharSequence s) {
        ToastUtils.show(s);
    }

    public void toast(@StringRes int id) {
        ToastUtils.show(getString(id));
    }

    public void toast(Object object) {
        ToastUtils.show(object);
    }

    /**
     * 打印日志
     */
    public void log(Object object) {
        if (DebugUtils.isDebug(this)) {
            Log.v(getClass().getSimpleName(), object != null ? object.toString() : "null");
        }
    }

    /**
     * 获取当前的 Application 对象
     */
    public final MyApplication getMyApplication() {
        return (MyApplication) getApplication();
    }

    private final StatusManager mStatusManager = new StatusManager();

    /**
     * 显示加载中
     */
    public void showLoading() {
        mStatusManager.showLoading(this);
    }

    /**
     * 显示加载完成
     */
    public void showComplete() {
        mStatusManager.showComplete();
    }

    /**
     * 显示空提示
     */
    public void showEmpty() {
        mStatusManager.showEmpty(getContentView());
    }

    /**
     * 显示错误提示
     */
    public void showError() {
        mStatusManager.showError(getContentView());
    }

    /**
     * 显示自定义提示
     */
    public void showLayout(@DrawableRes int iconId, @StringRes int textId) {
        mStatusManager.showLayout(getContentView(), iconId, textId);
    }

    public void showLayout(Drawable drawable, CharSequence hint) {
        mStatusManager.showLayout(getContentView(), drawable, hint);
    }
}