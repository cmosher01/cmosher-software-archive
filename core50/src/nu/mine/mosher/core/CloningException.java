/*
 * Created on Apr 28, 2005
 */
package nu.mine.mosher.core;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class CloningException extends Exception
{
	/**
	 * 
	 */
	public CloningException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public CloningException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CloningException(String message, Throwable cause)
	{
		super(message,cause);
	}

	/**
	 * @param cause
	 */
	public CloningException(Throwable cause)
	{
		super(cause);
	}
}
