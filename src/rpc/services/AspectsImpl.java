package rpc.services;

import rpc.Msg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AspectsImpl implements Aspects
{
    public Msg check(String username,String pw)
    {
        Msg ret=new Msg();
        String sql="select * from users where u_name=?;";
        try(Connection conn=CardServiceImpl.getConnection();PreparedStatement pstmt=conn.prepareStatement(sql))
        {
            pstmt.setString(1,username);
            try(ResultSet rt=pstmt.executeQuery())
            {
                if(rt.next())
                {
                    int failed=rt.getInt("failed");
                    if(failed>=3)
                    {
                        ret.setSuccess(false);
                        ret.setMessage("Error: 密码错误超过三次，账户已被冻结");
                        return ret;
                    }
                }
                else
                    return ret;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return ret;
        }
        ret.setSuccess(true);
        return ret;
    }

    public Msg takeLogs(String username,String act)
    {
        Msg ret=new Msg();
        String sql="insert into logs values(?,?,?);";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try(Connection conn=CardServiceImpl.getConnection();PreparedStatement pstmt=conn.prepareStatement(sql))
        {
            pstmt.setString(1,format.format(new Date()));
            pstmt.setString(2,username);
            pstmt.setString(3,act);
            pstmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return ret;
        }
        ret.setSuccess(true);
        return ret;
    }

}
