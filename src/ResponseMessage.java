public class ResponseMessage<T> 
{
    private Boolean isSuccess;
    private T data;

    public Boolean getSuccess() 
    {
        return isSuccess;
    }

    public void setSuccess(Boolean success) 
    {
        isSuccess = success;
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