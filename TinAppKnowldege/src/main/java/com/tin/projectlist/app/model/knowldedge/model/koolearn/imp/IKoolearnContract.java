package com.tin.projectlist.app.model.knowldedge.model.koolearn.imp;

import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.KoolearnDetailBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;
import knowldege.app.tin.com.tinlibrary.mvp.IBasePresenter;
import knowldege.app.tin.com.tinlibrary.mvp.IBaseView;

import java.util.List;

public interface IKoolearnContract {


    interface View extends IBaseView<Presenter>{
        void showTypeList(List<TypeBean> typeBeanList);

        void showTypeClassilyLisxt(List<TypeClassilyBean> typeClassilyBeanList);

        void showTypeClassilyDetail(List<KoolearnDetailBean> typeClassilyBeanList);

    }


    interface Presenter extends IBasePresenter{
        /**
         * 获取分类
         * @param url
         */
        void  getTypeList(String url);

        /***
         * 获取分类 子目录
         * @param url
         */
        void  getTypeClassildyList(String url);


        /***
         * 获取分类 子目录 详情
         * @param url
         */
        void getTypeClassildyDetail(String url);

    }


}
