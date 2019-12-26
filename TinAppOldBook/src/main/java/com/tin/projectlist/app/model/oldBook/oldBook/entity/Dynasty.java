package com.tin.projectlist.app.model.oldBook.oldBook.entity;

import com.tin.projectlist.app.model.oldBook.constant.BmobTableConstant;

import cn.bmob.v3.BmobObject;

/***
 * 朝代
 */
public class Dynasty extends BmobObject {
    private int id;
    private String name;

    // 仅在客户端使用，不希望被gson序列化提交到后端云，记得用transient修饰
    private transient boolean select;

    public Dynasty() {
        setTableName(BmobTableConstant.TAB_DYNASTY);
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
