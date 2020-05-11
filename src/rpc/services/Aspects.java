package rpc.services;

import rpc.Msg;

public interface Aspects
{
    Msg check(String a,String b);
    Msg takeLogs(String username,String act);
}
