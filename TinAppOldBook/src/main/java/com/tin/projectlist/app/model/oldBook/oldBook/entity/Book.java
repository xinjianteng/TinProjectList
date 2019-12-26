package com.tin.projectlist.app.model.oldBook.oldBook.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.tin.projectlist.app.model.oldBook.constant.BmobTableConstant;

import cn.bmob.v3.BmobObject;

public class Book extends BmobObject implements Parcelable {
    private String book_name;
    private String book_cover;
    private String book_introduction;
    private String book_author;
    private String dynasty_id;
    private String book_summary;
    private String dynastyName;

    public Book() {
        setTableName(BmobTableConstant.TAB_BOOK_INFO);
    }


    protected Book(Parcel in) {
        book_name = in.readString();
        book_cover = in.readString();
        book_introduction = in.readString();
        book_author = in.readString();
        dynasty_id = in.readString();
        book_summary = in.readString();
        dynastyName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(book_name);
        dest.writeString(book_cover);
        dest.writeString(book_introduction);
        dest.writeString(book_author);
        dest.writeString(dynasty_id);
        dest.writeString(book_summary);
        dest.writeString(dynastyName);
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_introduction() {
        return book_introduction;
    }

    public void setBook_introduction(String book_introduction) {
        this.book_introduction = book_introduction;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public String getDynasty_id() {
        return dynasty_id;
    }

    public void setDynasty_id(String dynasty_id) {
        this.dynasty_id = dynasty_id;
    }

    public String getBook_summary() {
        return book_summary;
    }

    public void setBook_summary(String book_summary) {
        this.book_summary = book_summary;
    }

    public String getDynastyName() {
        return dynastyName;
    }

    public void setDynastyName(String dynastyName) {
        this.dynastyName = dynastyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }





}
