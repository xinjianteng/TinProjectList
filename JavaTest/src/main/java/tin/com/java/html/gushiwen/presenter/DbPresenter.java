package tin.com.java.html.gushiwen.presenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import tin.com.java.html.gushiwen.db.SqlConnection;
import tin.com.java.html.gushiwen.IDbContract;

public class DbPresenter implements IDbContract {

    String[] tNames={"先秦","两汉","魏晋","南北朝","隋","唐","五代","宋","金","元","明","清"};


    /***
     * 初始化朝代表
     * @return
     */
    @Override
    public boolean initTime() {
        Connection connection=new SqlConnection().TheSqlConnection();
        String sql="insert into times (t_name) values(?)";
        try {
            PreparedStatement ps=connection.prepareStatement(sql);
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


}
