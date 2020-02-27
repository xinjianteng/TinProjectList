package com.tin.projectlist.app.library.base.frame.http;

import android.text.TextUtils;


import com.tin.projectlist.app.library.base.frame.http.interfaces.ServiceAddressAgent;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * @package : com.cliff.libs.base.http
 * @description :
 * Created by chenhx on 2018/4/4 10:01.
 */

public class RetrofitUtil {
    private static RetrofitUtil instance;
    private Retrofit.Builder mRetrofitBuilder;
    private String mBaseUrl = "";
    private ServiceAddressAgent mServiceAddressAgent;

    private OkHttpClient.Builder mHttpClientBuilder;
    private static final int CONNECT_TIMEOUT = 3_000;
    private static final int WRITE_TIMEOUT = 2_000;
    private static final int READ_TIMEOUT = 10_000;


    public static final int DEF_RETRY_COUNT = 2;
    public static final int DEF_RETRY_INCREASE_DELAY = 1000;
    public static final int DEF_RETRY_DELAY = 1000;

    private boolean mDebug;
    private String mTag;


    protected int retryCount = DEF_RETRY_COUNT;
    protected int retryDelay = DEF_RETRY_INCREASE_DELAY;
    protected int retryIncreaseDelay = DEF_RETRY_DELAY;


    public static synchronized RetrofitUtil getInstance() {
        if (instance == null) {
            synchronized (RetrofitUtil.class) {
                if (instance == null) {
                    instance = new RetrofitUtil();
                }
            }
        }
        return instance;
    }

    private RetrofitUtil() {
        mHttpClientBuilder = new OkHttpClient.Builder();
        mHttpClientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        mHttpClientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        mHttpClientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        mRetrofitBuilder = new Retrofit.Builder();
    }


    public static RetrofitUtil build() {
        return getInstance();
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        return build().mRetrofitBuilder;
    }


    public static OkHttpClient.Builder getOkHttpClientBuilder() {
        return build().mHttpClientBuilder;
    }

    public static Retrofit getRetrofit() {
        return build().mRetrofitBuilder.baseUrl(getBaseUrl()).build();
    }

    public static String getBaseUrl() {
        return build().mBaseUrl;
    }

    public RetrofitUtil setBaseUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            build().mBaseUrl = url;
        }
        return this;
    }

    public RetrofitUtil setServiceAddressAgent(ServiceAddressAgent serviceAddressAgent) {
        if (serviceAddressAgent != null) {
            build().mServiceAddressAgent = serviceAddressAgent;
        }
        return this;
    }

    public static String getServiceAddress(String api) {
        if (build().mServiceAddressAgent != null) {
            return build().mServiceAddressAgent.getServiceUrl(api);
        } else {
            return build().mBaseUrl;
        }
    }

    public RetrofitUtil addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            build().mHttpClientBuilder.addInterceptor(interceptor);
        }
        return this;

    }

    public static boolean isDebug() {
        return build().mDebug;
    }

    public static String getGloableTag() {
        return build().mTag;
    }


    public static RetrofitUtil debug(boolean debug) {
        return build().setDebug(debug);
    }


    public RetrofitUtil setDebug(boolean mDebug) {
        this.mDebug = mDebug;
        return this;
    }


    public static int getRetryCount() {
        return build().retryCount;
    }


    public static int getRetryDelay() {
        return build().retryDelay;
    }

    public static int getRetryIncreaseDelay() {
        return build().retryIncreaseDelay;
    }

    public RetrofitUtil setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public RetrofitUtil setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
        return this;
    }

    public RetrofitUtil setRetryIncreaseDelay(int retryIncreaseDelay) {
        this.retryIncreaseDelay = retryIncreaseDelay;
        return this;
    }
}
