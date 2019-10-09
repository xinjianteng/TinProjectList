package com.tin.projectlist.app.model.oldBook.mvp.recommend;

import com.tin.projectlist.app.model.oldBook.dto.BookDTO;
import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

import java.util.List;

public class RecommendPresenter extends MvpPresenter<RecommendContract.View>
        implements RecommendContract.Presenter,RecommendOnListener{

    private RecommendModel model;


    /**
     * P 层初始化方法
     */
    @Override
    public void start() {
        model=new RecommendModel();
        model.setListener(this);

    }

    @Override
    public void getRecommendList() {
        model.getRecommendList();
    }


    @Override
    public void onRecommendSucceed(List<BookDTO> data) {
        getView().recommendResult(data);
    }

    @Override
    public void onRecommendFail(String msg) {
        getView().recommendResult(null);
    }

}
