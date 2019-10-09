package tin.com.java.html.gushiwen.presenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tin.com.java.html.gushiwen.IDbContract;
import tin.com.java.html.gushiwen.bean.AuthorBean;
import tin.com.java.html.gushiwen.bean.PoetryDetailEntity;
import tin.com.java.html.gushiwen.db.SqlConnection;

public class DbPresenter implements IDbContract {

    String[] tNames={"先秦","两汉","魏晋","南北朝","隋","唐","五代","宋","金","元","明","清"};

    /***
     * 插入一个新的朝代
     */
    String timeAdd="insert into times (t_name) values(?)";

    String authorSelectByName="";

    Connection connection=new SqlConnection().TheSqlConnection();
    /***
     * 初始化朝代表
     * @return
     */
    @Override
    public boolean initTime() {
        try {
            PreparedStatement ps=connection.prepareStatement(timeAdd);
            for(int i=0;i<tNames.length;i++){
                ps.setString(1,tNames[i]);
                ps.executeUpdate();
            }
            ps.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 查询所有的作者
     * @return
     */
    @Override
    public List<AuthorBean> selectAllAuthor() {
        String sql="select * from author";
        List<AuthorBean> list=new ArrayList<>();
        try {
            PreparedStatement pst=connection.prepareStatement(sql);
            ResultSet rs=pst.executeQuery();
            while (rs.next()){
                 AuthorBean authorBean=new AuthorBean();
                 authorBean.setAuthorId(rs.getInt("id"));
                 authorBean.setAuthorName(rs.getString("author_name"));
                 authorBean.setAuthorLink(rs.getString("author_link"));
                 authorBean.setAuthorHead(rs.getString("author_head"));
                 authorBean.setAuthorDes(rs.getString("author_des"));
                 authorBean.setPoetryNum(rs.getString("poetry_num"));
                 authorBean.setPoetryLink(rs.getString("poetry_link"));
                 list.add(authorBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /***
     * 查询大于id的所有作者信息
     * @return
     * @param authorId
     */
    @Override
    public List<AuthorBean> selectAllAuthorByBigId(int authorId) {
        String sql="select * from author where id>"+authorId;
        List<AuthorBean> list=new ArrayList<>();
        try {
            PreparedStatement pst=connection.prepareStatement(sql);
            ResultSet rs=pst.executeQuery();
            while (rs.next()){
                AuthorBean authorBean=new AuthorBean();
                authorBean.setAuthorId(rs.getInt("id"));
                authorBean.setAuthorName(rs.getString("author_name"));
                authorBean.setAuthorLink(rs.getString("author_link"));
                authorBean.setAuthorHead(rs.getString("author_head"));
                authorBean.setAuthorDes(rs.getString("author_des"));
                authorBean.setPoetryNum(rs.getString("poetry_num"));
                authorBean.setPoetryLink(rs.getString("poetry_link"));
                list.add(authorBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public AuthorBean selectAuthor(int authorId) {
        String sql="select * from author where id="+authorId;
        AuthorBean authorBean=new AuthorBean();
        try {
            PreparedStatement pst=connection.prepareStatement(sql);
            ResultSet rs=pst.executeQuery();
            while (rs.next()){
                authorBean.setAuthorId(rs.getInt("id"));
                authorBean.setAuthorName(rs.getString("author_name"));
                authorBean.setAuthorLink(rs.getString("author_link"));
                authorBean.setAuthorHead(rs.getString("author_head"));
                authorBean.setAuthorDes(rs.getString("author_des"));
                authorBean.setPoetryNum(rs.getString("poetry_num"));
                authorBean.setPoetryLink(rs.getString("poetry_link"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authorBean;
    }


    /***
     * 插入一个诗人
     * @param authorBean
     */
    @Override
    public void addAuthor(AuthorBean authorBean) {
        String sql="insert into author (" +
                "author_name," +
                "author_link," +
                "author_head," +
                "author_des," +
                "poetry_num," +
                "poetry_link" +
                ") values(?,?,?,?,?,?)";
        try {
            PreparedStatement ps=connection.prepareStatement(sql);
            ps.setString(1,authorBean.getAuthorName());
            ps.setString(2,authorBean.getAuthorLink());
            ps.setString(3,authorBean.getAuthorHead());
            ps.setString(4,authorBean.getAuthorDes());
            ps.setString(5,authorBean.getPoetryNum());
            ps.setString(6,authorBean.getPoetryLink());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPoerty(PoetryDetailEntity poetryDetailEntity) {
        String sql="insert into poetry (" +
                "poetry_name," +
                "poetry_content," +
                "translation," +
                "poetry_link," +
                "poetry_tag," +
                "author_id," +
                "author_link," +
                "author_head," +
                "author_name," +
                "author_des," +
                "author_time," +
                "author_time_link" +
                ") values(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps=connection.prepareStatement(sql);
            ps.setString(1,poetryDetailEntity.getPoetryName());
            ps.setString(2,poetryDetailEntity.getPoetryContent());
            ps.setString(3,poetryDetailEntity.getTranslation());
            ps.setString(4,poetryDetailEntity.getPoetryLink());
            ps.setString(5,poetryDetailEntity.getPoetryTag());
            ps.setInt(6,poetryDetailEntity.getAuthorId());
            ps.setString(7,poetryDetailEntity.getAuthorLink());
            ps.setString(8,poetryDetailEntity.getAuthorHead());
            ps.setString(9,poetryDetailEntity.getAuthorName());
            ps.setString(10,poetryDetailEntity.getAuthorDes());
            ps.setString(11,poetryDetailEntity.getAuthorTime());
            ps.setString(12,poetryDetailEntity.getAuthorTimeLink());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /***
     * 初始化分类
     * @return
     */
    @Override
    public boolean initTag() {
        return false;
    }


}
