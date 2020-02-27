package com.tin.projectlist.app.library.base.frame.http.watcher;

import com.cliff.libs.http.model.DataResult;

/**
 * @package : com.cliff.libs.http.watcher
 * @description :
 * Created by chenhx on 2018/4/8 15:54.
 */

public abstract class BaseProgressWatcher<E extends DataResult> extends ResponseWatcher<E> {
    /**
     * Download Progress
     *
     * @param bytesRead     Downloaded Size
     * @param contentLength File Total Size
     * @param progress      Download Progress
     * @param done          Download Complete
     */
    public abstract void update(long bytesRead, long contentLength, int progress, boolean done);

    /**
     * Download Complete
     *
     * @param path Saved Path
     */
    public abstract void onDownloaded(String path);
}
