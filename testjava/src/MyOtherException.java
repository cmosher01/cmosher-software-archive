public class MyOtherException extends Exception
{
    public MyOtherException()
    {
        super();
    }

    public MyOtherException(String message)
    {
        super(message);
    }

    public MyOtherException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MyOtherException(Throwable cause)
    {
        super(cause);
    }
}
