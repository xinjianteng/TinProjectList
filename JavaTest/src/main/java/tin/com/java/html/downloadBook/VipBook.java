package tin.com.java.html.downloadBook;

import java.math.BigInteger;

public class VipBook {


    private Integer shelf_status;
    private BigInteger create_time;
    private String file_name;
    private String cover_path;
    protected Integer vip_receive_book_id;
    protected Integer book_id;
    private String book_name;
    private String book_author;
    private String book_intro;
    private Integer firstTagId;
    private Integer second_tag_id;
    private String third_tag_name;
    private String book_age;
    private Integer indexNo;
    private Integer bookCount;
    private Integer createBy;
    private BigInteger modify_time;
    private Integer modify_by;
    private String firstTagName;
    private String secondTagName;
    private BigInteger createTimeStart;
    private BigInteger createTimeEnd;

    @Override
    public String toString() {
        return "VipBook{" +
                "shelf_status=" + shelf_status +
                ", create_time=" + create_time +
                ", file_name='" + file_name + '\'' +
                ", cover_path='" + cover_path + '\'' +
                ", vip_receive_book_id=" + vip_receive_book_id +
                ", book_id=" + book_id +
                ", book_name='" + book_name + '\'' +
                ", book_author='" + book_author + '\'' +
                ", book_intro='" + book_intro + '\'' +
                ", firstTagId=" + firstTagId +
                ", second_tag_id=" + second_tag_id +
                ", third_tag_name='" + third_tag_name + '\'' +
                ", book_age='" + book_age + '\'' +
                ", indexNo=" + indexNo +
                ", bookCount=" + bookCount +
                ", createBy=" + createBy +
                ", modify_time=" + modify_time +
                ", modify_by=" + modify_by +
                ", firstTagName='" + firstTagName + '\'' +
                ", secondTagName='" + secondTagName + '\'' +
                ", createTimeStart=" + createTimeStart +
                ", createTimeEnd=" + createTimeEnd +
                '}';
    }

    public Integer getShelf_status() {
        return shelf_status;
    }

    public void setShelf_status(Integer shelf_status) {
        this.shelf_status = shelf_status;
    }

    public BigInteger getCreate_time() {
        return create_time;
    }

    public void setCreate_time(BigInteger create_time) {
        this.create_time = create_time;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getCover_path() {
        return cover_path;
    }

    public void setCover_path(String cover_path) {
        this.cover_path = cover_path;
    }

    public Integer getVip_receive_book_id() {
        return vip_receive_book_id;
    }

    public void setVip_receive_book_id(Integer vip_receive_book_id) {
        this.vip_receive_book_id = vip_receive_book_id;
    }

    public Integer getBook_id() {
        return book_id;
    }

    public void setBook_id(Integer book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getBook_intro() {
        return book_intro;
    }

    public void setBook_intro(String book_intro) {
        this.book_intro = book_intro;
    }

    public Integer getFirstTagId() {
        return firstTagId;
    }

    public void setFirstTagId(Integer firstTagId) {
        this.firstTagId = firstTagId;
    }

    public Integer getSecond_tag_id() {
        return second_tag_id;
    }

    public void setSecond_tag_id(Integer second_tag_id) {
        this.second_tag_id = second_tag_id;
    }

    public String getThird_tag_name() {
        return third_tag_name;
    }

    public void setThird_tag_name(String third_tag_name) {
        this.third_tag_name = third_tag_name;
    }

    public String getBook_age() {
        return book_age;
    }

    public void setBook_age(String book_age) {
        this.book_age = book_age;
    }

    public Integer getIndexNo() {
        return indexNo;
    }

    public void setIndexNo(Integer indexNo) {
        this.indexNo = indexNo;
    }

    public Integer getBookCount() {
        return bookCount;
    }

    public void setBookCount(Integer bookCount) {
        this.bookCount = bookCount;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public BigInteger getModify_time() {
        return modify_time;
    }

    public void setModify_time(BigInteger modify_time) {
        this.modify_time = modify_time;
    }

    public Integer getModify_by() {
        return modify_by;
    }

    public void setModify_by(Integer modify_by) {
        this.modify_by = modify_by;
    }

    public String getFirstTagName() {
        return firstTagName;
    }

    public void setFirstTagName(String firstTagName) {
        this.firstTagName = firstTagName;
    }

    public String getSecondTagName() {
        return secondTagName;
    }

    public void setSecondTagName(String secondTagName) {
        this.secondTagName = secondTagName;
    }

    public BigInteger getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(BigInteger createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public BigInteger getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(BigInteger createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }
}
