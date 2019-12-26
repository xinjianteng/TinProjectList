package com.tin.projectlist.app.model.oldBook.oldBook.entity;

import com.tin.projectlist.app.model.oldBook.constant.BmobTableConstant;

import cn.bmob.v3.BmobObject;

public class BookComment extends BmobObject {

    private String content;

    public BookComment() {
        setTableName(BmobTableConstant.TAB_BOOK_COMMENT);
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BookComment{" +
                "content='" + content + '\'' +
                '}';
    }
}
