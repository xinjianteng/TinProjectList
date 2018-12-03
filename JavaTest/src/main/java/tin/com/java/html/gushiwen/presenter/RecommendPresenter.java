package tin.com.java.html.gushiwen.presenter;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.gushiwen.IRecommendContract;
import tin.com.java.html.gushiwen.ParseUtils;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;
import tin.com.java.html.gushiwen.bean.RecommendHerfEntity;
import tin.com.java.html.gushiwen.bean.RecommentEntity;
import tin.com.java.html.utils.StringUtil;

public class RecommendPresenter implements IRecommendContract {

    static String rootPath="https://www.gushiwen.org";
    private Gson gson=new Gson();


    /***
     * 获取推荐页面模块的所有数据
     * @param url
     */
    @Override
    public List<RecommentEntity> getRecommendAllPageData(String url) {
        final List<RecommentEntity> recommentEntityList=new ArrayList<>();
        String nextUrl=url;
        do {
            RecommentEntity recommentEntity=getRecommendPageData(nextUrl);
            recommentEntityList.add(recommentEntity);
            if(recommentEntity!=null&&recommentEntity.getRecommendHerfEntity()!=null){
                nextUrl=recommentEntity.getRecommendHerfEntity().getHerf();
            }else {
                nextUrl="";
            }
        }while(!StringUtil.isEmpty(nextUrl));
        return recommentEntityList;
    }

    /***
     * 获取当前页面的列表数据
     * @param url
     * @return
     */
    @Override
    public RecommentEntity getRecommendPageData(final String url) {
        Document document=ParseUtils.getDocument(url);
        RecommentEntity recommentEntity=new RecommentEntity();
        recommentEntity.setPoetryDetailEntityList(getRecommendPoetryEntityList(url));
        recommentEntity.setRecommendHerfEntity(getRecommendHerf(url));
        return recommentEntity;
    }

    /***
     * 获取当前页面的下一页链接地址
     * @param url
     * @return
     */
    public RecommendHerfEntity getRecommendHerf(String url) {
        Document document=ParseUtils.getDocument(url);
        Element pageElements=document.select("div.main3").select("div.left").select("div.pagesright").first();
        String href=pageElements.getElementById("amore").attr("href");
        if(!StringUtil.isEmpty(href)){
            RecommendHerfEntity recommendHerfEntity=new RecommendHerfEntity();
            recommendHerfEntity.setHerf(rootPath+href);
            return recommendHerfEntity;
        }
        return null;
    }



    /***
     * 获取当前页面的列表数据
     * @param url
     * @return
     */
    public List<PoetryDetailEntity> getRecommendPoetryEntityList(String url) {
        List<PoetryDetailEntity> poetryDetailEntityList =new ArrayList<>();
        Document document=ParseUtils.getDocument(url);
        Elements sonsElements= document.select("div.main3").select("div.left").select("div.sons");
        for(Element sonsElement:sonsElements){
            PoetryDetailEntity poetryDetailEntity =new PoetryDetailEntity();
            Elements contElements =sonsElement.select("div.cont").select("p");
            if(contElements.get(0)!=null){
                poetryDetailEntity.setTitle(contElements.get(0).select("a").text());
                poetryDetailEntity.setDetailHref(contElements.get(0).select("a").attr("href"));
            }
            if(contElements.get(1)!=null){
                Elements sourceElements=contElements.get(1).select("a");
                if(sourceElements.get(0)!=null){
                    poetryDetailEntity.setTimes(sourceElements.get(0).text());
                    poetryDetailEntity.setTimesHerf(sourceElements.get(0).attr("href"));
                }
                if(sourceElements.get(1)!=null){
                    poetryDetailEntity.setAuthor(sourceElements.get(1).text());
                    poetryDetailEntity.setAuthorHerf(sourceElements.get(1).attr("href"));
                }
            }
            poetryDetailEntity.setContent(sonsElement.select("div.cont").select("div.contson").text());
            System.out.println(""+gson.toJson(poetryDetailEntity));
        }
        return poetryDetailEntityList;
    }


}
