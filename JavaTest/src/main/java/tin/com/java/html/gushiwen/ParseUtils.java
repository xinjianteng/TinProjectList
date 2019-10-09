package tin.com.java.html.gushiwen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class ParseUtils {
    private final static String Tag="ParseUtils.class";

    /***
     * 获取网页源html
     * @param url
     * @return
     */
    public static Document getDocument(String url){
        Document document = null;
        try {
            // 网络加载HTML文档
            document = Jsoup.connect(url)
                    .timeout(5000) // 设置超时时间
                    .get(); // 使用GET方法访问URL
            return document;
        }catch (Exception e){
            System.out.print(e.toString());
            return document;
        }
    }


    /***
     * 获取网页源html
     * @return
     */
    public static Document getLocatlHtml(String path){
        Document document = null;
        File input = new File(path);
        try {
            document = Jsoup.parse(input, "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }




}
