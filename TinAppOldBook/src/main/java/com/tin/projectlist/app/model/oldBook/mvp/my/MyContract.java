package com.tin.projectlist.app.model.oldBook.mvp.my;

import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

public class MyContract {

    public interface View extends IMvpView{

        void showMyInfo();

    }


    public interface Presenter{

        void getMyInfo();

    }

}
