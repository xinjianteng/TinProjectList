package tin.com.java.html.sese;

import tin.com.java.html.sese.presenter.ZiPaiPresenter;

public class SeMain {

   public static ZiPaiPresenter ziPaiPresenter;


   public static String ziPaiUrl="https://www.qqce98.com/tupian/list-%E8%87%AA%E6%8B%8D%E5%81%B7%E6%8B%8D.html";

   public static String rootUrl="https://www.qqce98.com/index/home.html";


    public static void main(String args[]) {
        ziPaiPresenter=new ZiPaiPresenter();

        for(int i=2;i<100;i++){
            ziPaiPresenter.getPageData(ziPaiUrl);
        }


    }

}
