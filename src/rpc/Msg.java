package rpc;

import java.io.Serializable;

public class Msg implements Serializable
{
    private String Message;
    private boolean Success;

    void Msg()
    {
        Success=false;
        Message=new String();
    }

    public void setMessage(String message)
    {
        Message=message;
    }

    public boolean isSuccess()
    {
        return Success;
    }

    public String getMessage()
    {
        return Message;
    }

    public void setSuccess(boolean success)
    {
        Success=success;
    }
}
