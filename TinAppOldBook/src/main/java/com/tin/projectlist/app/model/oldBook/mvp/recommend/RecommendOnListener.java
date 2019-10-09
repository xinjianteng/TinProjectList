package com.tin.projectlist.app.model.oldBook.mvp.recommend;

import com.tin.projectlist.app.model.oldBook.dto.BookDTO;

import java.util.List;

public interface RecommendOnListener {

    void onRecommendSucceed(List<BookDTO> data);

    void onRecommendFail(String msg);

}