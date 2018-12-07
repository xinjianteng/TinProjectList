package tin.com.java.html.sese.presenter;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.gushiwen.ParseUtils;
import tin.com.java.html.sese.IZiPaiContract;
import tin.com.java.html.sese.SeMain;
import tin.com.java.html.sese.bean.CoverEntity;
import tin.com.java.html.sese.bean.PageEntity;

public class ZiPaiPresenter implements IZiPaiContract {

    private Gson gson=new Gson();
    /***
     * 获取封面列表
     * @return
     * @param url
     */
    @Override
    public List<CoverEntity> getPageData(String url) {
        Document document= ParseUtils.getDocument(url);

       Elements listElements= document
               .getElementsByClass("img-list-data")
               .first()
               .getElementById("tpl-img-content")
               .select("li");
       List<PageEntity> pageEntityList=new ArrayList<>();

       for(int i=0;i<listElements.size();i++){
           String imgUrl=listElements.get(i).select("img").first().getElementsByAttribute("data-prefix").attr("data-original");
           String detailUrl=listElements.get(i).select("a").attr("href");
           PageEntity pageEntity=new PageEntity();
           pageEntity.setImgHerf(imgUrl);
           pageEntity.setDetailHerf(SeMain.rootUrl+detailUrl);

           System.out.println(gson.toJson(pageEntity));
           pageEntityList.add(pageEntity);
       }

        return null;
    }

    /**
     * 获取详细列表
     *
     * @param url
     * @return
     */
    @Override
    public List<CoverEntity> getDetailData(String url) {
        return null;
    }


}
