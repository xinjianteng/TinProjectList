package com.tin.projectlist.app.library.base.frame.http.rx.function;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.tin.projectlist.app.library.base.frame.http.exception.ServerException;
import com.tin.projectlist.app.library.base.frame.http.model.ApiResult;
import com.tin.projectlist.app.library.base.utils.coder.Coder;

import java.lang.reflect.Type;

import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class SimpleParserFunction<E> implements Function<ResponseBody, ApiResult<E>>{

    private static final String TAG = SimpleParserFunction.class.getSimpleName();

    private Type type;

    private boolean isCalculateMd5;

    public SimpleParserFunction(Type type) {
        this.type = type;
    }

    public SimpleParserFunction(Type type, boolean isCalculateMd5) {
        this.type = type;
        this.isCalculateMd5 = isCalculateMd5;
    }

    @Override
    public ApiResult<E> apply(ResponseBody responseBody) throws Exception {
        String json;
        try {
            json = responseBody.string();

            if (!TextUtils.isEmpty(json)) {
                ApiResult<E> apiResult = new Gson().fromJson(json, type);
                if (apiResult.isOk()) {
                    if (isCalculateMd5) {
                        apiResult.setMd5Value(Coder.encryptMD5(json));
                    }
                    return apiResult;
                } else {
                    throw new ServerException(apiResult.getStatus(), apiResult.getMessage());
                }
            }
        } finally {
            responseBody.close();
        }
        throw new ServerException(500, json);
    }


}
