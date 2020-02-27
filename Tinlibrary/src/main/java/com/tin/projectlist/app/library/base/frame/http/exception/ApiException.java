package com.tin.projectlist.app.library.base.frame.http.exception;

import android.net.ParseException;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;


import com.google.gson.JsonSyntaxException;
import com.tin.projectlist.app.library.base.frame.http.model.ApiResult;
import com.tin.projectlist.app.library.base.utils.LogUtils;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * Created by Byk on 2017/12/12.
 *
 * @author Byk
 */
public class ApiException extends Exception {

    private static final String TAG = ApiException.class.getSimpleName();

    private final int code;
    private String message;

//    private String mDisplayMessage;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        this.message = throwable.getMessage();
    }

    public ApiException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }


    public static boolean isOk(ApiResult apiResult) {
        return apiResult != null && apiResult.isOk();
    }

    public static ApiException handleException(Throwable e) {
        LogUtils.d(TAG, e.toString()+"\n"+e);
        if (e instanceof ApiException) {
            return (ApiException) e;
        }
        ApiException ex;
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            ex = new ApiException(httpException, httpException.code());
            ex.message = httpException.code() + "：" + httpException.getMessage();
            return ex;
        } else if (e instanceof ServerException) {
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, resultException.getErrCode());
            ex.message = resultException.getMessage();
            return ex;
        } else if (e instanceof org.json.JSONException || e instanceof JsonSyntaxException ||
                e instanceof NotSerializableException || e instanceof ParseException) {
            ex = new ApiException(e, ApiErrorCode.PARSE_ERROR);
            ex.message = getErrorMsg(ex, "解析错误");
//            BugglyMgr.postException(e);
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(e, ApiErrorCode.CAST_ERROR);
            ex.message = "类型转换错误";
//            BugglyMgr.postException(e);
            return ex;
        } else if (e instanceof ConnectException) {
            ex = new ApiException(e, ApiErrorCode.NETWORK_ERROR);
            ex.message = "连接失败";
            return ex;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(e, ApiErrorCode.SSL_ERROR);
            ex.message = "证书验证失败";
//            BugglyMgr.postException(e);
            return ex;
        } else if (e instanceof ConnectTimeoutException) {
            ex = new ApiException(e, ApiErrorCode.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(e, ApiErrorCode.TIMEOUT_ERROR);
            ex.message = "连接超时";
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, ApiErrorCode.UNKNOWN_HOST_ERROR);
            ex.message = "无法解析该域名";
            return ex;
        } else if (e instanceof NullPointerException) {
            ex = new ApiException(e, ApiErrorCode.NULL_POINTER_EXCEPTION);
            ex.message = "空指针异常";
//            BugglyMgr.postException(e);
            return ex;
        } else if (e instanceof IllegalStateException) {
            ex = new ApiException(e, ApiErrorCode.STEAM_CLOSED);
            ex.message = "流错误";
//            BugglyMgr.postException(e);
            return ex;
        } else if (e instanceof NetworkOnMainThreadException) {
            ex = new ApiException(e, ApiErrorCode.THREAD_ERROR);
            ex.message = "线程错误";
//            BugglyMgr.postException(e);
            return ex;
        } else if (e instanceof IllegalArgumentException) {
            ex = new ApiException(e, ApiErrorCode.NULL_REQUEST_ERROR);
            ex.message = "参数错误";
//            BugglyMgr.postException(e);
            return ex;
        } else if (e instanceof SocketException) {
            ex = new ApiException(e, ApiErrorCode.NULL_REQUEST_ERROR);
            ex.message = "连接出错";
            return ex;
        } else {
            ex = new ApiException(e, ApiErrorCode.UNKNOWN);
            ex.message = getErrorMsg(e, "未知错误");
            if (!TextUtils.isEmpty(e.getMessage())) {
//                BugglyMgr.postException(e);
            }
            return ex;
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    private static String getErrorMsg(Throwable e, String customMsg) {
        String msg = e.getMessage();
        return TextUtils.isEmpty(msg) ? customMsg : msg;
    }

    /***
     * 上报 exception 到buggly
     * @param apiException
     */
    private void reportException(ApiException apiException) {

    }
}
