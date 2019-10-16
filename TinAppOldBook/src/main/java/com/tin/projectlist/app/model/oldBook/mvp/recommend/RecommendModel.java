package com.tin.projectlist.app.model.oldBook.mvp.recommend;


import com.tin.projectlist.app.model.oldBook.common.BaseResponse;
import com.tin.projectlist.app.model.oldBook.entity.BookDTO;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

import org.xutils.common.Callback;
import org.xutils.x;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的接口实现类
 */
public final class RecommendModel extends MvpModel<RecommendOnListener> {


    public RecommendModel() {
        // 在这里做一些初始化操作
    }


    public void getRecommendList() {
        RecommendParams recommendParams=new RecommendParams();
        x.http().get(recommendParams, new Callback.CacheCallback<BaseResponse>() {
            @Override
            public boolean onCache(BaseResponse result) {
                return false;
            }

            @Override
            public void onSuccess(BaseResponse result) {
                getListener().onRecommendSucceed((List<BookDTO>) result.getData());
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                getListener().onRecommendFail("账户或密码不对哦");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }






}