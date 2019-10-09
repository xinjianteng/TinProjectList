package tin.com.java.html.gushiwen;


import java.util.List;

import tin.com.java.html.gushiwen.bean.AuthorBean;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;

/***
 * 朝代操作
 */
public  interface IDbContract {




    /***
     * 初始化朝代表
     * @return
     */
    boolean initTime();


    /***
     * 查询所有的作者
     * @return
     */
    List<AuthorBean> selectAllAuthor();

    /***
     * 查询大于id的所有作者信息
     * @return
     */
    List<AuthorBean> selectAllAuthorByBigId(int authorId);


    AuthorBean selectAuthor(int authorId);



    /***
     * 插入一个诗人
     */
    void addAuthor(AuthorBean authorBean);



    void addPoerty(PoetryDetailEntity poetryDetailEntity);



    /***
     * 初始化分类
     * @return
     */
    boolean initTag();




}
