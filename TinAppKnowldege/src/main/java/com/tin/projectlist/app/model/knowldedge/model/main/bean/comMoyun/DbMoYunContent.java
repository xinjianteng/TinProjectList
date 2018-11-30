package com.tin.projectlist.app.model.knowldedge.model.main.bean.comMoyun;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 高考通
 *
 */
@Table(name = "content")
public class DbMoYunContent {

    @Column(name = "id" ,isId = true)
    private Integer id;

    @Column(name ="type")
    private Integer type;

    @Column(name ="tag")
    private String tag;

    @Column(name ="title")
    private String title;

    @Column(name ="content")
    private String content;

    @Column(name ="desc")
    private String desc;

    @Column(name ="detailUrl")
    private String detailUrl;

    @Column(name ="isCollection")
    private Integer isCollection;

    public DbMoYunContent() {
    }

    @Override
    public String toString() {
        return "DbMoYunContent{" +
                "id=" + id +
                ", type=" + type +
                ", tag='" + tag + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", desc='" + desc + '\'' +
                ", detailUrl='" + detailUrl + '\'' +
                ", isCollection=" + isCollection +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public Integer getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Integer isCollection) {
        this.isCollection = isCollection;
    }
}
