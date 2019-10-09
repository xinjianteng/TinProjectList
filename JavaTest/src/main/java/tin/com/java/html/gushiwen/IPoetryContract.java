package tin.com.java.html.gushiwen;

import org.jsoup.nodes.Document;

import java.util.List;

import tin.com.java.html.gushiwen.bean.AuthorBean;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;


/***
 * 详情页面
 */
public interface IPoetryContract {


    /***
     *获取一个作者的所有的诗歌集
     * @param authorBean
     * @return
     */
    List<PoetryDetailEntity> getOneAuthorAllPoertyDatas(AuthorBean authorBean);


    /***
     * 获取一个列表页面的诗集
     * @param documen
     *   * @param isDetail
     * @return
     */
    List<PoetryDetailEntity> getOnePagePoertyData(Document documen,AuthorBean authorBean, boolean isDetail);

    /***
     * 获取详情信息
     * @param url
     * @return
     */
    PoetryDetailEntity getPoetryDetail(String url,AuthorBean authorBean);






}
