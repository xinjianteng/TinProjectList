package com.tin.projectlist.app.model.knowldedge.model.koolearn.mdoel;

import com.google.gson.Gson;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.KoolearnDetailBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.bean.TypeClassilyBean;
import com.tin.projectlist.app.model.knowldedge.model.koolearn.imp.IKoolearn;
import com.tin.projectlist.app.model.knowldedge.model.utils.ParseUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class KoolearnModel implements IKoolearn{

    @Override
    public List<TypeBean> getKoolearnTypeList(final String url)  {
        final List<TypeBean> typeBeanList=new ArrayList<>();
        try {
            FutureTask<List<TypeBean>> task=new FutureTask<>(new Callable<List<TypeBean>>() {
                @Override
                public List<TypeBean> call() throws Exception {
                    Document document= ParseUtils.getDocument(url);
                    if(document!=null){
                        Elements typeElements=document.select("div.fc").select("div.list2").select(".mt12");
                        for(Element element:typeElements){
                            TypeBean typeBean=new TypeBean();
                            typeBean.setName(element.select("h2").select("a").first().text());
                            typeBean.setHref(element.select("h2").select("a").first().attr("href"));
                            Elements classilyElements=element.select("li");
                            List<TypeClassilyBean> typeClassilyBeanArrayList=new ArrayList<>();
                            for(Element classilyElement:classilyElements){
                                TypeClassilyBean typeClassilyBean=new TypeClassilyBean();
                                typeClassilyBean.setName(classilyElement.text());
                                typeClassilyBean.setHerf(classilyElement.select("a").attr("href"));
                                typeClassilyBeanArrayList.add(typeClassilyBean);
                            }
                            typeBean.setClassilyList(typeClassilyBeanArrayList);
                            typeBeanList.add(typeBean);
                        }
                    }
                    return typeBeanList;
                }
            });
            new Thread(task).start();
            return task.get();
        }catch (Exception e){
            return typeBeanList;
        }
    }

    @Override
    public List<TypeClassilyBean> getKoolearnTypeClassilyList(final String url) {
        final List<TypeClassilyBean> typeBeanList=new ArrayList<>();
        try {
            FutureTask<List<TypeClassilyBean>> task=new FutureTask<>(new Callable<List<TypeClassilyBean>>() {
                @Override
                public List<TypeClassilyBean> call() throws Exception {

                    Document document=ParseUtils.getDocument(url);
                    try {
                        //查询分类div
                        Elements lists=document.select("div.list01").select("li");
                        for(Element element:lists){
                            TypeClassilyBean typeClassilyBean=new TypeClassilyBean();
                            typeClassilyBean.setName(element.select("h3").text());
                            typeClassilyBean.setHerf(element.select("h3").select("a").attr("href"));
                            if(element.select("div.mt17").select(".js2").select("p").first()!=null){
                                typeClassilyBean.setDes(element.select("div.mt17").select(".js2").select("p").first().text());
                            }
                            typeClassilyBean.setPage(document.select("#page").select("span").text());
                            typeClassilyBean.setPageHerf(document.select("#page").select(".a1").last().attr("href"));
                            typeBeanList.add(typeClassilyBean);
                        }
                    }catch (Exception e){

                    }
                    System.out.println(new Gson().toJson(typeBeanList));
                    return typeBeanList;
                }
            });
            new Thread(task).start();
            return task.get();
        }catch (Exception e){
            return typeBeanList;
        }
    }


    @Override
    public List<KoolearnDetailBean> getKoolearnDetailList(final String url){
        final List<KoolearnDetailBean> typeBeanList=new ArrayList<>();
        try {
            FutureTask<List<KoolearnDetailBean>> task=new FutureTask<>(new Callable<List<KoolearnDetailBean>>() {
                @Override
                public List<KoolearnDetailBean> call() throws Exception {
                    Document document= ParseUtils.getDocument(url);
                    try {
                        //查询分类div
                        Elements elements=document.select("#cr").select("div.w685").select(".maincn_sw").select(".fl").select("div.show_l2").select("div.mt40").first().getAllElements();
                        for(Element element:elements){
                            KoolearnDetailBean koolearnDetailBean=new KoolearnDetailBean();
                            koolearnDetailBean.setContent(element.toString());
                            typeBeanList.add(koolearnDetailBean);
                        }
                    }catch (Exception e){

                    }
                    System.out.println(new Gson().toJson(typeBeanList));
                    return typeBeanList;
                }
            });
            new Thread(task).start();
            return task.get();
        }catch (Exception e){
            return typeBeanList;
        }
    }



}
