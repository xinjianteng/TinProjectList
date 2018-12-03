package tin.com.java.html.gushiwen;


import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;
import tin.com.java.html.gushiwen.presenter.PoetryDetailPresenter;
import tin.com.java.html.gushiwen.presenter.RecommendPresenter;

public class MainUtils {


    static String rootPath="https://www.gushiwen.org/";

    public static void main(String args[]) {
//        RecommendPresenter recommendPresenter =new RecommendPresenter();
//        recommendPresenter.getRecommendPageData(rootPath);

        PoetryDetailPresenter poetryDetailPresenter=new PoetryDetailPresenter();

        poetryDetailPresenter.getPoetryDetail("https://so.gushiwen.org/shiwenv_6f2836c5d2b8.aspx");


    }


}
