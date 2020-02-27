package com.tin.projectlist.app.library.base.frame.http.watcher;


import com.cliff.libs.http.model.DataResult;

/**
 * @package : com.cliff.libs.base.http.request.body
 * @description :
 * Created by chenhx on 2018/4/8 11:20.
 */

public class ResponseWatcherWrapper<T,E extends DataResult<T>> {

    private ResponseWatcher<E> watcher;

    public ResponseWatcherWrapper(ResponseWatcher<E> watcher) {
        this.watcher = watcher;
    }

    public ResponseWatcher<E> getWatcher() {
        return watcher;
    }
}
