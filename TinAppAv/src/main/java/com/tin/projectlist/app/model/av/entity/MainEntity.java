package com.tin.projectlist.app.model.av.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

/**
 * 2019/12/20
 * author : chx
 * description :
 */
@Keep
public class MainEntity implements Parcelable {

    private String coverPath;


    protected MainEntity(Parcel in) {
        coverPath = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(coverPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainEntity> CREATOR = new Creator<MainEntity>() {
        @Override
        public MainEntity createFromParcel(Parcel in) {
            return new MainEntity(in);
        }

        @Override
        public MainEntity[] newArray(int size) {
            return new MainEntity[size];
        }
    };


    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

}
