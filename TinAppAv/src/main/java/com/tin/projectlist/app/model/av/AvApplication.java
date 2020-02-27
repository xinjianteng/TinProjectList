package com.tin.projectlist.app.model.av;

import android.app.Activity;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tin.projectlist.app.library.base.AppActivityLifecycleCallbacks;
import com.tin.projectlist.app.library.base.BaseApplication;
import com.tin.projectlist.app.library.base.autosize.AutoSizeConfig;
import com.tin.projectlist.app.library.base.autosize.onAdaptListener;
import com.tin.projectlist.app.library.base.utils.AutoUtils;
import com.tin.projectlist.app.library.base.utils.LogUtils;
import com.tin.projectlist.app.model.av.utils.DynamicTimeFormat;

import java.util.Locale;

public class AvApplication extends BaseApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if(isAppMainProcess(this,getPackageName())){
            initSmartRefresh();
            registerActivityLifecycleCallbacks(new AppActivityLifecycleCallbacks());
            AutoSizeConfig.getInstance().init(this);
            AutoSizeConfig.getInstance().setCustomFragment(true)
                    .setBaseOnWidth(AutoUtils.getIsBaseOnWidth(this))
                    .setLog(BuildConfig.DEBUG)
                    .setOnAdaptListener(new onAdaptListener() {
                        @Override
                        public void onAdaptBefore(Object target, Activity activity) {
                            LogUtils.d(String.format(Locale.ENGLISH, "%s onAdaptBefore!", target.getClass().getName()));
                        }

                        @Override
                        public void onAdaptAfter(Object target, Activity activity) {
                            LogUtils.d(String.format(Locale.ENGLISH, "%s onAdaptAfter!", target.getClass().getName()));
                        }
                    });
        }


    }


    /****
     * 全局设置 smartRefreshLayout 的刷新头
     */
    private void initSmartRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.app_bg, R.color.c_ff4f4f);//全局设置主题颜色
            return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context));

        SmartRefreshLayout.setDefaultRefreshInitializer((context, layout) -> {
            layout.setEnableRefresh(false);
            layout.setEnableLoadMore(false);
        });
    }


}
