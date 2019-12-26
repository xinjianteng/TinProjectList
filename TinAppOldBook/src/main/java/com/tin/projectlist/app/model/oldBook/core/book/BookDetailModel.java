package com.tin.projectlist.app.model.oldBook.core.book;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tin.projectlist.app.model.oldBook.constant.BmobTableConstant;
import com.tin.projectlist.app.model.oldBook.core.book.BookDetailOnListener;
import com.tin.projectlist.app.model.oldBook.entity.BookComment;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

import org.json.JSONArray;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class BookDetailModel extends MvpModel<BookDetailOnListener> {

    public BookDetailModel() {
    }


    public void getCommentForBookId(String bookId) {
        BmobQuery query = new BmobQuery(BmobTableConstant.TAB_BOOK_COMMENT);
        query.addWhereEqualTo("book_id", bookId);
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null) {
                    List<BookComment> bookCommentList = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<BookComment>>() {
                    }.getType());
                    if(bookCommentList.size()>0){
                        for (int i = 0; i < 100; i++) {
                            bookCommentList.add(bookCommentList.get(0));
                        }
                        getListener().getBookCommendList(true, bookCommentList);
                    }else {
                        getListener().getBookCommendList(false, null);
                    }
                } else {
                    getListener().getBookCommendList(false, null);
                }
            }
        });
    }
}
