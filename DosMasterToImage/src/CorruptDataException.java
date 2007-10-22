/*
 * Created on Oct 22, 2007
 */
public class CorruptDataException extends Exception
{
	private final int[] data;
	public CorruptDataException(int[] data)
	{
		this.data = data;
	}

	public CorruptDataException(int[] data, String message)
	{
		super(message);
		this.data = data;
	}

	public CorruptDataException(int[] data, Throwable cause)
	{
		super(cause);
		this.data = data;
	}

	public CorruptDataException(int[] data, String message, Throwable cause)
	{
		super(message,cause);
		this.data = data;
	}
}
