package com.tin.projectlist.app.model.knowldedge.model.main.bean.comMoyun;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "question")
public class DbMoYunQuestion {

    @Column(name = "id" ,isId = true)
    private Integer id;

    @Column(name ="groupTitle")
    private String groupTitle;

    @Column(name ="intention")
    private String intention;

    @Column(name ="year")
    private String year;

    @Column(name ="title")
    private String title;

    @Column(name ="qa")
    private String qa;

    @Column(name ="qb")
    private String qb;

    @Column(name ="qc")
    private String qc;

    @Column(name ="qd")
    private String qd;

    @Column(name ="answer")
    private String answer;

    @Column(name ="explan")
    private String explan;

    @Column(name ="tag")
    private String tag;

    @Column(name ="type")
    private Integer type;

    @Column(name ="picUrl")
    private String picUrl;

    @Column(name ="isCollection")
    private Integer isCollection;


    public DbMoYunQuestion() {
    }

    @Override
    public String toString() {
        return "DbMoYunQuestion{" +
                "id=" + id +
                ", groupTitle='" + groupTitle + '\'' +
                ", intention='" + intention + '\'' +
                ", year='" + year + '\'' +
                ", title='" + title + '\'' +
                ", qa='" + qa + '\'' +
                ", qb='" + qb + '\'' +
                ", qc='" + qc + '\'' +
                ", qd='" + qd + '\'' +
                ", answer='" + answer + '\'' +
                ", explan='" + explan + '\'' +
                ", tag='" + tag + '\'' +
                ", type=" + type +
                ", picUrl='" + picUrl + '\'' +
                ", isCollection=" + isCollection +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQa() {
        return qa;
    }

    public void setQa(String qa) {
        this.qa = qa;
    }

    public String getQb() {
        return qb;
    }

    public void setQb(String qb) {
        this.qb = qb;
    }

    public String getQc() {
        return qc;
    }

    public void setQc(String qc) {
        this.qc = qc;
    }

    public String getQd() {
        return qd;
    }

    public void setQd(String qd) {
        this.qd = qd;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplan() {
        return explan;
    }

    public void setExplan(String explan) {
        this.explan = explan;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Integer isCollection) {
        this.isCollection = isCollection;
    }
}
