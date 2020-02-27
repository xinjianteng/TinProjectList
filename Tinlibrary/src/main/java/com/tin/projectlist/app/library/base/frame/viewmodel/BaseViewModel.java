package com.tin.projectlist.app.library.base.frame.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 2019/9/23
 * author : chx
 * description :
 */
public class BaseViewModel extends AndroidViewModel {

    protected Application context;
    private CompositeDisposable mTaskMgr;
    protected MutableLiveData<Object> error = new MutableLiveData<>();

    public BaseViewModel(Application application) {
        super(application);
        this.context = getApplication();
    }

    public void applyTask(Disposable disposable) {
        if (disposable == null) {
            return;
        }
        if (mTaskMgr == null) {
            mTaskMgr = new CompositeDisposable();
        }
        mTaskMgr.add(disposable);
    }

    public void removeTask(Disposable disposable) {
        if (disposable == null) {
            return;
        }
        if (mTaskMgr != null) {
            mTaskMgr.remove(disposable);
        }
    }


    public void getError(LifecycleOwner owner, Observer<Object> observer) {
        if (error == null) {
            error = new MutableLiveData<>();
        }
        if (error != null) {
            error.observe(owner, observer);
        }
    }

    @Override
    public void onCleared() {
        super.onCleared();
        if (mTaskMgr != null) {
            mTaskMgr.clear();
        }
        error = null;
    }


}
