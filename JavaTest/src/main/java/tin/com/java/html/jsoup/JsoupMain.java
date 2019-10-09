package tin.com.java.html.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

/**
 * @作者 Goofy
 * @邮件 252878950@qq.com
 * @日期 2014-4-2上午10:41:19
 * @描述 选择器 操作示例
 */
public class JsoupMain {

    public static void main(String[] args) {
        Document doc;
        try {
//获取文档
            doc=Jsoup.connect("http://xxx.com/").get();

/*****获取单一元素******/
//与JS类似的根据ID选择的选择器<div id="content"></div>
            Element content = doc.getElementById("content");

/*****一下方法的返回值都是Elements集合******/

//获取所有的a标签<a href="#"></a>
            content.getElementsByTag("a");
//类选择器<div></div>
            doc.getElementsByClass("divClass");
//获取Document的所有元素
            doc.getAllElements();
//根据属性获取元素<a href="#"></a>
            doc.getElementsByAttribute("href");
//根据属性前缀获取元素 <li data-name="Peter Liu" data-city="ShangHai" data-lang="CSharp" data-food="apple">
            doc.getElementsByAttributeStarting("data-");
//根据key-value选择如<a href="http://xdemo.org"></a>
            doc.getElementsByAttributeValue("href","http://xdemo.org");
//和上面的正好相反
            doc.getElementsByAttributeValueNot("href","http://xdemo.org");
//根据key-value,其中value可能是key对应属性的一个子字符串，选择如<a href="http://xdemo.org"></a>
            doc.getElementsByAttributeValueContaining("href", "xdemo");
//根据key-value,其中key对应值的结尾是value，选择如<a href="http://xdemo.org"></a>
            doc.getElementsByAttributeValueEnding("href", "org");
//和上面的正好相反
            doc.getElementsByAttributeValueStarting("href","http://xdemo");
//正则匹配，value需要满足正则表达式，<a href="http://xdemo.org"></a>,如href的值含有汉字
            doc.getElementsByAttributeValueMatching("href",Pattern.compile("[\u4e00-\u9fa5]"));
//同上
            doc.getElementsByAttributeValueMatching("href", "[\u4e00-\u9fa5]");
//根据元素所在的z-index获取元素
            doc.getElementsByIndexEquals(0);
//获取z-index大于x的元素
            doc.getElementsByIndexGreaterThan(0);
//和上面的正好相反
            doc.getElementsByIndexLessThan(10);

//遍历标签
            for (Element link : content.getElementsByTag("a")) {
                String linkHref = link.attr("href");
                String linkText = link.text();
            }

/**************一些其他常用的方法**************/
//获取网页标题
            doc.title();
//获取页面的所有文本
            doc.text();

//为元素添加一个css class
            content.addClass("newClass");
//根据属性获取值
            content.attr("id");
//获取所有子元素
            content.children();
//获取元素内的所有文本
            content.text();
//获取同级元素
            content.siblingElements();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
