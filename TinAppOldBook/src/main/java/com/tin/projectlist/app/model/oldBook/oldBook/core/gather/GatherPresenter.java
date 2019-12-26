package com.tin.projectlist.app.model.oldBook.oldBook.core.gather;

import com.tin.projectlist.app.model.oldBook.core.gather.GatherContract;
import com.tin.projectlist.app.model.oldBook.core.gather.GatherModel;
import com.tin.projectlist.app.model.oldBook.core.gather.GatherOnListener;
import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.entity.Dynasty;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

import java.util.List;

public class GatherPresenter extends MvpPresenter<com.tin.projectlist.app.model.oldBook.core.gather.GatherContract.View>
        implements GatherContract.Presenter, GatherOnListener {

    private com.tin.projectlist.app.model.oldBook.core.gather.GatherModel model;


    /**
     * P 层初始化方法
     */
    @Override
    public void start() {
        model=new GatherModel();
        model.setListener(this);

    }

    @Override
    public void getGatherDynastyDatas() {
        getView().onLoading();
        model.getGatherDynastyDatas();
    }

    @Override
    public void getBookListForDynasty(String dynastyId, String dynastyName) {
        model.getBookListForDynasty(dynastyId, dynastyName);
    }


    @Override
    public void onGatherDynastySucceed(List<Dynasty> datas) {
        getView().onComplete();
        datas.get(0).setSelect(true);
        getView().GatherDynastyResult(datas);
    }

    @Override
    public void onGatherDynastyFail(String msg) {
        getView().onComplete();
        getView().GatherDynastyResult(null);
    }

    @Override
    public void onBookListForDynastySuccess(List<Book> datas) {
        getView().getBookListForDynasty(datas);
    }

}
