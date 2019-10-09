package com.tin.projectlist.app.model.oldBook.mvp.home;


import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的接口实现类
 */
public final class HomeModel extends MvpModel<HomeOnListener> {


    public HomeModel() {
        // 在这里做一些初始化操作
    }

    public void fromComeIn() {
        //获取OpenInstall安装数据
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                //获取渠道数据
                String channelCode = appData.getChannel();
                //获取自定义数据
                String bindData = appData.getData();
//                Log.d("OpenInstall", "getInstall : installData = " + appData.toString());
//                getListener().installFrom();
            }
        });

    }

}