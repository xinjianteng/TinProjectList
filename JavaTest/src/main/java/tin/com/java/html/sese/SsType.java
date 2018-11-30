package tin.com.java.html.sese;

import java.util.List;

import tin.com.java.html.xindongfang.TypeClassilyBean;

public class SsType {


    private String name;
    private String href;

    @Override
    public String toString() {
        return "GuShiWenBean{" +
                "name='" + name + '\'' +
                ", href='" + href + '\'' +
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


}
