/*
 * Created on Aug 30, 2004
 */
package nu.mine.mosher;

/**
 * TODO
 * 
 * @author Chris
 */
public class InvalidMagicBytes extends Exception
{

    /**
     * 
     */
    public InvalidMagicBytes(byte[] magic)
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InvalidMagicBytes(byte[] magic, String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InvalidMagicBytes(byte[] magic, Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidMagicBytes(byte[] magic, String message, Throwable cause)
    {
        super(message,cause);
        // TODO Auto-generated constructor stub
    }

}
