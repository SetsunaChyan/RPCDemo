package main;

import rpc.Server;
import rpc.services.AspectsImpl;
import rpc.services.CardServiceImpl;

public class MyServer2
{
    private final static String CLASS_PATH="rpc.services.";
    private final static int port=8001;

    public static void main(String args[]) throws Exception
    {
        Server server=new Server(port);
        server.register(CLASS_PATH+"CardService",new CardServiceImpl());
        server.register(CLASS_PATH+"AspectsImpl",new AspectsImpl());
        new Thread(server).start();
    }
}
