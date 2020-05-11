package rpc.services;

import rpc.Msg;

public interface CardService
{
    Msg Login(String username,String password);
    Msg Query(String username);
    Msg Withdraw(String username,int x);
    Msg Save(String username,int x);
}
