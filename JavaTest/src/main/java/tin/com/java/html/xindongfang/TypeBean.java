package tin.com.java.html.xindongfang;

import java.util.List;

public class TypeBean {

    private String name;
    private String href;
    private List<TypeClassilyBean> classilyList;

    @Override
    public String toString() {
        return "PoetryEntity{" +
                "name='" + name + '\'' +
                ", href='" + href + '\'' +
                ", classilyList=" + classilyList +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<TypeClassilyBean> getClassilyList() {
        return classilyList;
    }

    public void setClassilyList(List<TypeClassilyBean> classilyList) {
        this.classilyList = classilyList;
    }
}
