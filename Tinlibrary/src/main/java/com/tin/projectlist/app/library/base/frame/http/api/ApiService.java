package com.tin.projectlist.app.library.base.frame.http.api;


import com.tin.projectlist.app.library.base.frame.http.model.ApiHeaders;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @package : com.cliff.libs.base.http
 * @description :
 * Created by chenhx on 2018/4/4 10:00.
 */

public interface ApiService {


    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> post(@Url String url, @FieldMap Map<String, Object> map);

    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> post(@Header(ApiHeaders.AUTHORIZATION) String token, @Url String url, @FieldMap Map<String, Object> map);

    @POST()
    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    Observable<ResponseBody> postBody(@Url String url, @Body Object object);

    @POST()
    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    Observable<ResponseBody> postBody(@Header(ApiHeaders.AUTHORIZATION) String token, @Url String url, @Body Object object);

    @POST()
    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    Observable<ResponseBody> postBodyGeeboo(@Header("geeboo-auth") String token, @Url String url, @Body Object object);


    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> map);

    @GET()
    Observable<ResponseBody> getGeeboo(@Header("geeboo-auth") String token, @Url String url, @QueryMap Map<String, Object> map);

    @GET()
    Observable<ResponseBody> get(@Header(ApiHeaders.AUTHORIZATION) String token, @Url String url, @QueryMap Map<String, Object> map);

    @DELETE()
    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    Observable<ResponseBody> delete(@Url String url, @Body Map<String, Object> map);

    @HTTP(method = "DELETE", hasBody = true)
    @Headers({"Content-Type: application/json;charset=UTF-8", "Accept: application/json"})
    Observable<ResponseBody> delete(@Header(ApiHeaders.AUTHORIZATION) String token, @Url String url, @Body Map<String, Object> map);

    @PUT()
    Observable<ResponseBody> put(@Url String url, @Body Map<String, Object> map);

    @PUT()
    Observable<ResponseBody> put(@Header(ApiHeaders.AUTHORIZATION) String token, @Url String url, @Body Map<String, Object> map);

}
