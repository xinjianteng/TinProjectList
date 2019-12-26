package com.tin.projectlist.app.model.oldBook.oldBook.common;

import com.tin.projectlist.app.model.oldBook.common.JsonResponseParser;

import org.xutils.http.annotation.HttpResponse;

@HttpResponse(parser = JsonResponseParser.class)
public class BaseResponse {

    private int status;
    private String msg;
    private Object data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }


}
