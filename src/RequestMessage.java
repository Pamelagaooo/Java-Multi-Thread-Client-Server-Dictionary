public class RequestMessage<T> 
{
    private ServerAction action;
    private T data;

    public ServerAction getAction() 
    {
        return action;
    }

    public void setAction(ServerAction action) 
    {
        this.action = action;
    }

    public T getData() 
    {
        return data;
    }

    public void setData(T data) 
    {
        this.data = data;
    }
}