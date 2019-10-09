package tin.com.java.html.gushiwen;

import java.util.List;

import tin.com.java.html.gushiwen.bean.AuthorBean;
import tin.com.java.html.gushiwen.bean.AuthorPageBean;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;

/***
 *
 */
public interface IAuthorContract {


    /***
     * 获取所有页面的作者数据
     * @return
     */
    List<AuthorPageBean> getAllPagerAuthorData();

    /***
     * 获取一个页面的作者数据
     * @return
     */
    List<AuthorPageBean> getOnePagerAuthorData(String uri, boolean isDetail);

    /***
     * 获取所有作者
     * @return
     */
    List<AuthorBean> getAllAuthor();

    /***
     * 获取作者详情
     * @return
     */
    AuthorBean getAuthorDetail(String uri);


    /***
     *获取作者诗集
     * @return
     */
    List<PoetryDetailEntity> getAuthorPoetry();









}
