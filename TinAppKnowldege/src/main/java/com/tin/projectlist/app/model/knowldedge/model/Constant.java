package com.tin.projectlist.app.model.knowldedge.model;

import android.os.Environment;

public class Constant {


    public static final  String dbPath= Environment.getExternalStorageDirectory().getPath()+"/TinAppKnowldege/" + "db";
    public static final  String QINIU_PATH_HOST= "http://pcefxv7uy.bkt.clouddn.com/";

    public static final String subjectGson="[" +
            "{\"subjectDbName\":\"high_school_chiness\",\"subjectId\":1,\"subjectName\":\"语文\"}," +
            "{\"subjectDbName\":\"high_school_math\",\"subjectId\":2,\"subjectName\":\"数学\"}," +
            "{\"subjectDbName\":\"high_school_english\",\"subjectId\":3,\"subjectName\":\"英语\"}," +
            "{\"subjectDbName\":\"high_school_physics\",\"subjectId\":4,\"subjectName\":\"物理\"}," +
            "{\"subjectDbName\":\"high_school_chemistry\",\"subjectId\":5,\"subjectName\":\"化学\"}," +
            "{\"subjectDbName\":\"high_school_biology\",\"subjectId\":6,\"subjectName\":\"生物\"}," +
            "{\"subjectDbName\":\"high_school_geography\",\"subjectId\":7,\"subjectName\":\"地理\"}," +
            "{\"subjectDbName\":\"high_school_politics\",\"subjectId\":8,\"subjectName\":\"政治\"}," +
            "{\"subjectDbName\":\"high_school_history\",\"subjectId\":9,\"subjectName\":\"历史\"}]";



    public static final String koolean_chiness="http://gaokao.koolearn.com/yuwen/";


    public final String daojishi="http://www.gaosan.com/zt/daojishi/index.html";

}
