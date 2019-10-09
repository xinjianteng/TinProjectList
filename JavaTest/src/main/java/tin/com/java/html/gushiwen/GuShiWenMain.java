package tin.com.java.html.gushiwen;


import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.gushiwen.bean.AuthorBean;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;
import tin.com.java.html.gushiwen.presenter.DbPresenter;
import tin.com.java.html.gushiwen.presenter.PoetryDetailPresenter;

public class GuShiWenMain {


    public static String rootPath="https://m.gushiwen.org";

    public static void main(String args[]) {
//        RecommendPresenter recommendPresenter =new RecommendPresenter();
//        List<RecommentEntity> recommentEntityList=recommendPresenter.getRecommendAllPageData(rootPath);
//
//        PoetryDetailPresenter poetryDetailPresenter=new PoetryDetailPresenter();
//        poetryDetailPresenter.getPoetryDetail("https://m.gushiwen.org/shiwenv_1eaa0ce953db.aspx");


//        AuthorPresenter authorPresenter=new AuthorPresenter();
//        authorPresenter.getAuthorPoetry();
//        authorPresenter.getAuthorDetail("https://so.gushiwen.org/authorv_f59c39d8cecd.aspx");
//        authorPresenter.getAuthorDetail("https://m.gushiwen.org/authorv_b90660e3e492.aspx");

        DbPresenter dbPresenter=new DbPresenter();
//        AuthorBean authorBean=dbPresenter.selectAuthor(394);
//        authorBean.setPoetryLink("https://so.gushiwen.org/authors/authorvsw_677ad0bb97e7A356.aspx");
//
        PoetryDetailPresenter poetryDetailPresenter=new PoetryDetailPresenter();
//        poetryDetailPresenter.getOneAuthorAllPoertyDatas(authorBean);


        List<AuthorBean> authorBeanList=dbPresenter.selectAllAuthorByBigId(7746);
        List<PoetryDetailEntity> poetryDetailEntityList=new ArrayList<>();
        for(int i=0;i<authorBeanList.size();i++){
            if(!authorBeanList.get(i).getPoetryLink().equals("")){
                poetryDetailPresenter.getOneAuthorAllPoertyDatas(authorBeanList.get(i));
            }
        }


    }


    {

//        https://m.gushiwen.org/authorv_d0b6d44d1bf2.aspx
//        https://m.gushiwen.org/authorv_90afb67780e5.aspx
//        https://m.gushiwen.org/authorv_90afb67780e5.aspx
//        https://m.gushiwen.org/authorv_d0b6d44d1bf2.aspx
//        https://m.gushiwen.org/authorv_a3223b202a04.aspx
//        https://m.gushiwen.org/authorv_3f7646c27a5b.aspx
//        https://m.gushiwen.org/authorv_6612e938d4da.aspx
//        https://m.gushiwen.org/authorv_6612e938d4da.aspx
//        https://m.gushiwen.org/authorv_8b70687b3577.aspx
//        https://m.gushiwen.org/authorv_d0b6d44d1bf2.aspx
//        https://m.gushiwen.org/authorv_946deb196766.aspx
//        https://m.gushiwen.org/authorv_61b410378f92.aspx
//        https://m.gushiwen.org/authorv_fb707df63460.aspx
//        https://m.gushiwen.org/authorv_dc939a2e88d9.aspx




    }


}
