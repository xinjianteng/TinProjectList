package com.tin.projectlist.app.model.oldBook.core.read;

import com.tin.projectlist.app.model.oldBook.mvp.MvpPresenter;

public class ReadPresenter extends MvpPresenter<ReadContract.View> implements ReadContract.Presenter,ReadOnListener{

    ReadModel readModel;

    @Override
    public void start() {
        readModel=new ReadModel();
        readModel.setListener(this);
    }

}
