package com.tin.projectlist.app.library.base.utils.event;

/**
 * @package : com.cliff.libs.util.event
 * @description :
 * Created by chenhx on 2018/4/9 14:40.
 */

public class BaseEvent {
    private int mType;
    private String msg;

    public BaseEvent(int mType) {
        this.mType = mType;
    }

    public BaseEvent(int mType, String msg) {
        this.mType = mType;
        this.msg = msg;
    }


    public int getType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
