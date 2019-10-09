package com.tin.projectlist.app.model.oldBook.dto;

import java.io.Serializable;

public class BookDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer bookId;

    private String bookName;

    private String bookIntroduction;

    private String bookPath;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookIntroduction() {
        return bookIntroduction;
    }

    public void setBookIntroduction(String bookIntroduction) {
        this.bookIntroduction = bookIntroduction;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public BookDTO() {

    }

    public BookDTO(Integer bookId, String bookName, String bookIntroduction, String bookPath) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookIntroduction = bookIntroduction;
        this.bookPath = bookPath;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", bookIntroduction='" + bookIntroduction + '\'' +
                ", bookPath='" + bookPath + '\'' +
                '}';
    }
}
