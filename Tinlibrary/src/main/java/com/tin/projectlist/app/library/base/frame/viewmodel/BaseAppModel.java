package com.tin.projectlist.app.library.base.frame.viewmodel;

import android.app.Application;


import com.tin.projectlist.app.library.base.AppCoreSprite;
import com.tin.projectlist.app.library.base.R;
import com.tin.projectlist.app.library.base.frame.event.LogoutEvent;
import com.tin.projectlist.app.library.base.frame.http.api.BaseRemoteTask;
import com.tin.projectlist.app.library.base.frame.http.exception.ApiException;
import com.tin.projectlist.app.library.base.frame.http.exception.CryptKeyNullException;
import com.tin.projectlist.app.library.base.frame.http.rx.function.CustomRetryFunction;
import com.tin.projectlist.app.library.base.utils.event.EventBusMgr;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import retrofit2.HttpException;

/**
 * 2019/9/24
 * author : chx
 * description :
 */
public class BaseAppModel extends BaseRemoteTask {
    protected Application context;
    /**
     * 服务器错误
     */
    public static final int SERVICE_RESULT_ERROR = 500;

    /**
     * token 失效
     */
    public static final int SERVICE_RESULT_NO_INTERFACE = 401;

    private static final int RECHECK_TIMES = 2;

    public BaseAppModel() {
        super(AppCoreSprite.getApp());
        this.context = AppCoreSprite.getApp();
    }

    @Override
    public CustomRetryFunction getReChecker() {
        return new CustomRetryFunction() {
            @Override
            public Observable<?> apply(Observable<? extends Throwable> observable) {
                return observable.zipWith(Observable.range(1, RECHECK_TIMES + 1), Wrapper.createFunction())
                        .flatMap(wrapper -> {
                            boolean overRetry = wrapper.index > RECHECK_TIMES;
                            if (overRetry) {
                                return Observable.error(wrapper.throwable);
                            }
                            int errCode;
                            String message = null;
                            if (wrapper.throwable.getCause() instanceof CryptKeyNullException) {
                                errCode = SERVICE_RESULT_NO_INTERFACE;
                            } else if (wrapper.throwable instanceof ApiException) {
                                ApiException apiException = (ApiException) wrapper.throwable;
                                errCode = apiException.getCode();
//                                message = iContext.getString(R.string.account_login_has_expired);
                            } else if (wrapper.throwable instanceof HttpException) {
                                HttpException throwable = (HttpException) wrapper.throwable;
                                errCode = throwable.code();
//                                message = iContext.getString(R.string.account_login_has_expired);
                            } else {
                                return Observable.error(wrapper.throwable);
                            }
                            switch (errCode) {
                                case SERVICE_RESULT_NO_INTERFACE:
                                    wrapper.throwable = new ApiException(SERVICE_RESULT_NO_INTERFACE, message);
                                    EventBusMgr.post(new LogoutEvent(LogoutEvent.LOGOUT, message));
                                    break;
                                case SERVICE_RESULT_ERROR:
                                    break;
                                default:
                                    break;
                            }

                            return Observable.error(wrapper.throwable);
                        });
            }
        };
    }

    private static class Wrapper {

        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }

        public static BiFunction<Throwable, Integer, Wrapper> createFunction() {
            return (throwable, integer) -> new Wrapper(throwable, integer);
        }
    }
}
