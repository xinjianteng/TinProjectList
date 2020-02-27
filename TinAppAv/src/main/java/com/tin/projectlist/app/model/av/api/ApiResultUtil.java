package com.tin.projectlist.app.model.av.api;

import com.cliff.libs.http.exception.ApiException;
import com.cliff.libs.http.model.ApiResult;

/**
 * 2019/12/20
 * author : chx
 * description :
 */
public class ApiResultUtil {
    //发行平台图书未上架
    public static final int BOOK_UNUPPER_SHELF = 2005;
    //发行平台图书数量不足
    public static final int BOOK_NOT_ENOUGH = 2006;

    public static ApiResult getApiResult(ApiException apiException) {
        ApiResult apiResult = new ApiResult();
        apiResult.setStatus(apiException.getCode());
        apiResult.setMessage(apiException.getMessage());
        return apiResult;
    }

    public static ApiResult getApiResult(Throwable throwable) {
        ApiException apiException = ApiException.handleException(throwable);
        ApiResult apiResult = new ApiResult();
        apiResult.setStatus(apiException.getCode());
        apiResult.setMessage(apiException.getMessage());
        return apiResult;
    }
}
