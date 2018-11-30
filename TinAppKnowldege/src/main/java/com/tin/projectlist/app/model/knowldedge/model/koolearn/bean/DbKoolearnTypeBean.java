package com.tin.projectlist.app.model.knowldedge.model.koolearn.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.List;

@Table(name = "TypeBean")
public class DbKoolearnTypeBean {

    /**
     * name = "id"：数据库表中的一个字段
     * isId = true：是否是主键
     * autoGen = true：是否自动增长
     * property = "NOT NULL"：添加约束
     */
    @Column(name = "id",isId = true,autoGen = true,property = "NOT NULL")
    private Integer id;

    @Column(name = "typeName")
    private String typeName;

    @Column(name = "typeHref")
    private String typeHref;

    public DbKoolearnTypeBean() {
    }

    private List<TypeClassilyBean> typeClassilyBeanList;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeHref() {
        return typeHref;
    }

    public void setTypeHref(String typeHref) {
        this.typeHref = typeHref;
    }

    public List<TypeClassilyBean> getTypeClassilyBeanList() {
        return typeClassilyBeanList;
    }

    public void setTypeClassilyBeanList(List<TypeClassilyBean> typeClassilyBeanList) {
        this.typeClassilyBeanList = typeClassilyBeanList;
    }

    @Override
    public String toString() {
        return "TypeBean{" +
                "typeName='" + typeName + '\'' +
                ", typeHref='" + typeHref + '\'' +
                ", typeClassilyBeanList=" + typeClassilyBeanList +
                '}';
    }
}
