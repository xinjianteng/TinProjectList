package com.tin.projectlist.app.library.base.frame.http.model;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;
import com.tin.projectlist.app.library.base.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Byk on 2018/1/29.
 *
 * @author Byk
 */@Keep
public class DataResult<T> {

    private int total;

    private int pages;


    @SerializedName(value = "list", alternate = {"rows", "resultList", "results","readerFontList"})
    private List<T> mDatas = new ArrayList<>();



    public DataResult() {
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setDatas(List<T> datas) {
        mDatas = datas;
    }

    @Override
    public String toString() {
        if (BuildConfig.DEBUG) {
            return "DataResult{" +
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
