package tin.com.java.html.gushiwen.presenter;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tin.com.java.html.gushiwen.GuShiWenMain;
import tin.com.java.html.gushiwen.IPoetryContract;
import tin.com.java.html.gushiwen.ParseUtils;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;

public class PoetryDetailPresenter implements IPoetryContract{

    static String rootPath="https://www.gushiwen.org";

    private Gson gson;
    private ClassilyTagPresenter classilyTagPresenter;


    public PoetryDetailPresenter() {
        classilyTagPresenter=new ClassilyTagPresenter();
        gson=new Gson();
    }
    /***
     * 获取详情页面信息
     * @param url
     * @return
     */
    @Override
    public PoetryDetailEntity getPoetryDetail(String url) {
        PoetryDetailEntity poetryDetailEntity =new PoetryDetailEntity();
        poetryDetailEntity.setDetailHref(url);
        Document document=ParseUtils.getDocument(url);
        Element mainElement= document.getElementsByClass("main3").first();
        Elements sonsElements= mainElement.select("div.sons");
        Elements fanyiElements=new Elements();
        Elements shangxiElements=new Elements();

        for(int i=0;i<sonsElements.size();i++){
            if(i==0){
                //内容主题
                Element contElement =sonsElements.get(0).select("div.cont").first();
                poetryDetailEntity.setTitle(contElement.select("h1").text());
                Element sourceElement=contElement.getElementsByClass("source").first();
                poetryDetailEntity.setTimes(sourceElement.select("a").get(0).text());
                poetryDetailEntity.setTimesHerf(GuShiWenMain.rootPath+sourceElement.select("a").get(0).attr("href"));
                poetryDetailEntity.setAuthor(sourceElement.select("a").get(1).text());
                poetryDetailEntity.setAuthorHerf(GuShiWenMain.rootPath+sourceElement.select("a").get(1).attr("href"));
                poetryDetailEntity.setContent(contElement.getElementsByClass("contson").select("p").html());
                poetryDetailEntity.setTag( sonsElements.get(0).getElementsByClass("tag").text());
                poetryDetailEntity.setClassilyTagEntityList(classilyTagPresenter.getListContainTags(sonsElements.get(0)));
            }

            Element fanyiElement=sonsElements.get(i).getElementsByAttributeValueMatching("id","fanyi*").first();
            if(fanyiElement!=null){
                fanyiElements.add(fanyiElement);
            }

            Element shangxiElement=sonsElements.get(i).getElementsByAttributeValueMatching("id","shangxi*").first();
            if(shangxiElement!=null){
                shangxiElements.add(shangxiElement);
            }

        }

        for(int i=0;i<fanyiElements.size();i++){

        }



        return poetryDetailEntity;
    }



}
