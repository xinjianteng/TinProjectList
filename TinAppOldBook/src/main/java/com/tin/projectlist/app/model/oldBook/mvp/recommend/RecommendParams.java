package com.tin.projectlist.app.model.oldBook.mvp.recommend;

import org.xutils.http.RequestParams;
import org.xutils.http.annotation.HttpRequest;
import org.xutils.http.app.DefaultParamsBuilder;

/**
 * Created by wyouflf on 15/11/4.
 */
@HttpRequest(
        host = "http://prn2hb7n4.bkt.clouddn.com",
        path = "pua_recommend.txt",
        builder = DefaultParamsBuilder.class/*可选参数, 控制参数构建过程, 定义参数签名, SSL证书等*/)
public class RecommendParams extends RequestParams {

    public RecommendParams() {
    }

}
