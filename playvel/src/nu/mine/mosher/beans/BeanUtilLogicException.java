/*
 * Created on Apr 14, 2004
 */
package nu.mine.mosher.beans;

/**
 * Indicates an error in the internal logic in BeanUtil.
 * @author Chris Mosher
 */
public class BeanUtilLogicException extends RuntimeException
{
    public BeanUtilLogicException(Throwable cause)
    {
        super(cause);
    }
}
