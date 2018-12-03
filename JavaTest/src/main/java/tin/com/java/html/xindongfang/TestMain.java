package tin.com.java.html.xindongfang;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.crypto.Data;

public class TestMain {


    private static final String yuwen="http://gaokao.koolearn.com/yuwen/";

    public static void main(String args[]){
            long t1=new Date().getTime();
//            List<PoetryEntity> typeBeanList=getTypeBeanList(yuwen);
//            List<List<TypeClassilyBean>> typeClassilyListList=new ArrayList<>();
//            for(PoetryEntity typeBean:typeBeanList){
//                typeClassilyListList.add(getTypeClassilyList(typeBean.getHref()));
//            }
            getDetail("http://gaokao.koolearn.com/20180608/1154866.html");
            long t2=new Date().getTime();
            System.out.print(t2-t1);
    }

    private static void getDetail(String url){
        Document doc=null;
        try {
            doc=Jsoup.connect(url)
                    .timeout(5000)
                    .get();
            Elements elements=doc.select("#cr").select("div.w685").select(".maincn_sw").select(".fl").select("div.show_l2").select("div.mt40").first().getAllElements();
            for(Element element:elements){
                element.getAllElements();
            }

        }catch (Exception e){

        }

    }

    private static List<TypeClassilyBean> getTypeClassilyList(String url) {
        List<TypeClassilyBean> typeBeanList=new ArrayList<>();
        Document doc=null;
        try {
            doc=Jsoup.connect(url).timeout(5000).get();
            //查询分类div
            Elements lists=doc.select("div.list01").select("li");
            for(Element element:lists){
                TypeClassilyBean typeClassilyBean=new TypeClassilyBean();
                typeClassilyBean.setName(element.select("h3").text());
                typeClassilyBean.setHerf(element.select("h3").select("a").attr("href"));
                if(element.select("div.mt17").select(".js2").select("p").first()!=null){
                    typeClassilyBean.setDes(element.select("div.mt17").select(".js2").select("p").first().text());
                }
                typeClassilyBean.setPage(doc.select("#page").select("span").text());
                typeClassilyBean.setPageHerf(doc.select("#page").select(".a1").last().attr("href"));
                typeBeanList.add(typeClassilyBean);
            }
        }catch (Exception e){

        }
        System.out.println(new Gson().toJson(typeBeanList));
        return typeBeanList;

    }

    /***
     * 解析一级分类目录以及个别类别
     * @return
     */
    private static List<TypeBean> getTypeBeanList(String url) {

        List<TypeBean> typeBeanList=new ArrayList<>();
        Document doc=null;
        try {
            // 网络加载HTML文档
            doc = Jsoup.connect(url)
                    .timeout(5000) // 设置超时时间
                    .get(); // 使用GET方法访问URL
            //查询分类div
            Elements typeElements=doc.select("div.fc").select("div.list2").select(".mt12");
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
            return typeBeanList;
        }catch (Exception e){
            return typeBeanList;
        }

    }




}
