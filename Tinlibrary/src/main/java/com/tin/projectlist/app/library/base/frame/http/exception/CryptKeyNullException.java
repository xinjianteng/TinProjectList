package com.tin.projectlist.app.library.base.frame.http.exception;

import java.io.IOException;

/**
 * @package : com.cliff.libs.http.exception
 * @description :
 * Created by chenhx on 2018/4/9 9:57.
 */

public class CryptKeyNullException extends IOException {
    private int code;
    private String msg;


    public CryptKeyNullException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
