package rpc.services;

import rpc.Connector;
import rpc.Msg;

import java.sql.*;

public class CardServiceImpl implements CardService
{
    static Connection getConnection()
    {
        Connection conn=null;
        try
        {
            String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
            String url="jdbc:sqlserver://localhost:1433;DatabaseName=RPCdb";
            String userName="Setsuna";
            String userPwd="";
            Class.forName(driverName);
            conn=DriverManager.getConnection(url,userName,userPwd);
            return conn;
        }
        catch(ClassNotFoundException|SQLException e)
        {
            e.printStackTrace();
        }
        return conn;
    }

    private void updateFail(String username,int x)
    {
        String sql="update users set failed=? where u_name=?;";
        try(Connection conn=getConnection();PreparedStatement pstmt=conn.prepareStatement(sql))
        {
            pstmt.setInt(1,x);
            pstmt.setString(2,username);
            pstmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public Msg Save(String username,int x)
    {
        Msg ret=new Msg();
        int now=0;
        String sql="select * from users where u_name=?;";
        try(Connection conn=getConnection();PreparedStatement pstmt=conn.prepareStatement(sql))
        {
            pstmt.setString(1,username);
            try(ResultSet rt=pstmt.executeQuery())
            {
                if(rt.next())
                {
                    now=rt.getInt("cash");
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        sql="update users set cash=? where u_name=?;";
        try(Connection conn=getConnection();PreparedStatement pstmt=conn.prepareStatement(sql))
        {
            pstmt.setInt(1,now+x);
            pstmt.setString(2,username);
            pstmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        ret.setSuccess(true);
        ret.setMessage("存款成功");
        return ret;
    }

    public Msg Withdraw(String username,int x)
    {
        Msg ret=new Msg();
        int now=0;
        try
        {
            String sql="select * from users where u_name=?;";
            Connection conn=getConnection();
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setString(1,username);
            try(ResultSet rt=pstmt.executeQuery())
            {
                if(rt.next())
                {
                    now=rt.getInt("cash");
                }
                if(now<x)
                {
                    ret.setMessage("余额不足!");
                    return ret;
                }
            }
            sql="update users set cash=? where u_name=?;";
            pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,now-x);
            pstmt.setString(2,username);
            pstmt.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        ret.setSuccess(true);
        ret.setMessage("取款成功");
        return ret;
    }

    public Msg Query(String username)
    {
        Msg ret=new Msg();
        String sql="select * from users where u_name=?;";
        try(Connection conn=getConnection();PreparedStatement pstmt=conn.prepareStatement(sql))
        {
            pstmt.setString(1,username);
            try(ResultSet rt=pstmt.executeQuery())
            {
                if(rt.next())
                {
                    ret.setMessage(String.valueOf(rt.getInt("cash")));
                }
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        ret.setSuccess(true);
        return ret;
    }

    @Override
    public Msg Login(String username,String password)
    {
        Msg ret=new Msg();
        String sql="select * from users where u_name=?;";
        try(Connection conn=getConnection();PreparedStatement pstmt=conn.prepareStatement(sql))
        {
            pstmt.setString(1,username);
            try(ResultSet rt=pstmt.executeQuery())
            {
                if(rt.next())
                {
                    int failed=rt.getInt("failed");
                    if(!rt.getString("u_password").equals(password))
                    {
                        ret.setSuccess(false);
                        ret.setMessage("Error: 密码错误");
                        updateFail(username,failed+1);
                        return ret;
                    }
                    else
                        updateFail(username,0);
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
        ret.setMessage("Login Success");
        return ret;
    }
}
