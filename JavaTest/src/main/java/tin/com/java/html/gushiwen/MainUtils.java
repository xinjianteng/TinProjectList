package tin.com.java.html.gushiwen;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import tin.com.java.html.gushiwen.bean.GuShiWenBean;

public class MainUtils {

    static String rootPath="https://www.gushiwen.org/";

    public static void main(String args[]) {
        System.out.println("Hello World!");
        getKoolearnTypeList(rootPath);


    }


    public static List<GuShiWenBean> getKoolearnTypeList(final String url)  {
        final List<GuShiWenBean> typeBeanList=new ArrayList<>();
        try {
            FutureTask<List<GuShiWenBean>> task=new FutureTask<>(new Callable<List<GuShiWenBean>>() {
                @Override
                public List<GuShiWenBean> call() throws Exception {
                    Document document= ParseUtils.getDocument(url);
                    if(document!=null){
                        Elements sonsElements=document.select("div.main3").select("div.left").select("div.sons");
                        for(Element sonsElement:sonsElements){
                            GuShiWenBean GuShiWenBean=new GuShiWenBean();
                            Elements contElements =sonsElement.select("div.cont").select("p");
                            if(contElements.get(0)!=null){
                                GuShiWenBean.setTitle(contElements.get(0).text());
                            }
                            if(contElements.get(1)!=null){
                                Elements sourceElements=contElements.get(1).select("a");
                                if(sourceElements.get(0)!=null){
                                    GuShiWenBean.setTimes(sourceElements.get(0).text());
                                }
                                if(sourceElements.get(1)!=null){
                                    GuShiWenBean.setAuthor(sourceElements.get(1).text());
                                }
                            }
                            GuShiWenBean.setContent(sonsElement.select("div.cont").select("div.contson").text());
                            typeBeanList.add(GuShiWenBean);
                        }
                    }
                    System.out.print(typeBeanList.toString());
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
