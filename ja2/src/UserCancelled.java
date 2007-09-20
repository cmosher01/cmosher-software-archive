/*
 * Created on Sep 19, 2007
 */
public class UserCancelled extends Exception
{
	public UserCancelled()
	{
	}

	public UserCancelled(String message)
	{
		super(message);
	}

	public UserCancelled(Throwable cause)
	{
		super(cause);
	}

	public UserCancelled(String message, Throwable cause)
	{
		super(message,cause);
	}
}
