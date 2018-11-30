package com.tin.projectlist.app.model.knowldedge.model.main.bean.comMoyun;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "t_knowledgepoint")
public class DbMoYunPoint {

    @Column(name = "_id" ,isId = true)
    private Integer id;

    @Column(name ="pk")
    private Integer pk;

    @Column(name ="name")
    private String name;

    @Column(name ="content")
    private String content;

    @Column(name ="is_collect")
    private Integer isCollection;

    public DbMoYunPoint() {
    }


    @Override
    public String toString() {
        return "DbMoYunPoint{" +
                "id=" + id +
                ", pk=" + pk +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", isCollection=" + isCollection +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(Integer isCollection) {
        this.isCollection = isCollection;
    }
}
