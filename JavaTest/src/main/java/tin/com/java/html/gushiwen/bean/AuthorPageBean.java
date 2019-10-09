package tin.com.java.html.gushiwen.bean;

public class AuthorPageBean {


    private String aName;

    private String  authorHead;

    private String  des;

    private String link;

    private String poetryNum;

    private AuthorBean authorBean;


    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getAuthorHead() {
        return authorHead;
    }

    public void setAuthorHead(String authorHead) {
        this.authorHead = authorHead;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPoetryNum() {
        return poetryNum;
    }

    public void setPoetryNum(String poetryNum) {
        this.poetryNum = poetryNum;
    }

    public AuthorBean getAuthorBean() {
        return authorBean;
    }

    public void setAuthorBean(AuthorBean authorBean) {
        this.authorBean = authorBean;
    }
}
