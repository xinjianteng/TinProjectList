package com.tin.projectlist.app.model.oldBook.core.gather;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tin.projectlist.app.model.oldBook.constant.BmobTableConstant;
import com.tin.projectlist.app.model.oldBook.core.gather.GatherOnListener;
import com.tin.projectlist.app.model.oldBook.entity.Book;
import com.tin.projectlist.app.model.oldBook.entity.Dynasty;
import com.tin.projectlist.app.model.oldBook.mvp.MvpModel;

import org.json.JSONArray;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/11/17
 * desc   : 可进行拷贝的接口实现类
 */
public final class GatherModel extends MvpModel<GatherOnListener> {


    public GatherModel() {
        // 在这里做一些初始化操作
    }

    public void getGatherDynastyDatas() {
        BmobQuery query = new BmobQuery(BmobTableConstant.TAB_DYNASTY);
        query.order("createdAt");
        //v3.5.0版本提供`findObjectsByTable`方法查询自定义表名的数据
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    List<Dynasty> dynastyList = new Gson().fromJson(ary.toString(), new TypeToken<List<Dynasty>>() {
                    }.getType());
                    getListener().onGatherDynastySucceed(dynastyList);
                } else {
                    getListener().onGatherDynastyFail("失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }


    public void getBookListForDynasty(String dynastyId, String dynastyName) {
        BmobQuery query = new BmobQuery(BmobTableConstant.TAB_BOOK_INFO);
        query.order("createdAt");
        query.addWhereEqualTo("dynasty_id", dynastyId);
        //v3.5.0版本提供`findObjectsByTable`方法查询自定义表名的数据
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    List<Book> dynastyList = new Gson().fromJson(ary.toString(), new TypeToken<List<Book>>() {
                    }.getType());
                    if(dynastyList.size()>0){
//                        for (int i = 0; i < 100; i++) {
//                            dynastyList.get(0).setDynastyName(dynastyName);
//                            dynastyList.add(dynastyList.get(0));
//                        }
                        getListener().onBookListForDynastySuccess(dynastyList);
                    }else {
                        getListener().onBookListForDynastySuccess(null);
                    }
                } else {
                }
            }
        });
    }


}