package tin.com.java.html.gushiwen.bean;

import java.util.List;

public class PoetryDetailEntity {

    //作者
    private String author;
    //作者连接
    private String authorHerf;
    //标题
    private String title;
    //朝代
    private String times;
    //朝代连接
    private String timesHerf;
    //内容
    private String content;
    //翻译
    private String translation;

    private String tag;

    private List<ClassilyTagEntity> classilyTagEntityList;

    private String detailHref;




    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDetailHref() {
        return detailHref;
    }

    public void setDetailHref(String detailHref) {
        this.detailHref = detailHref;
    }

    public String getTimesHerf() {
        return timesHerf;
    }

    public void setTimesHerf(String timesHerf) {
        this.timesHerf = timesHerf;
    }

    public String getAuthorHerf() {
        return authorHerf;
    }

    public void setAuthorHerf(String authorHerf) {
        this.authorHerf = authorHerf;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }


    public List<ClassilyTagEntity> getClassilyTagEntityList() {
        return classilyTagEntityList;
    }

    public void setClassilyTagEntityList(List<ClassilyTagEntity> classilyTagEntityList) {
        this.classilyTagEntityList = classilyTagEntityList;
    }

    @Override
    public String toString() {
        return "PoetryDetailEntity{" +
                "author='" + author + '\'' +
                ", authorHerf='" + authorHerf + '\'' +
                ", title='" + title + '\'' +
                ", times='" + times + '\'' +
                ", timesHerf='" + timesHerf + '\'' +
                ", content='" + content + '\'' +
                ", translation='" + translation + '\'' +
                ", tag='" + tag + '\'' +
                ", classilyTagEntityList=" + classilyTagEntityList +
                ", detailHref='" + detailHref + '\'' +
                '}';
    }
}
