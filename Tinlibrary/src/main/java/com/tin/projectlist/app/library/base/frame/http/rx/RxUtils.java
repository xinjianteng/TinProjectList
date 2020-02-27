package com.tin.projectlist.app.library.base.frame.http.rx;


import com.cliff.libs.http.model.ApiResult;
import com.cliff.libs.http.model.DataResult;
import com.cliff.libs.http.rx.function.ResultToDataFunction;
import com.cliff.libs.http.rx.function.ThrowableToDataFunction;
import com.cliff.libs.util.LogUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: chenhx
 * @Date: 2018/4/4 14:36
 * @Description:
 */
public class RxUtils {

    private static final String TAG = RxUtils.class.getSimpleName();

    public static <T> ObservableTransformer<T, T> dispatch() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) {
                                LogUtils.e("doOnSubscribe" + disposable.isDisposed());
                            }
                        })
                        .doFinally(new Action() {
                            @Override
                            public void run() {
                                LogUtils.e("doFinally");
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T, E extends DataResult<T>> ObservableTransformer<ApiResult<E>, E> dispatch(
            final boolean requestInSync, final boolean responseInSync) {
        return new ObservableTransformer<ApiResult<E>, E>() {
            @Override
            public ObservableSource<E> apply(Observable<ApiResult<E>> upstream) {
                Observable<ApiResult<E>> stream = upstream;
                // Thread Change
                if (!requestInSync) {
                    stream = upstream.subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io());
                }
                if (!responseInSync) {
                    stream = stream.observeOn(AndroidSchedulers.mainThread());
                }

                return stream.map(new ResultToDataFunction<T, E>())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) {
                                LogUtils.e("doOnSubscribe" + disposable.isDisposed());
                            }
                        })
                        .doFinally(new Action() {
                            @Override
                            public void run() {
                                LogUtils.e("doFinally");
                            }
                        })
                        .onErrorResumeNext(new ThrowableToDataFunction<E>());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> transError() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.onErrorResumeNext(new ThrowableToDataFunction<T>());
            }
        };
    }


    public static <T> void runInMainThread(T item, Consumer<T> consumer) {
        Observable.just(item)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }
}
