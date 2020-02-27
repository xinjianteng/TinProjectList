package com.tin.projectlist.app.library.base.frame.http;

import android.os.Process;
import android.support.constraint.BuildConfig;
import android.text.TextUtils;


import com.tin.projectlist.app.library.base.frame.http.api.ApiService;
import com.tin.projectlist.app.library.base.utils.AppSprite;
import com.tin.projectlist.app.library.base.utils.DateTimeUtil;
import com.tin.projectlist.app.library.base.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.internal.platform.Platform.INFO;

public class RetrofitManager {

    private static final String TAG = RetrofitManager.class.getSimpleName();

    private static HashMap<String, RetrofitManager> mInstances = new HashMap<>();
    private final ApiService mApiService;

    public static RetrofitManager getInstance(String url) {
        RetrofitManager rm = mInstances.get(url);
        if (rm == null) {
            synchronized (RetrofitManager.class) {
                rm = mInstances.get(url);
                if (rm == null) {
                    rm = new RetrofitManager(url);
                    mInstances.put(url, rm);
                }
            }
        }
        return rm;
    }


    public static boolean DEBUG = BuildConfig.DEBUG;


    private RetrofitManager(String url) {
        OkHttpClient.Builder okHttpClientBuilder = RetrofitUtil.getOkHttpClientBuilder();
        final File logger = FileUtils.getExternalFilesDir(AppSprite.INSTANCE, "logger");
        if (!DEBUG && logger != null) {
            FileUtils.deleteFiles(logger, null);
        }
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(message -> {
            if (DEBUG && logger != null) {
                Platform.get().log(INFO, message, null);
                BufferedWriter bufferedWriter = null;
                try {
                    File file = new File(logger, DateTimeUtil.getNowDate(DateTimeUtil.DATE_FORMAT) + ".log");
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
                    bufferedWriter.write(DateTimeUtil.getNowDate(DateTimeUtil.SIMPLE_DATE_FORMAT) + " " + Process.myPid() + " " + message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (Exception e) {
                } finally {
                    StreamUtil.close(bufferedWriter);
                }
            }
        });
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(logInterceptor);
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        Retrofit.Builder retrofitBuilder = RetrofitUtil.getRetrofitBuilder();
        if (!TextUtils.isEmpty(url)) {
            retrofitBuilder.baseUrl(url);
        }
        retrofitBuilder.client(okHttpClient);
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        mApiService = retrofitBuilder.build().create(ApiService.class);

    }


    public Observable<ResponseBody> post(String url, Map<String, Object> map) {
        String token = HttpSpUtil.getToken(AppSprite.INSTANCE.getApplicationContext());
        if (!TextUtils.isEmpty(token)) {
            return mApiService.post(token, url, map);
        }
        return mApiService.post(url, map);
    }

    public Observable<ResponseBody> postBody(String url, Object object) {
        String token = HttpSpUtil.getToken(AppSprite.INSTANCE.getApplicationContext());
        if (!TextUtils.isEmpty(token)) {
            return mApiService.postBody(token, url, object);
        }
        return mApiService.postBody(url, object);
    }

    public Observable<ResponseBody> postBodyGeeboo(String url, String authorization, Object object) {
        if (!TextUtils.isEmpty(authorization)) {
            return mApiService.postBodyGeeboo(authorization, url, object);
        }
        return mApiService.postBody(url, object);
    }

    public Observable<ResponseBody> get(String url, Map<String, Object> map) {
        String token = HttpSpUtil.getToken(AppSprite.INSTANCE.getApplicationContext());
        if (!TextUtils.isEmpty(token)) {
            return mApiService.get(token, url, map);
        }
        return mApiService.get(url, map);
    }

    public Observable<ResponseBody> getGeeboo(String url, String authorization, Map<String, Object> map) {
        if (!TextUtils.isEmpty(authorization)) {
            return mApiService.getGeeboo(authorization, url, map);
        }
        return mApiService.get(url, map);
    }

    public Observable<ResponseBody> delete(String url, Map<String, Object> map) {
        String token = HttpSpUtil.getToken(AppSprite.INSTANCE.getApplicationContext());
        if (!TextUtils.isEmpty(token)) {
            return mApiService.delete(token, url, map);
        }
        return mApiService.delete(url, map);
    }

    public Observable<ResponseBody> delete(String url, String authorization, Map<String, Object> map) {
        return mApiService.delete(authorization, url, map);
    }

    public Observable<ResponseBody> put(String url, Map<String, Object> map) {
        String token = HttpSpUtil.getToken(AppSprite.INSTANCE.getApplicationContext());
        if (!TextUtils.isEmpty(token)) {
            return mApiService.put(token, url, map);
        }
        return mApiService.put(url, map);
    }


}
