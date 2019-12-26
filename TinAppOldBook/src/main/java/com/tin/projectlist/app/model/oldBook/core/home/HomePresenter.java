package com.tin.projectlist.app.model.oldBook.core.home;


import com.tin.projectlist.app.model.oldBook.core.home.HomeContract;
import com.tin.projectlist.app.model.oldBook.core.home.HomeModel;
import com.tin.projectlist.app.model.oldBook.core.home.HomeOnListener;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的业务处理类
 */
public final class HomePresenter extends MvpPresenter<HomeContract.View>
        implements HomeContract.Presenter, HomeOnListener {

    private HomeModel mModel;

    @Override
    public void start() {
        mModel = new HomeModel();
    }


    @Override
    public void initConfig() {


    }



}