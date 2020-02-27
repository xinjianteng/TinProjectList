package com.tin.projectlist.app.model.av.model;


import com.tin.projectlist.app.library.base.frame.PageEntity;
import com.tin.projectlist.app.library.base.frame.http.model.ApiParams;
import com.tin.projectlist.app.library.base.frame.http.model.ApiResult;
import com.tin.projectlist.app.library.base.frame.http.model.DataResult;
import com.tin.projectlist.app.library.base.frame.viewmodel.BaseAppModel;
import com.tin.projectlist.app.model.av.entity.MainEntity;

import io.reactivex.Observable;

/**
 * 2019/12/20
 * author : chx
 * description :
 */
public class MainModel extends BaseAppModel {

    public Observable<ApiResult<DataResult<MainEntity>>> getGiftCardList(PageEntity pageEntity) {
        ApiParams apiParams = ApiParamsUtil.getPageParams(context, pageEntity);
        return get(ApiKeys.API_GET_SCORE_SHOP_LIST, apiParams.getUrlParamMap(), new SimpleParserFunction<>(new TypeToken<ApiResult<DataResult<MainEntity>>>() {
        }.getType()));
    }


}
