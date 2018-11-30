package knowldege.app.tin.com.tinlibrary.mvp;

import android.app.Activity;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @package : com.cliff.libs.base.mvp
 * @description :
 * Created by chenhx on 2018/4/9 14:57.
 */

public class BasePresenter implements IPresenterLifecycle {
    private CompositeDisposable mTaskMgr;

    private Activity mActivity;

    public BasePresenter(Activity mActivity) {
        this.mActivity = mActivity;
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

    public void removeTask(Disposable disposable){
        if (disposable == null) {
            return;
        }
        if (mTaskMgr != null) {
            mTaskMgr.remove(disposable);
        }
    }

    @Override
    public void onDestory() {
        if (mTaskMgr != null) {
            mTaskMgr.clear();
        }
    }
}
