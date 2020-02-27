package com.tin.projectlist.app.library.base.frame.http.watcher;

import com.cliff.libs.http.exception.ApiException;

/**
 * @package : com.cliff.libs.base.http.watcher
 * @description :
 * Created by chenhx on 2018/4/4 15:31.
 */

public abstract class BaseWatcher<E> extends ResponseWatcher<E> {

    /**
     * OnError
     *
     * @param e exception
     */
    @Override
    public abstract void onError(ApiException e);

    /**
     * onSuccess
     *
     * @param result Response Data
     */
    @Override
    public abstract void onSuccess(E result);
}
