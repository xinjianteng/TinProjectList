package com.tin.projectlist.app.model.oldBook.mvp.home;


import com.fm.openinstall.model.AppData;
import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的契约类
 */
public final class HomeContract {

    public interface View extends IMvpView {

        void installResult(AppData appData);

    }

    public interface Presenter {

        //从哪里进入的
        void installFrom();

    }

}