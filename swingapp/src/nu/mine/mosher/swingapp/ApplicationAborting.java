/*
 * Created on Feb 8, 2005
 */
package nu.mine.mosher.swingapp;

/**
 * TODO
 * 
 * @author chrism
 */
public class ApplicationAborting extends Exception
{
    /**
     * 
     */
    public ApplicationAborting()
    {
        super();
    }

    /**
     * @param message
     */
    public ApplicationAborting(String message)
    {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ApplicationAborting(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * @param cause
     */
    public ApplicationAborting(Throwable cause)
    {
        super(cause);
    }
}
