package tin.com.java.html.gushiwen;


import java.util.List;

import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;
import tin.com.java.html.gushiwen.bean.RecommentEntity;
import tin.com.java.html.gushiwen.presenter.PoetryDetailPresenter;
import tin.com.java.html.gushiwen.presenter.RecommendPresenter;

public class GuShiWenMain {


    public static String rootPath="https://m.gushiwen.org";

    public static void main(String args[]) {
        RecommendPresenter recommendPresenter =new RecommendPresenter();
        PoetryDetailPresenter poetryDetailPresenter=new PoetryDetailPresenter();

        List<RecommentEntity> recommentEntityList=recommendPresenter.getRecommendAllPageData(rootPath);


//        poetryDetailPresenter.getPoetryDetail("https://m.gushiwen.org/shiwenv_1eaa0ce953db.aspx");



    }


}
