package com.tin.projectlist.app.model.knowldedge.model.utils;

import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @package : com.cliff.utils
 * @description : event 管理类
 * Created by chenhx on 2018/3/23 15:26.
 */

public class EventBusUtil {


    public static EventBus build(){
        return EventBus.getDefault();
    }


    public static void register(Object subscriber){
        if(!build().isRegistered(subscriber)){
            build().register(subscriber);
        }
    }

    public static void unRegister(Object subscriber) {
        if (build().isRegistered(subscriber)) {
            build().unregister(subscriber);
        }
    }


    public static void post(BaseEvent event) {
       build().post(event);
    }

    public static void postSticky(BaseEvent event) {
       build().postSticky(event);
    }

    public static void removeStickyEvent(BaseEvent object) {
        build().removeStickyEvent(object);
    }
}
