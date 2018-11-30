package com.tin.projectlist.app.model.knowldedge.model.main.bean;


/**
 * 学科分类
 */
public class SubjectBean {

    private Integer subjectId;

    private String subjectName;

    private String subjectDbName;

    public SubjectBean(Integer subjectId, String subjectName, String subjectDbName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectDbName = subjectDbName;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectDbName() {
        return subjectDbName;
    }

    public void setSubjectDbName(String subjectDbName) {
        this.subjectDbName = subjectDbName;
    }

    @Override
    public String toString() {
        return "SubjectBean{" +
                "subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", subjectDbName='" + subjectDbName + '\'' +
                '}';
    }
}
