package nu.mine.mosher.compare;

public class UpdateException extends Exception
{
	public UpdateException()
	{
		super();
	}

	public UpdateException(String message)
	{
		super(message);
	}

	public UpdateException(String message, Throwable cause)
	{
		super(message,cause);
	}

	public UpdateException(Throwable cause)
	{
		super(cause);
	}
}
