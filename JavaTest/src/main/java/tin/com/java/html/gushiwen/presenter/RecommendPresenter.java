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
import tin.com.java.html.gushiwen.bean.RecommendLinkEntity;
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
                nextUrl=recommentEntity.getRecommendHerfEntity().getLink();
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
    public RecommendLinkEntity getRecommendHerf(String url) {
        Document document=ParseUtils.getDocument(url);
        Element amoreElement=document.getElementById("amore");

        if(amoreElement!=null){
            String href=amoreElement.attr("href");
            RecommendLinkEntity recommendHerfEntity=new RecommendLinkEntity();
            recommendHerfEntity.setLink(GuShiWenMain.rootPath+href);
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
                poetryDetailEntity.setPoetryName(titleElement.text());
                poetryDetailEntity.setPoetryLink(GuShiWenMain.rootPath+titleElement.attr("href"));

                Element authorElement=contElement.getElementsByClass("source").first();
                poetryDetailEntity.setAuthorTime(authorElement.select("a").get(0).text());
                poetryDetailEntity.setAuthorTimeLink(GuShiWenMain.rootPath+ authorElement.select("a").get(0).attr("href"));
                poetryDetailEntity.setAuthorName(authorElement.select("a").get(1).text());
                poetryDetailEntity.setAuthorLink(GuShiWenMain.rootPath+authorElement.select("a").get(1).attr("href"));
                poetryDetailEntity.setPoetryContent(contElement.getElementsByClass("contson").text());
                poetryDetailEntity.setPoetryTag( sonsElement.getElementsByClass("tag").text());
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
