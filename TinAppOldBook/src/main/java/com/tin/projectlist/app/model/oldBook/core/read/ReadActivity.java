package com.tin.projectlist.app.model.oldBook.core.read;

import android.view.View;

import com.tin.projectlist.app.model.oldBook.R;
import com.tin.projectlist.app.model.oldBook.mvp.MvpActivity;

import org.xutils.view.annotation.ContentView;


@ContentView(R.layout.activity_read)
public class ReadActivity extends MvpActivity<ReadPresenter> implements ReadContract.View{

    @Override
    protected ReadPresenter createPresenter() {
        return new ReadPresenter();
    }

    @Override
    protected View getTitleId() {
        return null;
    }

    @Override
    protected void initData() {

    }


}
