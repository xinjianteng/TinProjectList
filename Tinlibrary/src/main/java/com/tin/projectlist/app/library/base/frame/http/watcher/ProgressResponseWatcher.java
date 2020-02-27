package com.tin.projectlist.app.library.base.frame.http.watcher;

/**
 * @package : com.cliff.libs.base.http
 * @description :
 * Created by chenhx on 2018/4/4 15:30.
 */

public interface ProgressResponseWatcher {

    /**
     * Trans Progress
     *
     * @param bytesWritten  Written.
     * @param contentLength Total Length.
     * @param done          Whether Finished.
     */
    void onUiThreadProgress(long bytesWritten, long contentLength, boolean done);

}
