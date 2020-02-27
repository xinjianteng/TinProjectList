package com.tin.projectlist.app.library.base.frame.event;


import com.tin.projectlist.app.library.base.utils.event.BaseEvent;

/**
 * 2019/12/31
 * author : chx
 * description :
 */
public class LogoutEvent extends BaseEvent {
    public static final int LOGOUT = 1;
    public LogoutEvent(int mType) {
        super(mType);
    }

    public LogoutEvent(int mType, String msg) {
        super(mType, msg);
    }
}
