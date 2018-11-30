package com.tin.projectlist.app.model.knowldedge.model.koolearn.bean;

public class TypeClassilyBean {
    private String name;
    private String herf;
    private String des;
    private String page;
    private String pageHerf;
    private String key;
    private String data;

    @Override
    public String toString() {
        return "TypeClassilyBean{" +
                "name='" + name + '\'' +
                ", herf='" + herf + '\'' +
                ", des='" + des + '\'' +
                ", page='" + page + '\'' +
                ", pageHerf='" + pageHerf + '\'' +
                ", key='" + key + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHerf() {
        return herf;
    }

    public void setHerf(String herf) {
        this.herf = herf;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageHerf() {
        return pageHerf;
    }

    public void setPageHerf(String pageHerf) {
        this.pageHerf = pageHerf;
    }
}
