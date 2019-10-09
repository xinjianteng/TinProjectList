package tin.com.java.html.mobile;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.gushiwen.GuShiWenMain;
import tin.com.java.html.gushiwen.ParseUtils;

public class MobileMain {

   static String url="http://www.guisd.com/ss/fujian/xiamen/";

    public static void main(String args[]) {

        String str="";
        Document document=ParseUtils.getDocument(url);
        if(document!=null){
            Element mainElement= document.getElementsByClass("wrap h_list").first();
            Elements sonsElements= mainElement.select("dd");

            for(Element element: sonsElements){
                str=str+","+element.text();
            }
            System.out.print(str);
        }else {
        }

    }
}
