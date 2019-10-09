package tin.com.java.html.gushiwen.presenter;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.gushiwen.GuShiWenMain;
import tin.com.java.html.gushiwen.IPoetryContract;
import tin.com.java.html.gushiwen.ParseUtils;
import tin.com.java.html.gushiwen.bean.AuthorBean;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;

public class PoetryDetailPresenter implements IPoetryContract{

    private Gson gson;
    private ClassilyTagPresenter classilyTagPresenter;
    private DbPresenter dbPresenter;


    public PoetryDetailPresenter() {
        classilyTagPresenter=new ClassilyTagPresenter();
        dbPresenter=new DbPresenter();
        gson=new Gson();
    }

    /***
     *获取一个作者的所有的诗歌集
     * @param authorBean
     * @return
     */
    @Override
    public List<PoetryDetailEntity> getOneAuthorAllPoertyDatas(AuthorBean authorBean) {
        if(authorBean!=null){
            List<PoetryDetailEntity> authorPageBeanList=new ArrayList<>();
            List<String> authorPageBeanErrorList=new ArrayList<>();
            boolean isEnd=false;
            String nextUrl="";
            for(int i=1;!isEnd;i++){
                Document document= ParseUtils.getDocument(nextUrl.equals("")?authorBean.getPoetryLink():nextUrl);
                if(document!=null){
                    List<PoetryDetailEntity>  onePageAuthorDatas=getOnePagePoertyData(document, authorBean,true);
                    String uri=document.getElementsByClass("main3").first().getElementsByClass("amore").attr("href");
                    if(uri.equals("")){
                        isEnd=true;
                    }else {
                        nextUrl=GuShiWenMain.rootPath+uri;
                    }
                    if(onePageAuthorDatas!=null&&onePageAuthorDatas.size()>0){
                        authorPageBeanList.addAll(onePageAuthorDatas);
                    }
                }else {
                    authorPageBeanErrorList.add(nextUrl);
                }
            }
            System.out.println("请求超时的作者某一页诗集   /n"+new Gson().toJson(authorPageBeanErrorList));
            return authorPageBeanList.size()>0?authorPageBeanList:null;
        }else {
            return null;
        }

    }

    /***
     * 获取一个列表页面的诗集
     * @param document
     * @param isDetail
     * @return
     */
    @Override
    public List<PoetryDetailEntity> getOnePagePoertyData(Document document,AuthorBean authorBean, boolean isDetail) {
        List<PoetryDetailEntity> authorPageBeanList=new ArrayList<>();
        try {
            new Robot().delay(2000);
            if(document!=null){
                Element mainElement= document.getElementsByClass("main3").first();
                Elements sonsElements= mainElement.getElementsByClass("sons");
                for(Element element: sonsElements){
                    PoetryDetailEntity poetryDetailEntity=new PoetryDetailEntity();
                    Element contElement=element.getElementsByClass("cont").first();
                    poetryDetailEntity.setPoetryName(contElement.select("p").get(0).text());
                    poetryDetailEntity.setAuthorTime(contElement.getElementsByClass("source").select("a").get(0).text());
                    poetryDetailEntity.setAuthorName(contElement.getElementsByClass("source").select("a").get(1).text());
                    poetryDetailEntity.setPoetryLink(GuShiWenMain.rootPath+contElement.select("p").select("a").attr("href"));
                    PoetryDetailEntity poetryDetail=getPoetryDetail(poetryDetailEntity.getPoetryLink(),authorBean);
                    if(poetryDetail!=null){
                        dbPresenter.addPoerty(poetryDetail);
                        authorPageBeanList.add(poetryDetail);
                    }else {
                        System.out.println("error"+new Gson().toJson(authorBean));
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error"+new Gson().toJson(authorBean));
        }
        return authorPageBeanList;
    }

    /***
     * 获取详情页面信息
     * @param url
     * @return
     */
    @Override
    public PoetryDetailEntity getPoetryDetail(String url,AuthorBean authorBean) {
        PoetryDetailEntity poetryDetailEntity =new PoetryDetailEntity();
        Document document=ParseUtils.getDocument(url);
        if(document!=null){
            Element mainElement= document.getElementsByClass("main3").first();
            Elements sonsElements= mainElement.select("div.sons");
            Elements fanyiElements=new Elements();
            Elements shangxiElements=new Elements();

            for(int i=0;i<sonsElements.size();i++){
                if(i==0){
                    //内容主题
                    Element contElement =sonsElements.get(0).select("div.cont").first();
                    poetryDetailEntity.setAuthorId(authorBean.getAuthorId());
                    poetryDetailEntity.setAuthorLink(authorBean.getAuthorLink());
                    poetryDetailEntity.setAuthorHead(authorBean.getAuthorHead());
                    poetryDetailEntity.setAuthorName(authorBean.getAuthorName());
                    poetryDetailEntity.setAuthorDes(authorBean.getAuthorDes());
                    Element sourceElement=contElement.getElementsByClass("source").first();
                    poetryDetailEntity.setAuthorTime(sourceElement.select("a").get(0).text());
                    poetryDetailEntity.setAuthorTimeLink(GuShiWenMain.rootPath+sourceElement.select("a").get(0).attr("href"));
                    poetryDetailEntity.setPoetryName(contElement.select("h1").text());
                    poetryDetailEntity.setAuthorLink(GuShiWenMain.rootPath+sourceElement.select("a").get(1).attr("href"));
                    poetryDetailEntity.setPoetryContent(contElement.getElementsByClass("contson").html());
                    poetryDetailEntity.setPoetryTag(sonsElements.get(0).getElementsByClass("tag").text());
                    poetryDetailEntity.setClassilyTagEntityList(classilyTagPresenter.getListContainTags(sonsElements.get(0)));
                    poetryDetailEntity.setPoetryLink(url);

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
                poetryDetailEntity.setTranslation(  fanyiElements.get(0).select("p").html());
            }

            return poetryDetailEntity;
        }else {
            return null;
        }

    }



}
