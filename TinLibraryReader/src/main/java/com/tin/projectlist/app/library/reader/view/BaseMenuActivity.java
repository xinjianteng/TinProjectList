package com.tin.projectlist.app.library.reader.view;

import android.view.Window;
import android.view.WindowManager;

import com.tin.projectlist.app.library.base.view.base.BaseActivity;
import com.tin.projectlist.app.library.reader.controller.ReaderApplication;
import com.tin.projectlist.app.library.reader.view.widget.GBAndroidWidget;


public abstract class BaseMenuActivity extends BaseActivity {
    protected ReaderApplication mApplication;
    // 当前阅读图书实体
    protected ReadBottomMenu mBottomMenu;
    protected ReadHeadMenu mHeadMenu;

    public void closeView(int num){
        if (num==0){
            mBottomMenu.closeOtherView();
        }else {
            mHeadMenu.closeOtherView();
        }
    }

    public void showDisMenu() {
        fullScreen(!mBottomMenu.mIsDismess);
        mBottomMenu.showOrDismess();
        mHeadMenu.showOrDismess();
    }

    // epub阅读控件
    protected GBAndroidWidget mWidget;

    @Override
    protected void initData() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomMenu = new ReadBottomMenu(this);
        mHeadMenu = new ReadHeadMenu(this);

    }


    // 动态控制头部状态栏显示隐藏
    private void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams lp2 = getWindow().getAttributes();
            lp2.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp2);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /***
     * 获取阅读进度
     * @return
     */
    public abstract float getReadPro();



}
