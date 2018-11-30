package com.tin.projectlist.app.model.knowldedge.model.main.bean.comWorkerJingjing;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "content")
public class DbJJContent {

    @Column(name = "ContentID" ,isId = true)
    private Integer ContentID;

    @Column(name ="title")
    private String title;

    @Column(name ="remark")
    private String remark;

    @Column(name ="SubjectID")
    private Integer SubjectID;

    @Column(name ="sorting")
    private Integer sorting;

    @Column(name ="ScrollTop")
    private Integer ScrollTop;

    @Column(name ="favorite")
    private Integer favorite;

    @Column(name ="last_read_time")
    private Integer last_read_time;

    public DbJJContent() {
    }




}
