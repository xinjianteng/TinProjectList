package tin.com.java.html.gushiwen.bean;

import java.util.List;

public class PoetryDetailEntity {

    private int poetryId;
    //作者id
    private int authorId;
    //作者连接
    private String authorLink;

    private String  authorHead;

    private String authorName;

    private String  authorDes;

    private String authorTime;

    private String authorTimeLink;

    //标题
    private String poetryName;
    //内容
    private String poetryContent;

    //翻译
    private String translation;

    private String poetryTag;

    private List<ClassilyTagEntity> classilyTagEntityList;

    private String poetryLink;

    public int getPoetryId() {
        return poetryId;
    }

    public void setPoetryId(int poetryId) {
        this.poetryId = poetryId;
    }

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

    public String getAuthorTime() {
        return authorTime;
    }

    public void setAuthorTime(String authorTime) {
        this.authorTime = authorTime;
    }

    public String getAuthorTimeLink() {
        return authorTimeLink;
    }

    public void setAuthorTimeLink(String authorTimeLink) {
        this.authorTimeLink = authorTimeLink;
    }

    public String getPoetryName() {
        return poetryName;
    }

    public void setPoetryName(String poetryName) {
        this.poetryName = poetryName;
    }

    public String getPoetryContent() {
        return poetryContent;
    }

    public void setPoetryContent(String poetryContent) {
        this.poetryContent = poetryContent;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getPoetryTag() {
        return poetryTag;
    }

    public void setPoetryTag(String poetryTag) {
        this.poetryTag = poetryTag;
    }

    public List<ClassilyTagEntity> getClassilyTagEntityList() {
        return classilyTagEntityList;
    }

    public void setClassilyTagEntityList(List<ClassilyTagEntity> classilyTagEntityList) {
        this.classilyTagEntityList = classilyTagEntityList;
    }

    public String getPoetryLink() {
        return poetryLink;
    }

    public void setPoetryLink(String poetryLink) {
        this.poetryLink = poetryLink;
    }

    @Override
    public String toString() {
        return "PoetryDetailEntity{" +
                "poetryId=" + poetryId +
                ", authorId=" + authorId +
                ", authorLink='" + authorLink + '\'' +
                ", authorHead='" + authorHead + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorDes='" + authorDes + '\'' +
                ", authorTime='" + authorTime + '\'' +
                ", authorTimeLink='" + authorTimeLink + '\'' +
                ", poetryName='" + poetryName + '\'' +
                ", poetryContent='" + poetryContent + '\'' +
                ", translation='" + translation + '\'' +
                ", poetryTag='" + poetryTag + '\'' +
                ", classilyTagEntityList=" + classilyTagEntityList +
                ", poetryLink='" + poetryLink + '\'' +
                '}';
    }
}
