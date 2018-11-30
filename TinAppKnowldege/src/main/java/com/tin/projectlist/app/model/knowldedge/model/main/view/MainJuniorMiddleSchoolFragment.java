package com.tin.projectlist.app.model.knowldedge.model.main.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseFrg;

public class MainJuniorMiddleSchoolFragment extends BaseFrg{

    private static final String ARG_SECTION_NUMBER = "section_number";

    public MainJuniorMiddleSchoolFragment() {
    }


    public static MainJuniorMiddleSchoolFragment newInstance(int sectionNumber) {
        MainJuniorMiddleSchoolFragment fragment = new MainJuniorMiddleSchoolFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_junior_middle_school,null);
        return view;
    }
}
