package tin.com.java.html.gushiwen.presenter;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


import tin.com.java.html.gushiwen.GuShiWenMain;
import tin.com.java.html.gushiwen.IClassilyTagContract;
import tin.com.java.html.gushiwen.bean.ClassilyTagEntity;

public class ClassilyTagPresenter implements IClassilyTagContract{


    /***
     * 获取包含的tag标签
     * @param rootElement
     * @return
     */
    @Override
    public List<ClassilyTagEntity> getListContainTags(Element rootElement) {
        Elements tagElements= rootElement.getElementsByClass("tag").select("a");
        if(tagElements!=null){
            List<ClassilyTagEntity> classilyTagEntityList=new ArrayList<>();
            for(org.jsoup.nodes.Element tagElement:tagElements){
                ClassilyTagEntity classilyTagEntity=new ClassilyTagEntity();
                classilyTagEntity.setTagName(tagElement.text());
                classilyTagEntity.setTagHref(GuShiWenMain.rootPath+tagElement.attr("href"));
                classilyTagEntityList.add(classilyTagEntity);
            }
            return classilyTagEntityList;
        }else {
            return null;
        }
    }


}
