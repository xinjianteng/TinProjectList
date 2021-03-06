package com.tin.projectlist.app.model.oldBook.core.splash;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.gyf.barlibrary.BarHide;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.library.base.view.MyActivity;
import com.tin.projectlist.app.model.oldBook.core.home.HomeActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;


/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 启动界面
 */
@ContentView(R.layout.activity_launcher)
public final class LauncherActivity extends MyActivity
        implements OnPermission {


    @ViewInject(R.id.iv_launcher_bg)
    private View mImageView;
    @ViewInject(R.id.iv_launcher_icon)
    private View mIconView;

    private static final int ANIM_TIME = 300;

    @Override
    protected View getTitleId() {
        return null;
    }

    @Override
    protected void initData() {
        //初始化动画
        initStartAnim();
        //设置状态栏和导航栏参数
        getStatusBarConfig()
                .fullScreen(true)//有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)//隐藏状态栏
                .transparentNavigationBar()//透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                .init();
    }


    /**
     * 启动动画
     */
    private void initStartAnim() {
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.4f, 1.0f);
        aa.setDuration(ANIM_TIME * 2);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                requestPermission();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mImageView.startAnimation(aa);

        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIM_TIME);
        mIconView.startAnimation(sa);
    }

    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .request(this);
    }

    /**
     * {@link OnPermission}
     */
    @Override
    public void hasPermission(List<String> granted, boolean isAll) {
        startActivity(HomeActivity.class);
        finish();
    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {
        if (quick) {
            toast("没有权限访问文件，请手动授予权限");
            XXPermissions.gotoPermissionSettings(LauncherActivity.this, true);
        } else {
            toast("请先授予文件读写权限");
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestPermission();
                }
            }, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (XXPermissions.isHasPermission(LauncherActivity.this, Permission.Group.STORAGE)) {
            hasPermission(null, true);
        } else {
            requestPermission();
        }
    }

    @Override
    public boolean isSupportSwipeBack() {
        //不使用侧滑功能
        return false;
    }


    /**
     * Android 8.0踩坑记录：Only fullscreen opaque activities can request orientation
     * https://www.jianshu.com/p/d0d907754603
     */
    @Override
    protected void initOrientation() {
        // 注释父类的固定屏幕方向方法
        // super.initOrientation();
    }
}