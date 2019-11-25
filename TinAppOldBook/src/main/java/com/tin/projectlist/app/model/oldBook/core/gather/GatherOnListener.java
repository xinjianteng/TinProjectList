package com.tin.projectlist.app.model.oldBook.core.gather;

import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.entity.Dynasty;

import java.util.List;

public interface GatherOnListener {

    void onGatherDynastySucceed(List<Dynasty> datas);

    void onGatherDynastyFail(String msg);

    void onBookListForDynastySuccess(List<Book> datas);


}
