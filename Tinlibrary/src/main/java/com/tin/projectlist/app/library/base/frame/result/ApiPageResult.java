package com.tin.projectlist.app.library.base.frame.result;


import com.tin.projectlist.app.library.base.frame.PageEntity;
import com.tin.projectlist.app.library.base.frame.http.model.ApiResult;

/**
 * 2019/10/11
 * author : chx
 * description :
 */
public class ApiPageResult<T> {
    private PageEntity pageEntity;
    private ApiResult<T> apiResult;

    public ApiPageResult(PageEntity pageEntity, ApiResult<T> apiResult) {
        this.pageEntity = pageEntity;
        this.apiResult = apiResult;
    }

    public PageEntity getPageEntity() {
        return pageEntity;
    }

    public void setPageEntity(PageEntity pageEntity) {
        this.pageEntity = pageEntity;
    }

    public ApiResult<T> getApiResult() {
        return apiResult;
    }

    public void setApiResult(ApiResult<T> apiResult) {
        this.apiResult = apiResult;
    }
}
