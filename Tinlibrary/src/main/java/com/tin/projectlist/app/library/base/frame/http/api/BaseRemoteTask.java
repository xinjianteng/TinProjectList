package com.tin.projectlist.app.library.base.frame.http.api;

import android.content.Context;


import com.tin.projectlist.app.library.base.R;
import com.tin.projectlist.app.library.base.frame.http.RetrofitManager;
import com.tin.projectlist.app.library.base.frame.http.RetrofitUtil;
import com.tin.projectlist.app.library.base.frame.http.exception.ApiErrorCode;
import com.tin.projectlist.app.library.base.frame.http.exception.ApiException;
import com.tin.projectlist.app.library.base.frame.http.model.ApiResult;
import com.tin.projectlist.app.library.base.frame.http.rx.function.CustomRetryFunction;
import com.tin.projectlist.app.library.base.frame.http.rx.function.SimpleParserFunction;
import com.tin.projectlist.app.library.base.frame.http.rx.function.ThrowableResolveFunction;
import com.tin.projectlist.app.library.base.utils.NetworkUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Byk on 2017/12/26.
 *
 * @author Byk
 */
public class BaseRemoteTask {

    protected Context iContext;

    public BaseRemoteTask(Context context) {
        iContext = context;
    }

    protected boolean isSyncResponse() {
        return false;
    }

    public String getBaseUrl(String api) {
        return RetrofitUtil.getServiceAddress(api);
    }


    public CustomRetryFunction getReChecker() {
        return new CustomRetryFunction();
    }

    public <E> Observable<ApiResult<E>> post(String api, LinkedHashMap<String, Object> params, SimpleParserFunction<E> function) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        return RetrofitManager.getInstance(getBaseUrl(api)).postBody(api, params).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }

    public <E> Observable<ApiResult<E>> post(String api, String authorization, LinkedHashMap<String, Object> params, SimpleParserFunction<E> function) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        return RetrofitManager.getInstance(getBaseUrl(api)).postBodyGeeboo(api, authorization, params).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }


    public <E> Observable<ApiResult<E>> get(String api, HashMap hashMap, SimpleParserFunction<E> function) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        return RetrofitManager.getInstance(getBaseUrl(api)).get(api, hashMap).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }

    public <E> Observable<ApiResult<E>> get(String api, String authorization, LinkedHashMap<String, Object> params, SimpleParserFunction<E> function) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        return RetrofitManager.getInstance(getBaseUrl(api)).getGeeboo(api, authorization, params).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }


    public <E> Observable<ApiResult<E>> getZiDian(String api, LinkedHashMap<String, Object> params, Function<ResponseBody, ApiResult<E>> function) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        String url = "http://dict-co.iciba.com/";
        return RetrofitManager.getInstance(url).get(api, params).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }

    //--delete--------------------------------------------------------------------------------------------------------------------------

    public <E> Observable<ApiResult<E>> delete(String api, HashMap<String, Object> params, SimpleParserFunction<E> parser) {


        return delete(api, params, parser, true, true, false);
    }

    public <T, E> Observable<ApiResult<E>> delete(String api, HashMap<String, Object> params, SimpleParserFunction<E> function,
                                                  boolean enableReqLog, boolean enableRespLog, boolean isSyncRequest) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        return RetrofitManager.getInstance(getBaseUrl(api)).delete(api, params).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T, E> Observable<ApiResult<E>> delete(String api, String authorization, HashMap<String, Object> params, SimpleParserFunction<E> function) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        return RetrofitManager.getInstance(getBaseUrl(api)).delete(api, authorization, params).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread());
    }


    //--put--------------------------------------------------------------------------------------------------------------------------

    public <E> Observable<ApiResult<E>> put(String api, HashMap<String, Object> params, SimpleParserFunction<E> parser) {


        return put(api, params, parser, true, true, false);
    }

    public <E> Observable<ApiResult<E>> put(String api, HashMap<String, Object> params, SimpleParserFunction<E> function,
                                            boolean enableReqLog, boolean enableRespLog, boolean isSyncRequest) {
        Observable<ApiResult<E>> netConnect = isNetConnect();
        if (netConnect != null) {
            return netConnect;
        }
        return RetrofitManager.getInstance(getBaseUrl(api)).put(api, params).subscribeOn(Schedulers.io()).map(function)
                .retryWhen(new ThrowableResolveFunction(RetrofitUtil.getRetryCount(), RetrofitUtil.getRetryDelay(), RetrofitUtil.getRetryIncreaseDelay()))
                .retryWhen(getReChecker())
                .observeOn(AndroidSchedulers.mainThread());
    }


    private <E> Observable<ApiResult<E>> isNetConnect() {
        if (!NetworkUtil.isConnected(iContext)) {
            Exception exception = new Exception(iContext.getString(R.string.default_txt_msv_load_net_error));
            return Observable.create((ObservableOnSubscribe<ApiResult<E>>) emitter -> {
                if (!emitter.isDisposed()) {
                    emitter.onError(new ApiException(exception, ApiErrorCode.NETWORK_UNAVAILABLE));
                }
            }).delaySubscription(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
        return null;
    }

}
