package tin.com.java.html.gushiwen;

import org.jsoup.nodes.Element;

import java.util.List;


import tin.com.java.html.gushiwen.bean.ClassilyTagEntity;

public interface IClassilyTagContract {


    /***
     * 获取包含的tag标签
     * @param rootElement
     * @return
     */
    List<ClassilyTagEntity> getListContainTags(Element rootElement);





}
