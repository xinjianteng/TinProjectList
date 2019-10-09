package com.tin.library.mvp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class BasePresenter implements IPresenterLifecycle {
    private CompositeDisposable mTaskMgr;


    public void applyTask(Disposable disposable) {
        if (disposable == null) {
            return;
        }
        if (mTaskMgr == null) {
            mTaskMgr = new CompositeDisposable();
        }
        mTaskMgr.add(disposable);
    }

    public void removeTask(Disposable disposable){
        if (disposable == null) {
            return;
        }
        if (mTaskMgr != null) {
            mTaskMgr.remove(disposable);
        }
    }

    @Override
    public void onDestroy() {
        if (mTaskMgr != null) {
            mTaskMgr.clear();
        }
    }

}
