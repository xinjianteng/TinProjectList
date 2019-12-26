package com.tin.projectlist.app.model.oldBook.oldBook.mvp.copy;

import android.view.View;

import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;
import com.tin.projectlist.app.model.oldBook.mvp.copy.CopyContract;
import com.tin.projectlist.app.model.oldBook.mvp.copy.CopyPresenter;

import org.xutils.view.annotation.ContentView;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的MVP Activity 类
 */
@ContentView(R.layout.activity_copy)
public final class CopyMvpActivity extends MvpActivity<com.tin.projectlist.app.model.oldBook.mvp.copy.CopyPresenter>
        implements com.tin.projectlist.app.model.oldBook.mvp.copy.CopyContract.View {

    @Override
    protected com.tin.projectlist.app.model.oldBook.mvp.copy.CopyPresenter createPresenter() {
        return new CopyPresenter();
    }


    @Override
    protected View getTitleId() {
        return null;
    }

    @Override
    protected void initData() {

    }

    public void onLogin(View view) {
        // 登录操作
        getPresenter().login("账户", "密码");
    }

    /**
     * {@link CopyContract.View}
     */

    @Override
    public void loginError(String msg) {
        toast(msg);
    }

    @Override
    public void loginSuccess(List<String> data) {
        toast("登录成功了");
    }
}