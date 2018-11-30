package com.tin.projectlist.app.model.knowldedge.model.base.view;


/**
 * 类名：BaseEvent.java<br>
 * 描述： 基础网络事件<br>
 * 创建者： XinJianTeng<br>
 * 创建日期：2016-06-21<br>
 * 版本： <br>
 * 修改者： <br>
 * 修改日期：<br>
 */
public class BaseEvent {

    public static final int SUCCESS = 1;   //数据请求成功
    public static final int ERROR = 2;     //请求失败
    public static final int REFRESH = 3;     //刷新界面
    public static final int LOADING = 4;   //加载中
    public static final int END = 5;       //到最后一页了
    public static final int NONET = 6;     //尚未联网

    public int state;
    public String msg;
    protected String strStatus;


    public BaseEvent() {
    }

    public BaseEvent(int state) {
        this.state = state;
    }

    public BaseEvent(String msg) {
        this.msg = msg;
    }

    public BaseEvent(int state, String msg) {
        this.state = state;
        this.msg = msg;
    }



    public BaseEvent(String strStatus, String msg) {
        this.strStatus = strStatus;
        this.msg = msg;
    }

    public int getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }


    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }
}
