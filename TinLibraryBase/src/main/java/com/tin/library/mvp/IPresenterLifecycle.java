package com.tin.library.mvp;

import android.arch.lifecycle.LifecycleObserver;

/**
 * @package : com.cliff.libs.base.mvp
 * @description :
 * Created by chenhx on 2018/4/9 14:58.
 */

public interface IPresenterLifecycle extends LifecycleObserver {

    void onDestroy();

}
