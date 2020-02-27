package com.tin.projectlist.app.library.base.utils.event;

import org.greenrobot.eventbus.EventBus;

/**
 * @package : com.cliff.libs.util.event
 * @description :
 * Created by chenhx on 2018/4/9 14:41.
 */

public class EventBusMgr {

    public static <T> void register(T t) {
        if (!EventBus.getDefault().isRegistered(t)) {
            EventBus.getDefault().register(t);
        }
    }

    public static <T> void ungister(T t) {
        if (EventBus.getDefault().isRegistered(t)) {
            EventBus.getDefault().unregister(t);
        }
    }

    public static void post(BaseEvent baseEvent) {
        EventBus.getDefault().post(baseEvent);

    }


    public static void postSticky(BaseEvent baseEvent) {
        EventBus.getDefault().postSticky(baseEvent);
    }

    public static void removeSticky(BaseEvent baseEvent) {
        EventBus.getDefault().removeStickyEvent(baseEvent);
    }


    public static void removeAllSticky() {
        EventBus.getDefault().removeAllStickyEvents();
    }

}
