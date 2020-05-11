package rpc;

import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Vector;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rpc.services.CardService;

public class Client
{
    private final static String CLASS_PATH="rpc.";
    private final static String JsonPath="json/ServerAddress.json";
    private Vector<Pair<String,Integer>> serverAddress=new Vector<>();
    private int lastVis=0;

    public Client() throws JSONException
    {
        getAddressFromJson();
    }

    private void addServer(String ad,int port)
    {
        serverAddress.add(new Pair<>(ad,port));
    }

    private void getAddressFromJson() throws JSONException
    {
        JSONObject jsonObject=ReadJSON.read(JsonPath);
        JSONArray jsonArray = jsonObject.getJSONArray("server");
        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject obj=jsonArray.getJSONObject(i);
            addServer(obj.getString("address"),obj.getInt("port"));
        }
    }

    @Nullable
    private Pair<String,Integer> getAddress() throws IOException
    {
        int cnt=serverAddress.size();
        int cur=(lastVis+1)%cnt,vis=0;
        Connector ret=null;
        while(vis!=cnt)
        {
            Socket skt;
            try
            {
                //System.out.println("Connect to "+serverAddress.elementAt(cur).getKey()+serverAddress.elementAt(cur).getValue());
                ret=new Connector(serverAddress.elementAt(cur).getKey(),serverAddress.elementAt(cur).getValue());
                //skt=new Socket(serverAddress.elementAt(cur).getKey(),serverAddress.elementAt(cur).getValue());
            }
            catch(Exception e)
            {
                cur=(cur+1)%cnt;
                vis++;
                continue;
            }
            //skt.close();
            lastVis=cur;
            ret.close();
            return serverAddress.elementAt(cur);
        }
        System.out.println("No server is available.");
        return null;
    }

    public boolean Login(String username,String password) throws IOException
    {
        Pair<String,Integer> psi=getAddress();
        if(psi==null) return false;
        CardService service=DynamicProxyFactory.getProxy(CardService.class,psi.getKey(),psi.getValue());
        Msg ret=service.Login(username,password);
        if(!ret.isSuccess()) System.out.println("登录失败");
        return ret.isSuccess();
    }

    public boolean Query(String username) throws IOException
    {
        Pair<String,Integer> psi=getAddress();
        if(psi==null) return false;
        CardService service=DynamicProxyFactory.getProxy(CardService.class,psi.getKey(),psi.getValue());
        System.out.println("您的余额: "+service.Query(username).getMessage()+"元");
        return true;
    }

    public boolean Withdraw(String username,int x) throws IOException
    {
        Pair<String,Integer> psi=getAddress();
        if(psi==null) return false;
        CardService service=DynamicProxyFactory.getProxy(CardService.class,psi.getKey(),psi.getValue());
        Msg ret=service.Withdraw(username,x);
        System.out.println(ret.getMessage());
        return ret.isSuccess();
    }

    public boolean Save(String username,int x) throws IOException
    {
        Pair<String,Integer> psi=getAddress();
        if(psi==null) return false;
        CardService service=DynamicProxyFactory.getProxy(CardService.class,psi.getKey(),psi.getValue());
        System.out.println(service.Save(username,x).getMessage());
        return true;
    }
}
