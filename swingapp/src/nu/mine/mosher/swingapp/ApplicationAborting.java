/*
 * Created on Feb 8, 2005
 */
package nu.mine.mosher.ja2;

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
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ApplicationAborting(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ApplicationAborting(String message, Throwable cause)
    {
        super(message,cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public ApplicationAborting(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
