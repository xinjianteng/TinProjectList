package com.tin.projectlist.app.model.oldBook.mvp.recommend;


import com.tin.projectlist.app.model.oldBook.dto.BookDTO;
import com.tin.projectlist.app.model.oldBook.mvp.IMvpView;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/11/17
 *    desc   : 可进行拷贝的契约类
 */
public final class RecommendContract {

    public interface View extends IMvpView {

        void recommendResult(List<BookDTO> bookDTOList);

    }

    public interface Presenter {

        void getRecommendList();

    }

}