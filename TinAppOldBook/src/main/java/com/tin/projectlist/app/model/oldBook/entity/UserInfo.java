package com.tin.projectlist.app.model.oldBook.entity;

import cn.bmob.v3.BmobObject;



public class UserInfo extends BmobObject {

    private String mobile;
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}