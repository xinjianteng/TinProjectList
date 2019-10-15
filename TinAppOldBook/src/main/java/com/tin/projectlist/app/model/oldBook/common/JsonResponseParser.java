package com.tin.projectlist.app.model.oldBook.common;

import com.google.gson.Gson;

import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

import java.lang.reflect.Type;

/**
 * Created by wyouflf on 15/11/5.
 */
public class JsonResponseParser implements ResponseParser {

    private Gson gson=new Gson();

    /**
     * 转换result为resultType类型的对象
     *
     * @param resultType  返回值类型(可能带有泛型信息)
     * @param resultClass 返回值类型
     * @param result      网络返回数据(支持String, byte[], JSONObject, JSONArray, InputStream)
     * @return 请求结果, 类型为resultType
     */
    @Override
    public Object parse(Type resultType, Class resultClass, Object result) throws Throwable {
        return null;
    }

    /**
     * 检查请求参数等处理
     *
     * @param request
     */
    @Override
    public void beforeRequest(UriRequest request) throws Throwable {

    }

    /**
     * 检查请求相应头等处理
     *
     * @param request
     */
    @Override
    public void afterRequest(UriRequest request) throws Throwable {

    }

//    @Override
//    public void checkResponse(UriRequest request) throws Throwable {
//
//    }
//
//    /**
//     * 转换result为resultType类型的对象
//     *
//     * @param resultType  返回值类型(可能带有泛型信息)
//     * @param resultClass 返回值类型
//     * @param result      字符串数据
//     * @return
//     * @throws Throwable
//     */
//    @Override
//    public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {
//        // TODO: json to java bean
////        if (resultClass == List.class) {
////            // 这里只是个示例, 不做json转换.
////            List<BaiduResponse> list = new ArrayList<BaiduResponse>();
////            BaiduResponse baiduResponse = new BaiduResponse();
////            baiduResponse.setTest(result);
////            list.add(baiduResponse);
////            return list;
////            // fastJson 解析:
////            // return JSON.parseArray(result, (Class<?>) ParameterizedTypeUtil.getParameterizedType(resultType, List.class, 0));
////        } else {
////            // 这里只是个示例, 不做json转换.
////            BaiduResponse baiduResponse = new BaiduResponse();
////            baiduResponse.setTest(result);
////            return baiduResponse;
////            // fastjson 解析:
////            // return JSON.parseObject(result, resultClass);
////        }
//
//
//        return gson.fromJson(result,resultClass);
//    }


}
