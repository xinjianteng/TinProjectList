package com.tin.projectlist.app.model.oldBook.core.register;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gyf.barlibrary.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.tin.projectlist.app.library.base.utils.InputTextHelper;
import com.tin.projectlist.app.library.base.widget.CountdownView;
import com.tin.projectlist.app.model.oldBook.BuildConfig;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.constant.ValueConstant;
import com.tin.projectlist.app.model.oldBook.core.register.RegisterContract;
import com.tin.projectlist.app.model.oldBook.core.register.RegisterPresenter;
import com.tin.projectlist.app.model.oldBook.entity.UserInfo;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;


@ContentView(R.layout.activity_register)
public class RegisterActivity extends MvpActivity<RegisterPresenter> implements RegisterContract.View{

    @ViewInject(R.id.tabBar)
    TitleBar mToolbar;
    @ViewInject(R.id.et_register_phone)
    EditText mPhoneView;
    @ViewInject(R.id.cv_register_countdown)
    CountdownView mCountdownView;

    @ViewInject(R.id.et_register_code)
    EditText mCodeView;

    @ViewInject(R.id.et_register_password1)
    EditText mPasswordView1;
    @ViewInject(R.id.et_register_password2)
    EditText mPasswordView2;

    @ViewInject(R.id.btn_register_commit)
    Button mCommitView;

    @Override
    protected RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @Override
    protected View getTitleId() {
        return mToolbar;
    }

    @Override
    protected ImmersionBar statusBarConfig() {
        // 不要把整个布局顶上去
        return super.statusBarConfig().keyboardEnable(true);
    }

    @Override
    protected void initData() {
        new InputTextHelper.Builder(this)
                .setMain(mCommitView)
                .addView(mPhoneView)
                .addView(mCodeView)
                .addView(mPasswordView1)
                .addView(mPasswordView2)
                .build();
        if(BuildConfig.DEBUG){
            mPasswordView1.setText("123456");
            mPasswordView2.setText("123456");
            mPhoneView.setText("15805930942");
            mCodeView.setText("123456");
        }
    }



    @Event({R.id.cv_register_countdown,R.id.btn_register_commit})
    private void onClick(View view){
        switch (view.getId()) {
            case R.id.cv_register_countdown: //获取验证码
                if (mPhoneView.getText().toString().length() != 11) {
                    // 重置验证码倒计时控件
                    mCountdownView.resetState();
                    toast(getString(R.string.common_phone_input_error));
                } else {
                    // 获取验证码
                    toast(getString(R.string.common_send_code_succeed));
                }

                break;
            case R.id.btn_register_commit: //提交注册
                if (mPhoneView.getText().toString().length() != 11) {
                    toast(getString(R.string.common_phone_input_error));
                } else if (!mPasswordView1.getText().toString().equals(mPasswordView2.getText().toString())) {
                    toast(getString(R.string.register_password_input_error));
                } else {
                    getPresenter().register(mPhoneView.getText().toString(),mPasswordView1.getText().toString());
                }
                break;
            default:
                break;
        }
    }



    @Override
    public void registerError(String msg) {
        toast(msg);
    }

    @Override
    public void registerSuccess(UserInfo userInfo) {
        Intent intent=new Intent();
        intent.putExtra(ValueConstant.ENTITY, userInfo);
        finishResult(RESULT_OK,intent);
    }
}
