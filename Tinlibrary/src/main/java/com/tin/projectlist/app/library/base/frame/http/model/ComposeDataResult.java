package com.tin.projectlist.app.library.base.frame.http.model;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;
import com.tin.projectlist.app.library.base.BuildConfig;

import java.util.ArrayList;
import java.util.List;

@Keep
public class ComposeDataResult<T, Extend> {

    @SerializedName(value = "book")
    private T mData;

    private int total;

    private int pages;

    @SerializedName(value = "extend", alternate = {"emailData", "isAlreadyGet", "taskcentersp","paramValue"})
    private Extend extend;

    @SerializedName(value = "list", alternate = {"rows", "resultList", "results"})
    private List<T> mDatas = new ArrayList<>();


    public T getData() {
        return mData;
    }

    public void setData(T data) {
        mData = data;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
    }

    public Extend getExtend() {
        return extend;
    }

    public void setExtend(Extend extend) {
        this.extend = extend;
    }

    @Override
    public String toString() {
        if (BuildConfig.DEBUG) {
            return "DataResult{" +
                    ", mData=" + mData +
                    ", extend=" + extend +
                    ", mDatas=" + mDatas +
                    '}';
        } else {
            return super.toString();
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

}
