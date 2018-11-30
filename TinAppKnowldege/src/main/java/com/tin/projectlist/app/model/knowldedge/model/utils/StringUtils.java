package com.tin.projectlist.app.model.knowldedge.model.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by jingbin
 * on 16-7-12.
 * 字符串工具类
 */
public class StringUtils {



    public static String stringToEncodeUTF(String content) {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return content;
        }
    }

    public static String stringToDecoderUTF(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return content;
        }
    }




}
