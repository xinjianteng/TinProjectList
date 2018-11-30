package com.tin.projectlist.app.model.knowldedge.model.base.view;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tin.projectlist.app.model.knowldedge.R;

public abstract class BaseAppAct extends BaseAct{

    protected Toolbar mToolbar;
    protected TextView tvTitle;
    protected ImageView imgRight;
    protected ImageView imgLeft;

    @Override
    protected void initToolbar() {
        mToolbar= LayoutInflater.from(this).inflate(R.layout.view_tool_bar,null).findViewById(R.id.toolbar);
        if(getSupportActionBar()==null&&mToolbar!=null){
            setSupportActionBar(mToolbar);
        }
        imgLeft = mToolbar.findViewById(R.id.img_left);
        tvTitle =mToolbar. findViewById(R.id.tv_title);
        imgRight =mToolbar. findViewById(R.id.img_right);
        imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }




}
