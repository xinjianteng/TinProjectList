package com.tin.projectlist.app.model.knowldedge.model.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.tin.projectlist.app.model.knowldedge.R;
import com.tin.projectlist.app.model.knowldedge.model.Constant;
import com.tin.projectlist.app.model.knowldedge.KnowledgeAppLication;
import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseFrg;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.view.KoolearnTypeActivity;
import com.tin.projectlist.app.model.knowldedge.model.main.bean.SubjectBean;

import org.xutils.view.annotation.ContentView;

import java.util.List;


/***
 * 高中
 */

@ContentView(R.layout.fragment_main_highschool)
public class MainHighSchoolFragment extends BaseFrg implements View.OnClickListener{

    private static final String ARG_SECTION_NUMBER = "section_number";

    private final  String Tag=getClass().getName();

    private TextView tv_chiness;
    private TextView tv_math;
    private TextView tv_english;
    private TextView tv_physics;
    private TextView tv_chemistry;
    private TextView tv_biology;
    private TextView tv_geography;
    private TextView tv_politics;
    private TextView tv_history;



    public static MainHighSchoolFragment newInstance(int sectionNumber) {
        MainHighSchoolFragment fragment = new MainHighSchoolFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    List<SubjectBean> datas;

    @Override
    protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_highschool, container, false);

        datas = KnowledgeAppLication.Instance().gson.fromJson(Constant.subjectGson, new TypeToken<List<SubjectBean>>(){}.getType());

        tv_chiness=view.findViewById(R.id.tv_chiness);
        tv_math=view.findViewById(R.id.tv_math);
        tv_english=view.findViewById( R.id.tv_english);
        tv_physics=view.findViewById(R.id.tv_physics);
        tv_chemistry=view.findViewById(R.id.tv_chemistry);
        tv_biology=view.findViewById(R.id.tv_biology);
        tv_geography=view.findViewById(R.id.tv_geography);
        tv_politics=view.findViewById( R.id.tv_politics);
        tv_history=view.findViewById( R.id.tv_history);;

        tv_chiness.setOnClickListener(this);
        tv_math.setOnClickListener(this);
        tv_english.setOnClickListener(this);
        tv_physics.setOnClickListener(this);
        tv_chemistry.setOnClickListener(this);
        tv_biology.setOnClickListener(this);
        tv_geography.setOnClickListener(this);
        tv_politics.setOnClickListener(this);
        tv_history.setOnClickListener(this);

        return view;
    }



    public void startActivity(int position){
        Intent intent=new Intent(getActivity(),SubjectHomeActivity.class);
        intent.putExtra("dbName",datas.get(position).getSubjectDbName());
        intent.putExtra("subjectTitle",datas.get(position).getSubjectName());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_chiness:
//               startActivity(0);
               startActivity(new Intent(getActivity(), KoolearnTypeActivity.class));
                break;
            case R.id.tv_math:
                startActivity(1);
                break;
            case R.id.tv_english:
                startActivity(2);
                break;
            case R.id.tv_physics:
                startActivity(3);
                break;
            case R.id.tv_chemistry:
                startActivity(4);
                break;
            case R.id.tv_biology://生物
                startActivity(5);
                break;
            case R.id.tv_geography:
                startActivity(6);
                break;
            case R.id.tv_politics:
                startActivity(7);
                break;
            case R.id.tv_history:
                startActivity(8);
                break;
        }
    }



}
