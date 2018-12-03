package tin.com.java.html.gushiwen;

import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;


/***
 * 详情页面
 */
public interface IPoetryContract {


    /***
     * 获取详情信息
     * @param url
     * @return
     */
    PoetryDetailEntity getPoetryDetail(String url);



}
