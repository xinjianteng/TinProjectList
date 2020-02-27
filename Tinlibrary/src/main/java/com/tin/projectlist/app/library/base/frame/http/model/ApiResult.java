package com.tin.projectlist.app.library.base.frame.http.model;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

/**
 * @package : com.cliff.libs.base.http
 * @description :
 * Created by chenhx on 2018/4/4 13:34.
 */
@Keep
public class ApiResult<T> {
    public static final int RET_OK = 200;
    @SerializedName(value = "status", alternate = {"code"})
    private int status;
    @SerializedName(value = "message", alternate = {"msg"})
    private String message = "";
    @SerializedName(value = "data", alternate = {"result", "symbols"})
    private T data;
    private String content;
    private boolean success;

    private String md5Value;
    private int isAllLend;


    public int isAllLend() {
        return isAllLend;
    }

    public void setAllLend(int allLend) {
        isAllLend = allLend;
    }

    public boolean isOk() {
        return status == RET_OK;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMd5Value() {
        return md5Value;
    }

    public void setMd5Value(String md5Value) {
        this.md5Value = md5Value;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", content='" + content + '\'' +
                ", success=" + success +
                '}';
    }
}
