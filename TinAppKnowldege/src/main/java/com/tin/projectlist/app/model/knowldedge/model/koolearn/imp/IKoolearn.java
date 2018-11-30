package com.tin.projectlist.app.model.knowldedge.model.koolearn.imp;

import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.KoolearnDetailBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;

import java.util.List;

public interface IKoolearn {

    /***
     * 获取分类
     * @param url
     * @return
     */
    List<TypeBean> getKoolearnTypeList(String url) ;

    /***
     * 获取分类 子目录
     * @param url
     * @return
     */
    List<TypeClassilyBean> getKoolearnTypeClassilyList(String url);

    /***
     * 获取分类 子目录详情
     * @param url
     * @return
     */
    List<KoolearnDetailBean> getKoolearnDetailList(final String url);

}
