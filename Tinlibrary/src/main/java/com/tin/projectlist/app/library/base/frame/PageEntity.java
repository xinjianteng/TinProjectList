package com.tin.projectlist.app.library.base.frame;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class PageEntity {

    @SerializedName("pageNo")
    private int pageNum = BaseConstant.PAGE_START_NUM;
    private int pageSize;
    private Integer pages;
    private int total;
    private int itemCount;

    public PageEntity(int pageSize) {
        this.pageSize = pageSize;
    }

    public PageEntity(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public boolean isFirstPage() {
        return pageNum == 1;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public PageEntity setPages(int pages) {
        this.pages = pages;
        return this;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean isHasMore() {
        return pages != null ? pages >= pageNum : itemCount >= pageSize;
    }

    public void incrementPageNum() {
        pageNum++;
    }

    public PageEntity resetPageNum() {
        pageNum = BaseConstant.PAGE_START_NUM;
        return this;
    }

    public void setPagesAndTotal(Integer pages, int total) {
        this.pages = pages;
        this.total = total;
    }
}