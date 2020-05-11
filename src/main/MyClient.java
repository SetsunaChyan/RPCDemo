package main;

import rpc.Client;
import rpc.Msg;

import java.util.Scanner;

public class MyClient
{
    public static void main(String args[]) throws Exception
    {
        Client c=new Client();
        Scanner sc=new Scanner(System.in);
        String username=null,password=null;
        while(true)
        {
            System.out.print("用户名:");
            username=sc.nextLine();
            System.out.print("密码:");
            password=sc.nextLine();
                  if(c.Login(username,password)) break;
        }
        while(true)
        {
            System.out.println("----------------------");
            System.out.println("1. 查询余额\n2. 取钱\n3. 存钱\n4. 退出\n输入您的操作:");
            int op=sc.nextInt();
            if(op==1)
            {
                c.Query(username);
            }
            else if(op==2)
            {
                System.out.println("输入您要取的金额:");
                int m=sc.nextInt();
                c.Withdraw(username,m);
            }
            else if(op==3)
            {
                System.out.println("输入您要存的金额:");
                int m=sc.nextInt();
                c.Save(username,m);
            }
            else break;
        }
        sc.close();
    }
}
