package com.tin.projectlist.app.library.base.frame.http.rx.function;


import com.tin.projectlist.app.library.base.frame.http.exception.ApiException;
import com.tin.projectlist.app.library.base.frame.http.exception.ServerException;
import com.tin.projectlist.app.library.base.frame.http.model.ApiResult;
import com.tin.projectlist.app.library.base.frame.http.model.DataResult;

import io.reactivex.functions.Function;

/**
 * @package : com.cliff.libs.base.http.rx.function
 * @description :
 * Created by chenhx on 2018/4/4 14:25.
 */

public class ResultToDataFunction<T, E extends DataResult<T>> implements Function<ApiResult<E>, E> {
    @Override
    public E apply(ApiResult<E> result) throws Exception {
        if (ApiException.isOk(result)) {
            return result.getData();
        } else {
            throw new ServerException(result.getStatus(), result.getMessage());
        }
    }
}
