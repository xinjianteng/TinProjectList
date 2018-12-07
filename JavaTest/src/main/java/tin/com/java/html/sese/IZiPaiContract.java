package tin.com.java.html.sese;

import java.util.List;

import tin.com.java.html.sese.bean.CoverEntity;

/***
 * 自拍
 */
public interface IZiPaiContract {


    /***
     * 获取封面列表
     * @return
     * @param url
     */
    List<CoverEntity> getPageData(String url);


    /**
     * 获取详细列表
     * @param url
     * @return
     */
    List<CoverEntity> getDetailData(String url);


}
