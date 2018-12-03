package tin.com.java.html.gushiwen.presenter;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import tin.com.java.html.gushiwen.IPoetryContract;
import tin.com.java.html.gushiwen.ParseUtils;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;

public class PoetryDetailPresenter implements IPoetryContract{

    private Gson gson=new Gson();
    static String rootPath="https://www.gushiwen.org";
    /***
     * 获取详情信息
     * @param url
     * @return
     */
    @Override
    public PoetryDetailEntity getPoetryDetail(String url) {
        PoetryDetailEntity poetryDetailEntity =new PoetryDetailEntity();
        Document document=ParseUtils.getDocument(url);
        Element leftElements= document.select("div.main3").select("div.left").first();

        Element contElement =leftElements.select("div.sons").select("div.cont").first();
        poetryDetailEntity.setTitle(contElement.select("h1").text());
        Elements sourceElements=contElement.select(".source");
        if(sourceElements.get(0)!=null){
            poetryDetailEntity.setTimes(sourceElements.get(0).select("a").get(0).text());
            poetryDetailEntity.setTimesHerf(sourceElements.get(0).select("a").get(0).attr("href"));
            poetryDetailEntity.setAuthor(sourceElements.get(0).select("a").get(1).text());
            poetryDetailEntity.setAuthorHerf(rootPath+sourceElements.get(0).select("a").get(1).attr("href"));
        }
        poetryDetailEntity.setContent(contElement.select("div.contson").text());

       Element fanyiElements= leftElements.select("div.fanyi").first();
       poetryDetailEntity.setTranslation(fanyiElements.select("div.contyishang").text());




        return poetryDetailEntity;
    }



}
