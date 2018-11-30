package com.tin.projectlist.app.model.knowldedge.model.koolearn.event;

import com.tin.projectlist.app.model.knowldedge.model.base.view.BaseEvent;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;

import org.jsoup.select.Elements;

import java.util.List;

public class KollearnEvent extends BaseEvent{
    public static final int TYPE_SUCCESS = 10;   //数据请求成功
    public static final int LIST_SUCCESS = 11;   //数据请求成功
    public static final int Detail_SUCCESS = 12;   //数据请求成功
    public List<TypeBean> typeBeanList;
    public List<TypeClassilyBean> typeClassilyBeanLis;
    public Elements elements;

    public KollearnEvent(int state) {
        super(state);
    }

    public void setTypeBeanList(List<TypeBean> typeBeanList) {
        this.typeBeanList = typeBeanList;
    }

    public void setTypeClassilyBeanLis(List<TypeClassilyBean> typeClassilyBeanLis) {
        this.typeClassilyBeanLis = typeClassilyBeanLis;
    }
}
