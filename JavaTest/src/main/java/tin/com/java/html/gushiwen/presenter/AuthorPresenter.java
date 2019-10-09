package tin.com.java.html.gushiwen.presenter;


import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.Robot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tin.com.java.html.gushiwen.GuShiWenMain;
import tin.com.java.html.gushiwen.IAuthorContract;
import tin.com.java.html.gushiwen.ParseUtils;
import tin.com.java.html.gushiwen.bean.AuthorBean;
import tin.com.java.html.gushiwen.bean.AuthorPageBean;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;

/***
 * 作者
 */
public class AuthorPresenter implements IAuthorContract{


    private final String authorHerf="https://m.gushiwen.org/authors/default.aspx?p=";
    private DbPresenter dbPresenter;
    PoetryDetailPresenter poetryDetailPresenter;

    public AuthorPresenter() {
        dbPresenter=new DbPresenter();
        poetryDetailPresenter=new PoetryDetailPresenter();
    }

    /***
     * 获取所有页面的作者列表
     * @return
     */
    @Override
    public List<AuthorPageBean> getAllPagerAuthorData() {
        List<AuthorPageBean> authorPageBeanList=new ArrayList<>();
        boolean isEnd=false;
        for(int i=1;!isEnd;i++){
            List<AuthorPageBean>  onePageAuthorData= getOnePagerAuthorData(authorHerf+i, true);
            isEnd=onePageAuthorData.isEmpty();
            if(!isEnd){
                authorPageBeanList.addAll(onePageAuthorData);
            }
            System.out.println("第 "+i+"页       "+new Gson().toJson(onePageAuthorData));
        }
        System.out.println("总列表数据  "+new Gson().toJson(authorPageBeanList));
        return authorPageBeanList;
    }


    /***
     * 获取单页作者列表
     * @param uri
     * @param isDetail
     * @return
     */
    @Override
    public List<AuthorPageBean> getOnePagerAuthorData(String uri, boolean isDetail) {
        List<AuthorPageBean> authorPageBeanList=new ArrayList<>();
        try {
            System.out.println( "延时前:"+new Date().toString()  );
            new Robot().delay(2000);
            System.out.println( "延时后:"+new Date().toString()  );
            Document document= ParseUtils.getDocument(uri);
            if(document!=null){
                Element mainElement= document.getElementsByClass("main3").first();
//                Element mainElement= document.getElementsByClass("main3").first().getElementsByClass("left").first();
                Elements sonsElements= mainElement.getElementsByClass("sonspic");
                for(Element element: sonsElements){
                    AuthorPageBean authorPageBean=new AuthorPageBean();
                    try {
                        Element contElement=element.getElementsByClass("cont").first();
                        authorPageBean.setLink(GuShiWenMain.rootPath+contElement.getElementsByClass("divimg").select("a").attr("href"));
                        authorPageBean.setAuthorHead(contElement.getElementsByClass("divimg").select("img").attr("src"));
                        authorPageBean.setaName(contElement.select("p").get(0).text());
                        if(authorPageBean.getAuthorHead().equals("")){
                            authorPageBean.setLink(GuShiWenMain.rootPath+contElement.select("a").get(0).attr("href"));
                        }
                        authorPageBean.setDes(contElement.select("p").get(1).text());
                        authorPageBean.setPoetryNum(contElement.select("p").get(1).select("a").text());
                        if(isDetail){
                            authorPageBean.setAuthorBean(getAuthorDetail(authorPageBean.getLink()));
                        }
                        authorPageBeanList.add(authorPageBean);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorPageBeanList;
    }


    @Override
    public List<AuthorBean> getAllAuthor() {
        List<AuthorBean>  authorBeanList=new ArrayList<>();
        List<AuthorPageBean> authorPageBeanList= getAllPagerAuthorData();
        for(int i=0;i<authorPageBeanList.size();i++){
            authorBeanList.add(getAuthorDetail(authorPageBeanList.get(i).getLink()));
        }
        return authorBeanList;
    }



    @Override
    public AuthorBean getAuthorDetail(String uri) {
        AuthorBean authorBean=new AuthorBean();
        authorBean.setAuthorLink(uri);
        Document document= ParseUtils.getDocument(uri);
        if(document!=null){
            try {
                Element mainElement= document.getElementsByClass("main3").first();
                Element contElement= mainElement.getElementsByClass("sonspic").first().getElementsByClass("cont").first();
                authorBean.setAuthorHead(contElement.getElementsByClass("divimg").select("img").attr("src"));
                authorBean.setAuthorName(contElement.select("h1").get(0).text());
                authorBean.setAuthorDes(contElement.select("p").get(0).text());
                authorBean.setPoetryNum(contElement.select("p").get(0).select("a").text());
                try {
                    authorBean.setPoetryLink(GuShiWenMain.rootPath+mainElement.getElementsByClass("title").last().select("a").attr("href"));
                }catch (Exception e){
                    authorBean.setPoetryLink("");
                }
                dbPresenter.addAuthor(authorBean);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return authorBean;
    }

    /***
     *获取作者诗集
     * @return
     */
    @Override
    public List<PoetryDetailEntity> getAuthorPoetry() {
         List<AuthorBean> authorBeanList= dbPresenter.selectAllAuthor();
         List<PoetryDetailEntity> poetryDetailEntityList=new ArrayList<>();
         for(int i=0;i<authorBeanList.size();i++){
             List<PoetryDetailEntity> list=poetryDetailPresenter.getOneAuthorAllPoertyDatas(authorBeanList.get(i));
             if(list!=null){
                 poetryDetailEntityList.addAll(list);
             }
         }
         return poetryDetailEntityList;
    }





}
