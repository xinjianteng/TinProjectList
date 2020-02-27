package com.tin.projectlist.app.library.base.frame.http.rx.function;

import com.cliff.libs.http.exception.ApiException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @package : com.cliff.libs.base.http.rx.function
 * @description :
 * Created by chenhx on 2018/4/4 14:33.
 */

public class ThrowableToDataFunction<T> implements Function<Throwable,Observable<T>> {
    @Override
    public Observable<T> apply(Throwable throwable) {
        return Observable.error(ApiException.handleException(throwable));
    }
}
