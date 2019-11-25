package com.tin.projectlist.app.model.oldBook.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.tin.projectlist.app.model.oldBook.constant.BmobTableConstant;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Book extends BmobObject implements Parcelable {
    private String book_name;

    public Book() {
        setTableName(BmobTableConstant.TAB_BOOK_INFO);
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(book_name);
    }
    protected Book(Parcel in) {
        book_name = in.readString();
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


}
