package com.core.support;

import java.nio.charset.Charset;

/**
 *
 * 提供isEncodingSupported方法判断该编码集java是否支持<br>
 *
 * @author jack<br>
 * @date 2013-4-12<br>
 */
public final class JavaEncodingCollection extends FilteredEncodingCollection {
    private volatile static JavaEncodingCollection ourInstance;

    public static JavaEncodingCollection Instance() {
        if (ourInstance == null) {
            ourInstance = new JavaEncodingCollection();
        }
        return ourInstance;
    }

    private JavaEncodingCollection() {
        super();
    }

    @Override
    public boolean isEncodingSupported(String name) {
        try {
            return Charset.forName(name) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
