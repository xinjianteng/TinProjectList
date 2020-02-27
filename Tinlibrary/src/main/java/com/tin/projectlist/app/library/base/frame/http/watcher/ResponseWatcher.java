package com.tin.projectlist.app.library.base.frame.http.watcher;


import com.cliff.libs.http.exception.ApiException;

/**
 * @package : com.cliff.libs.base.http.watcher
 * @description :
 * Created by chenhx on 2018/4/4 15:31.
 */

public class ResponseWatcher<E> {
    public void onStart() {
    }

    public void onCompleted() {
    }

    public void onError(ApiException e) {
    }

    public void onSuccess(E result) {
    }
}
