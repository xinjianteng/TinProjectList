package com.tin.projectlist.app.library.base.frame.http.api;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Byk on 2017/12/25.
 *
 * @author Byk
 */
public class BaseApiResult  {

    @SerializedName(value = "ret")
    private int ret;

    @SerializedName(value = "msg")
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
