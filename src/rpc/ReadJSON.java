package rpc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ReadJSON
{
    static final String jsonAddress="json/aops.json";

    static JSONObject read(String file) throws JSONException
    {
        StringBuffer stringBuffer=new StringBuffer();
        try
        {
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));
            String line;
            while((line=bufferedReader.readLine())!=null) stringBuffer.append(line);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return new JSONObject(stringBuffer.toString());
    }

    static void readAspects(String target) throws JSONException
    {
        DynamicProxyFactory.afterMethod="";
        DynamicProxyFactory.beforeMethod="";
        JSONObject jsonObject=read(jsonAddress);
        JSONArray jsonArray=jsonObject.getJSONArray("aops");
        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject obj=jsonArray.getJSONObject(i);
            if(obj.getString("target").equals(target))
            {
                String type=obj.getString("type");
                String name=obj.getString("method");
                if(type.equals("after")) DynamicProxyFactory.afterMethod=name;
                else if(type.equals("before")) DynamicProxyFactory.beforeMethod=name;
            }
        }
    }
}
