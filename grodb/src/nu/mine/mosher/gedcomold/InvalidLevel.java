/*
 * Created on Jul 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.gedcom;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IllegalLevel extends Exception
{
    public IllegalLevel(Gedcom line)
    {
        super("GEDCOM line has illegal level number at line: "+line);
    }

    /**
     * @param message
     */
    public IllegalLevel(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public IllegalLevel(Throwable cause)
    {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public IllegalLevel(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
