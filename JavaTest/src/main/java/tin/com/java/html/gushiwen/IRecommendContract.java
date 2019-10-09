package tin.com.java.html.gushiwen;

import java.util.List;

import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;
import tin.com.java.html.gushiwen.bean.RecommendLinkEntity;
import tin.com.java.html.gushiwen.bean.RecommentEntity;

/***
 * 推荐页面
 */
public interface IRecommendContract {


    /***
     * 获取推荐页面模块的所有数据
     * @param url
     */
    List<RecommentEntity> getRecommendAllPageData(String url);


    /***
     * 获取当前页面的列表数据+下一页连接地址
     * @param url
     * @return
     */
    RecommentEntity getRecommendPageData(final String url);


    /***
     * 获取当前页面的下一页链接地址
     * @param url
     * @return
     */
    RecommendLinkEntity getRecommendHerf(String url);


    /***
     * 获取当前页面的列表数据
     * @param url
     * @return
     */
    List<PoetryDetailEntity> getRecommendPoetryEntityList(String url);




}
