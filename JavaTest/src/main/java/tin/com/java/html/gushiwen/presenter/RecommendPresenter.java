package tin.com.java.html.gushiwen.presenter;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.gushiwen.GuShiWenMain;
import tin.com.java.html.gushiwen.IRecommendContract;
import tin.com.java.html.gushiwen.ParseUtils;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;
import tin.com.java.html.gushiwen.bean.RecommendHerfEntity;
import tin.com.java.html.gushiwen.bean.RecommentEntity;
import tin.com.java.html.utils.StringUtil;

public class RecommendPresenter implements IRecommendContract {

    private Gson gson;

    private ClassilyTagPresenter classilyTagPresenter;


    public RecommendPresenter() {
        classilyTagPresenter=new ClassilyTagPresenter();
        gson=new Gson();
    }

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
        Element amoreElement=document.getElementById("amore");

        if(amoreElement!=null){
            String href=amoreElement.attr("href");
            RecommendHerfEntity recommendHerfEntity=new RecommendHerfEntity();
            recommendHerfEntity.setHerf(GuShiWenMain.rootPath+href);
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
        Elements sonsElements= document.getElementById("main3").select("div.sons");

        for(Element sonsElement:sonsElements){
            Element contElement=sonsElement.getElementsByClass("cont").first();
            if(contElement!=null){
                PoetryDetailEntity poetryDetailEntity =new PoetryDetailEntity();
                Element titleElement=contElement.select("p").get(0).select("a").first();
                poetryDetailEntity.setTitle(titleElement.text());
                poetryDetailEntity.setDetailHref(GuShiWenMain.rootPath+titleElement.attr("href"));

                Element authorElement=contElement.getElementsByClass("source").first();
                poetryDetailEntity.setTimes(authorElement.select("a").get(0).text());
                poetryDetailEntity.setTimesHerf(GuShiWenMain.rootPath+ authorElement.select("a").get(0).attr("href"));
                poetryDetailEntity.setAuthor(authorElement.select("a").get(1).text());
                poetryDetailEntity.setAuthorHerf(GuShiWenMain.rootPath+authorElement.select("a").get(1).attr("href"));
                poetryDetailEntity.setContent(contElement.getElementsByClass("contson").text());
                poetryDetailEntity.setTag( sonsElement.getElementsByClass("tag").text());
                poetryDetailEntity.setClassilyTagEntityList(classilyTagPresenter.getListContainTags(sonsElement));
                System.out.println("");
                System.out.println(""+gson.toJson(poetryDetailEntity));
                System.out.println("");
                poetryDetailEntityList.add(poetryDetailEntity);
            }
        }
        return poetryDetailEntityList;
    }


}
