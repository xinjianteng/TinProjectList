package tin.com.java.html.gushiwen.bean;

public class GuShiWenBean {

    //作者
    private String author;
    //标题
    private String title;
    //朝代
    private String times;
    //内容
    private String content;

    private String tag;

    private String href;


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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "GuShiWenBean{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", times='" + times + '\'' +
                ", content='" + content + '\'' +
                ", tag='" + tag + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
