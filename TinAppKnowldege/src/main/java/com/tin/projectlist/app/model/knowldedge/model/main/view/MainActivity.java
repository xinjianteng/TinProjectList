package com.tin.projectlist.app.model.knowldedge.model.main.view;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseAct;
import com.tin.projectlist.app.model.knowldedge.model.main.adapter.SectionsPagerAdapter;

public class MainActivity extends BaseAct {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private TabLayout tabLayout;


    @Override
    protected void initToolbar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        toolbar=findViewById(R.id.toolbar);
        mViewPager=findViewById(R.id.container);
        tabLayout=findViewById(R.id.tabs);

        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    protected void setValue() {

    }

    @Override
    protected void setListener() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
