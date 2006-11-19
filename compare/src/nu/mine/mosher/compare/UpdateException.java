package nu.mine.mosher.compare;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class UpdateException extends Exception
{
	/**
	 * 
	 */
	public UpdateException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public UpdateException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UpdateException(String message, Throwable cause)
	{
		super(message,cause);
	}

	/**
	 * @param cause
	 */
	public UpdateException(Throwable cause)
	{
		super(cause);
	}
}
