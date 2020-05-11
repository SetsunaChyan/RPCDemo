package rpc;

import rpc.services.Aspects;
import rpc.services.AspectsImpl;

import java.lang.reflect.*;

// 动态代理类
public class DynamicProxyFactory
{
    static String beforeMethod="";
    static String afterMethod="";

    public static <T> T getProxy(final Class<T> classType,final String host,final int port)
    {
        InvocationHandler handler=(proxy,method,args)->
        {
            String className=AspectsImpl.class.getName();
            Class<?> clazz=AspectsImpl.class;
            ReadJSON.readAspects(classType.getName()+"."+method.getName());

            Connector connector=null;
            RemoteCall call;

            //System.out.println("before "+beforeMethod);
            //System.out.println("after "+afterMethod);

            if(beforeMethod!=null&&beforeMethod.length()>0)
            {
                connector=new Connector(host,port);
                Method m=clazz.getMethod(beforeMethod,String.class,String.class);
                call=new RemoteCall(className,m.getName(),m.getParameterTypes(),args);
                connector.send(call);
                call=(RemoteCall)connector.receive();
                Msg ret=(Msg)call.getResult();
                if(!ret.isSuccess())
                    return ret;
                connector.close();
            }

            connector=new Connector(host,port);
            call=new RemoteCall(classType.getName(),method.getName(),method.getParameterTypes(),args);
            connector.send(call);
            call=(RemoteCall)connector.receive();
            connector.close();
            Object zret=call.getResult();

            if(afterMethod!=null&&afterMethod.length()>0)
            {
                connector=new Connector(host,port);
                Method m=clazz.getMethod(afterMethod,String.class,String.class);
                call=new RemoteCall(className,m.getName(),m.getParameterTypes(),new String[]{(String)args[0],method.getName()});
                connector.send(call);
                call=(RemoteCall)connector.receive();
                Msg ret=(Msg)call.getResult();
                if(!ret.isSuccess())
                    return ret;
                connector.close();
            }
            return zret;
        };
        //System.out.println("代理开始执行");
        return (T)Proxy.newProxyInstance(classType.getClassLoader(),new Class<?>[]{classType},handler);
    }
}

