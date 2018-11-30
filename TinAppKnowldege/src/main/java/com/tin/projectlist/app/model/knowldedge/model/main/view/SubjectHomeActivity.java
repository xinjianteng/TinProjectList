package com.tin.projectlist.app.model.knowldedge.model.main.view;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.Log;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.Constant;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseListAct;
import com.tin.projectlist.app.model.knowldedge.model.main.adapter.SubjectHomeAdapter;
import com.tin.projectlist.app.model.knowldedge.model.main.bean.comMoyun.DbMoYunContent;
import com.tin.projectlist.app.model.knowldedge.model.utils.ToastUtil;
import com.tin.projectlist.app.model.knowldedge.model.utils.XutilsDb;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.Collection;
import java.util.List;


public class SubjectHomeActivity extends BaseListAct {

    private final  String Tag=getClass().getName();

    private SubjectHomeAdapter mAdapter;
    List<DbMoYunContent> dbContentTList;
    String  dbName;
    String subjectTitle="";
    String dbFileSavePath;
    String dbFileDownloadPath;


    private Collection<DbMoYunContent> initData() {
        try {
            dbContentTList =x.getDb(XutilsDb.getHighScoolChiness(this,dbName)).selector(DbMoYunContent.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbContentTList;
    }


    @Override
    protected void setValue() {
        dbName=getIntent().getStringExtra("dbName");
        subjectTitle=getIntent().getStringExtra("subjectTitle");
        getSupportActionBar().setTitle(subjectTitle);
        dbFileSavePath=getExternalFilesDir("")+"/db/"+dbName;
        dbFileDownloadPath= Constant.QINIU_PATH_HOST+dbName+".db";

        rcv_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_list.setItemAnimator(new DefaultItemAnimator());
        rcv_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mAdapter = new SubjectHomeAdapter(R.layout.activity_subject_home_item);
        rcv_list.setAdapter(mAdapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rcv_list);
        if(!new File(dbFileSavePath).exists()){
            RequestParams params = new RequestParams(dbFileDownloadPath);
            params.setSaveFilePath(dbFileSavePath);
            params.setCacheDirName(getExternalCacheDir().getPath()+"/db/");
            //自动为文件命名
            params.setAutoRename(false);
            x.http().post(params, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File result) {
                    ToastUtil.show(SubjectHomeActivity.this,"加载完毕");
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                }
                @Override
                public void onCancelled(Callback.CancelledException cex) {
                }
                @Override
                public void onFinished() {
                    mAdapter.refresh(initData());
                }
                //网络请求之前回调
                @Override
                public void onWaiting() {
                }
                //网络请求开始的时候回调
                @Override
                public void onStarted() {
                    ToastUtil.show(SubjectHomeActivity.this,"首次加载"+subjectTitle+"学科，初始化数据中...");
                }
                //下载的时候不断回调的方法
                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    //当前进度和文件总大小
                    Log.e("JAVA","current："+ current +"，total："+total);
                    int progress = (int) (current * 100 / total);//取十位
                    ToastUtil.show(SubjectHomeActivity.this,"首次加载"+subjectTitle+"学科，初始化数据中..."+progress+"%");
                }
            });
        }else {
            initData();
        }

    }

    @Override
    protected void setListener() {

    }
}
