package com.tin.projectlist.app.model.oldBook.core.gather;


import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.entity.Dynasty;
import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的契约类
 */
public final class GatherContract {

    public interface View extends IMvpView {

        void GatherDynastyResult(List<Dynasty> dynamicList);

        void getBookListForDynasty(List<Book> bookList);

    }

    public interface Presenter {


        /***
         * 获取朝代列表
         */
        void getGatherDynastyDatas();


        void getBookListForDynasty(String dynastyId, String dynastyName);
    }

}