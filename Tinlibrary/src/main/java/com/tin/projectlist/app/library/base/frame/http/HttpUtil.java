package com.tin.projectlist.app.library.base.frame.http;

import android.text.TextUtils;


import com.tin.projectlist.app.library.base.utils.LogUtils;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.internal.Util;
import okio.Buffer;

/**
 * @package : com.cliff.libs.base.http
 * @description :
 * Created by chenhx on 2018/4/4 11:04.
 */

public class HttpUtil {
    private static final String TAG = HttpUtil.class.getSimpleName();
    public static final String MEDIA_TYPE_TEXT = "text";
    public static final String MEDIA_TYPE_SUBTYPE_FORM = "x-www-form-urlencoded";
    public static final String MEDIA_TYPE_SUBTYPE_JSON = "json";
    public static final String MEDIA_TYPE_SUBTYPE_XML = "xml";
    public static final String MEDIA_TYPE_SUBTYPE_HTML = "html";

    public static final String UTF8 = "UTF-8";
    public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);


    public static final String CONTENT_TYPE_ZIP = "application/zip;charset=utf-8";
    public static final String CONTENT_TYPE_APK = "application/vnd.android.package-archive";
    public static final String CONTENT_TYPE_PNG = "image/png";
    public static final String CONTENT_TYPE_JPG = "image/jpg";


    public static boolean isPlainText(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }


        String type = mediaType.type();
        if (!TextUtils.isEmpty(type) && type.equals(MEDIA_TYPE_TEXT)) {
            return true;
        }
        String subType = mediaType.subtype();
        if (!TextUtils.isEmpty(subType)) {
            subType = subType.toLowerCase();
            return subType.contains(MEDIA_TYPE_SUBTYPE_FORM) || subType.contains(MEDIA_TYPE_SUBTYPE_JSON)
                    || subType.contains(MEDIA_TYPE_SUBTYPE_HTML) || subType.contains(MEDIA_TYPE_SUBTYPE_XML);
        }
        return false;
    }

    public static String transBytes(byte[] content, MediaType contentType) {
        Buffer buffer = new Buffer().write(content);
        try {
            Charset newCharset = (contentType == null) ? contentType.charset(CHARSET_UTF8) : CHARSET_UTF8;
            Charset charset = Util.bomAwareCharset(buffer, newCharset);
            return buffer.readString(charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(buffer);
        }
        return "";
    }

    public static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    /**
     * 将传递进来的参数拼接成 url
     */
    public static String createUrlFromParam(String url, Map<String, Object> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
            else sb.append("?");
            for (Map.Entry<String, Object> urlParams : params.entrySet()) {
//                String urlValues = JsonUtil.toJson(urlParams.getValue());
                String urlValues = urlParams.getValue().toString();
                //对参数进行 utf-8 编码,防止头信息传中文
                String urlValue = URLEncoder.encode(urlValues, "UTF-8");
                sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");

            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, e.getMessage());
        }
        return url;
    }
}
