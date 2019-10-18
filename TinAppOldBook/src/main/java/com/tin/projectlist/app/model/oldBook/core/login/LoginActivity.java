package com.tin.projectlist.app.model.oldBook.core.login;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.library.base.helper.InputTextHelper;
import com.tin.projectlist.app.model.oldBook.BuildConfig;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.ValueConstant;
import com.tin.projectlist.app.model.oldBook.core.home.HomeActivity;
import com.tin.projectlist.app.model.oldBook.core.register.RegisterActivity;
import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;


@ContentView(R.layout.activity_login)
public class LoginActivity extends MvpActivity<LoginPresenter> implements LoginContract.View {

    @ViewInject(R.id.tabBar)
    TitleBar mToolbar;

    @ViewInject(R.id.iv_login_logo)
    ImageView mLogoView;
    @ViewInject(R.id.et_login_phone)
    EditText mPhoneView;
    @ViewInject(R.id.et_login_password)
    EditText mPasswordView;
    @ViewInject(R.id.btn_login_commit)
    Button mCommitView;


    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected View getTitleId() {
        return mToolbar;
    }

    @Override
    protected void initView() {
        new InputTextHelper.Builder(this)
                .setMain(mCommitView)
                .addView(mPhoneView)
                .addView(mPasswordView)
                .build();
    }

    @Override
    protected void initData() {
        if(BuildConfig.DEBUG){
            mPhoneView.setText("15805930942");
            mPasswordView.setText("123456");
        }
    }


    @Event({R.id.btn_login_commit, R.id.iv_login_wx})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login_commit:
                getPresenter().login(mPhoneView.getText().toString(), mPasswordView.getText().toString());
                break;
            case R.id.iv_login_wx:
                toast(R.string.develop);
                break;
        }
    }

    @Override
    public void onRightClick(View v) {
        startActivityForResult(new Intent(this, RegisterActivity.class), new ActivityCallback() {
            @Override
            public void onActivityResult(int resultCode, @Nullable Intent data) {
                toast(String.valueOf(resultCode));
                if(resultCode==RESULT_OK){
                    UserInfo userInfo=data.getParcelableExtra(ValueConstant.ENTITY);
                    data.getExtras().getParcelable(ValueConstant.ENTITY);
                    getPresenter().login(userInfo.getMobile(),userInfo.getMobile());
                }
            }
        });
    }

    @Override
    public void loginError(String msg) {
        toast(msg);
    }

    @Override
    public void loginSuccess(UserInfo userInfo) {
        startActivityFinish(HomeActivity.class);
    }
}
