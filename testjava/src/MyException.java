/**
 */
public class MyException extends Exception
{

    /**
     * Constructor for MyException.
     */
    public MyException()
    {
        super();
    }

    /**
     * Constructor for MyException.
     * @param message
     */
    public MyException(String message)
    {
        super(message);
    }

    /**
     * Constructor for MyException.
     * @param message
     * @param cause
     */
    public MyException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * Constructor for MyException.
     * @param cause
     */
    public MyException(Throwable cause)
    {
        super(cause);
    }
}
