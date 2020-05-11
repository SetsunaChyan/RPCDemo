package rpc;

import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server implements Runnable
{
    private Map<String,Object> remoteObjects=new HashMap<>();
    private ServerSocket serverSocket;
    private volatile boolean isRunning;

    public Server(int port) throws IOException
    {
        this.serverSocket=new ServerSocket(port);
        this.isRunning=true;
        System.out.println("Server start.");
        Runtime.getRuntime().addShutdownHook(new Thread(()->
        {
            isRunning=false;
            System.out.println("Server closed.");
        }));
    }

    @Override
    public void run()
    {
        while(isRunning)
        {
            Socket socket=null;
            try
            {
                socket=serverSocket.accept();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            new Thread(new ServerSocketRunnable(socket)).start();
        }
    }

    public void register(String className,Object remoteObject)
    {
        remoteObjects.put(className,remoteObject);
    }

    public RemoteCall invoke(RemoteCall call)
    {
        Object result=null;
        try
        {
            String className=call.getClassName();
            String methodName=call.getMethodName();
            Object[] params=call.getParams();
            Class<?> classType=Class.forName(className);
            Class<?>[] paramTypes=call.getParamTypes();
            Method method=classType.getMethod(methodName,paramTypes);
            Object remoteObject=remoteObjects.get(className);
            if(remoteObject==null)
            {
                throw new Exception(className+" 的远程对象不存在");
            }
            else
            {
                result=method.invoke(remoteObject,params);
                System.out.println("远程调用结束:remotObject:"+remoteObject.toString()+",params:"+params.toString());
            }
        }
        catch(Exception e)
        {

            System.out.println("错误："+e.getMessage());
        }
        call.setResult(result);

        return call;
    }

    class ServerSocketRunnable implements Runnable
    {
        private Socket socket;

        public ServerSocketRunnable(Socket socket)
        {
            this.socket=socket;
        }

        @Override
        public void run()
        {
            try
            {
                InputStream in=socket.getInputStream();
                ObjectInputStream ois=new ObjectInputStream(in);
                OutputStream out=socket.getOutputStream();
                ObjectOutputStream oos=new ObjectOutputStream(out);
                RemoteCall remotecallobj=(RemoteCall)ois.readObject();
                System.out.println(remotecallobj);
                remotecallobj=invoke(remotecallobj);
                // 向客户发送包含了执行结果的remotecallobj 对象
                oos.writeObject(remotecallobj);
                ois.close();
                oos.close();
            }
            catch(Exception e)
            {
                //e.printStackTrace();
            }
            finally
            {
                try
                {
                    socket.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
