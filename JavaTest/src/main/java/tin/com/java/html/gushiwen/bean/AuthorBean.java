package tin.com.java.html.gushiwen.bean;

import java.io.Serializable;

public class AuthorBean implements Serializable{

    private int authorId;
    private String authorLink;
    private String  authorHead;
    private String authorName;
    private String  authorDes;
    private String poetryNum;
    private String poetryLink;

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorLink() {
        return authorLink;
    }

    public void setAuthorLink(String authorLink) {
        this.authorLink = authorLink;
    }

    public String getAuthorHead() {
        return authorHead;
    }

    public void setAuthorHead(String authorHead) {
        this.authorHead = authorHead;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorDes() {
        return authorDes;
    }

    public void setAuthorDes(String authorDes) {
        this.authorDes = authorDes;
    }

    public String getPoetryNum() {
        return poetryNum;
    }

    public void setPoetryNum(String poetryNum) {
        this.poetryNum = poetryNum;
    }

    public String getPoetryLink() {
        return poetryLink;
    }

    public void setPoetryLink(String poetryLink) {
        this.poetryLink = poetryLink;
    }

    @Override
    public String toString() {
        return "AuthorBean{" +
                "authorId=" + authorId +
                ", authorLink='" + authorLink + '\'' +
                ", authorHead='" + authorHead + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorDes='" + authorDes + '\'' +
                ", poetryNum='" + poetryNum + '\'' +
                ", poetryLink='" + poetryLink + '\'' +
                '}';
    }
}
