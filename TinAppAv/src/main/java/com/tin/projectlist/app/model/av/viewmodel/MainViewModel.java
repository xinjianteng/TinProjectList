package com.tin.projectlist.app.model.av.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import com.tin.projectlist.app.library.base.frame.PageEntity;
import com.tin.projectlist.app.library.base.frame.http.exception.ApiException;
import com.tin.projectlist.app.library.base.frame.http.model.DataResult;
import com.tin.projectlist.app.library.base.frame.result.ApiPageResult;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseViewModel;
import com.tin.projectlist.app.model.av.api.ApiResultUtil;
import com.tin.projectlist.app.model.av.entity.MainEntity;
import com.tin.projectlist.app.model.av.model.MainModel;


/**
 * 2019/12/19
 * author : chx
 * description :积分商城
 */
public class MainViewModel extends BaseViewModel {
    private MainModel mMainModel;
    private MutableLiveData<ApiPageResult<DataResult<MainEntity>>> apiResult = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        mMainModel = new MainModel();
    }

    public void getList(PageEntity pageEntity) {
        applyTask(mMainModel.getGiftCardList(pageEntity).subscribe(dataResultApiResult ->
                        apiResult.setValue(new ApiPageResult<>(pageEntity, dataResultApiResult)),
                throwable -> apiResult.setValue(new ApiPageResult<>(pageEntity, ApiResultUtil.getApiResult(ApiException.handleException(throwable))))));

    }



    public MutableLiveData<ApiPageResult<DataResult<MainEntity>>> getApiResult() {
        return apiResult;
    }


}
