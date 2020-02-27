package com.tin.projectlist.app.library.base.frame.http.rx.function;


import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

public class StringParserFunction<E> implements Function<ResponseBody, String>{

    private static final String TAG = StringParserFunction.class.getSimpleName();

    @Override
    public String apply(ResponseBody responseBody) throws Exception {
        return responseBody.string();
    }


}
