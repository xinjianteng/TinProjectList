package tin.com.java.html.sese;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tin.com.java.html.xindongfang.TypeBean;

public class SsMain {

    private static final String host="https://www.155ss.com/htm";

    private static final String seurl=host+"/piclist1/";

    public static void main(String args[]){
        long t1=new Date().getTime();
        List<SsType> typeBeanList=getSsTypeList(seurl);

        for(SsType ssType:typeBeanList){
            if(ssType.getHref().startsWith("https")){
                getSsTypeDetailList(ssType.getHref());
            }else {
                getSsTypeDetailList(host+ssType.getHref());
            }

        }

        long t2=new Date().getTime();
        System.out.print(t2-t1);
    }

    /***
     * 解析一级分类目录以及个别类别
     * @return
     */
    private static List<SsType> getSsTypeList(String url) {
        List<SsType> SsTypeList=new ArrayList<>();
        Document doc=null;
        try {
            // 网络加载HTML文档
            doc = Jsoup.connect(url)
                    .timeout(5000) // 设置超时时间
                    .get(); // 使用GET方法访问URL
            //查询分类div
            Elements typeElements=doc.select(".layout").select(".mt10").select("div.mainArea").select("ul.textList").select("li");
            for(Element element:typeElements){
                SsType ssType=new SsType();
                ssType.setName(element.select("a").text());
                ssType.setHref(element.select("a").attr("href"));
                SsTypeList.add(ssType);
            }
            return SsTypeList;
        }catch (Exception e){
            return SsTypeList;
        }
    }


    /***
     * 解析一级分类目录以及个别类别
     * @return
     */
    private static List<SsDetail> getSsTypeDetailList(String url) {
        List<SsDetail> ssDetailList=new ArrayList<>();
        Document doc=null;
        try {
            // 网络加载HTML文档
            doc = Jsoup.connect(url)
                    .data("Hm_lvt_767e27c6fc5a7b6a90ba665ed5f7559b","1564245076834|1532706228")
                    .timeout(5000) // 设置超时时间
                    .get(); // 使用GET方法访问URL
            //查询分类div
            Elements typeElements=doc.select(".layout").select(".mt10").select("div.mainAreaBlack").select("div.picContent").select("img");
            for(Element element:typeElements){
                SsDetail ssDetail=new SsDetail();
                ssDetail.setImgUrl(element.attr("src"));
                ssDetailList.add(ssDetail);
            }
            return ssDetailList;
        }catch (Exception e){
            return ssDetailList;
        }
    }
}
