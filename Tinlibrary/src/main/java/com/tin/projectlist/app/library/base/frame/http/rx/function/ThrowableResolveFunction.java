package com.tin.projectlist.app.library.base.frame.http.rx.function;


import com.tin.projectlist.app.library.base.frame.http.RetrofitUtil;
import com.tin.projectlist.app.library.base.frame.http.exception.ApiErrorCode;
import com.tin.projectlist.app.library.base.frame.http.exception.ApiException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

/**
 * @package : com.cliff.libs.base.http.rx.function
 * @description :
 * Created by chenhx on 2018/4/8 10:08.
 */

public class ThrowableResolveFunction implements Function<Observable<? extends Throwable>, Observable<?>> {


    private static final String TAG = ThrowableResolveFunction.class.getSimpleName();
    private int mCount = RetrofitUtil.DEF_RETRY_COUNT;
    private long mDelay = RetrofitUtil.DEF_RETRY_DELAY;
    private long mIncreaseDelay = RetrofitUtil.DEF_RETRY_INCREASE_DELAY;

    public ThrowableResolveFunction(int mCount, long mDelay) {
        this.mCount = mCount;
        this.mDelay = mDelay;
    }

    public ThrowableResolveFunction(int mCount, long mDelay, long mIncreaseDelay) {
        this.mCount = mCount;
        this.mDelay = mDelay;
        this.mIncreaseDelay = mIncreaseDelay;
    }

    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        return observable.zipWith(Observable.range(1, mCount + 1), Wrapper.createFunction())
                .flatMap(new Function<Wrapper, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Wrapper wrapper) {
                        int errCode = 0;
                        if (wrapper.throwable instanceof ApiException) {
                            ApiException exception = (ApiException) wrapper.throwable;
                            errCode = exception.getCode();
                        }
                        boolean apiErr = errCode == ApiErrorCode.NETWORK_ERROR
                                || errCode == ApiErrorCode.TIMEOUT_ERROR;

                        boolean notOverRetry = wrapper.index < mCount + 1;

                        boolean serverError = wrapper.throwable instanceof ConnectException ||
                                wrapper.throwable instanceof SocketTimeoutException ||
                                wrapper.throwable instanceof TimeoutException;

                        boolean needRetry = (serverError || apiErr) && notOverRetry;
                        if (needRetry) {
                            return Observable.timer(mDelay + (wrapper.index - 1) * mIncreaseDelay,
                                    TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread());
                        }
                        return Observable.error(wrapper.throwable);
                    }
                });
    }

    private static class Wrapper {

        private int index;
        private Throwable throwable;

        public Wrapper(int index, Throwable throwable) {
            this.index = index;
            this.throwable = throwable;
        }

        public static BiFunction<Throwable, Integer, Wrapper> createFunction() {
            return new BiFunction<Throwable, Integer, Wrapper>() {
                @Override
                public Wrapper apply(Throwable throwable, Integer integer) {
                    return new Wrapper(integer, throwable);
                }
            };
        }
    }
}
