package com.core.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;

import com.core.log.L;

/**
 * 访问网络的对象
 *
 * @author 韩少杰
 */
public class HttpManager {
    // 客户端访问对象
    private static final DefaultHttpClient mClient;
    // 静态块
    static {
        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
        HttpConnectionParams.setSoTimeout(params, 30 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        HttpClientParams.setRedirecting(params, false);

        HttpProtocolParams.setUserAgent(params, "Boyue/1.1");

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

        /**
         * 自定义异常恢复机制
         * 2011-11-07 韩少杰添加
         */
        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler (){
            @Override
            public boolean retryRequest(IOException exception, int executionCount,
                                        HttpContext context) {
                if (executionCount >= 5) {
                    // 如果超过最大重试次数，那么就不要继续了
                    return false;
                }
                if (exception instanceof org.apache.http.NoHttpResponseException) {
                    // 如果服务器丢掉了连接，那么就重试
                    L.i("HTTP","retry times:"+executionCount);
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {
                    // 不要重试SSL握手异常
                    return false;
                }
                HttpRequest request = (HttpRequest) context.getAttribute(
                        ExecutionContext.HTTP_REQUEST);
                boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
                if (idempotent) {
                    // 如果请求被认为是幂等的，那么就重试
                    return true;
                }
                return false;
            }
        };
        mClient = new DefaultHttpClient(manager, params);
        mClient.setHttpRequestRetryHandler(retryHandler);
    }

    /**
     * http请求
     *
     * @param head http请求头
     * @return
     * @throws IOException
     */
    public static HttpResponse execute(HttpHead head) throws IOException {
        return mClient.execute(head);
    }

    /**
     * http请求
     *
     * @param host 远程服务器信息
     * @param get 请求信息
     * @return
     * @throws IOException
     */
    public static HttpResponse execute(HttpHost host, HttpGet get) throws IOException {
        return mClient.execute(host, get);
    }

    /**
     * http 请求
     *
     * @param get 请求信息
     * @return
     * @throws IOException
     */
    public static HttpResponse execute(HttpPost post) throws IOException{
        return mClient.execute(post);
    }
    /**
     * 转换List为jsonArray字符串并处理中文编码
     * @param list 要转换的list
     * @param charset 字符编码
     * @return
     * @throws UnsodingExceupportedEncption
     */
    public static String parseJsonArrayCode(List<Map<String,Object>> list,String charset) throws UnsupportedEncodingException{
        JSONArray jsonArray1 = new JSONArray(list);
        String str = jsonArray1.toString();
        str = str.replace("\"{", "{\"");
        str = str.replace("}\"", "\"}");
        str = str.replace("=", "\":\"");
        str = str.replace(", ", "\",\"");
        str = str.replace("}\",\"{", "},{");
        return URLEncoder.encode(str, charset);
    }
    public static String parseJsonCode(String Json) throws UnsupportedEncodingException{
        Json = Json.replace("\"{", "{\"");
        Json = Json.replace("}\"", "\"}");
        Json = Json.replace("=", "\":\"");
        Json = Json.replace(", ", "\",\"");
        Json = Json.replace("}\",\"{", "},{");
        return Json;
    }
}
