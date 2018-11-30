package com.tin.projectlist.app.model.knowldedge.model.main.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tin.projectlist.app.model.knowldedge.model.main.view.MainHighSchoolFragment;
import com.tin.projectlist.app.model.knowldedge.model.main.view.MainJuniorMiddleSchoolFragment;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return MainHighSchoolFragment.newInstance(position + 1);
        }else {
            return MainJuniorMiddleSchoolFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

}
