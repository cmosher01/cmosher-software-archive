/*
 * Created on Nov 29, 2007
 */
package chipset;

public class InvalidMemoryLoad extends Exception
{

	public InvalidMemoryLoad()
	{
		super();
	}

	public InvalidMemoryLoad(String message, Throwable cause)
	{
		super(message,cause);
	}

	public InvalidMemoryLoad(String message)
	{
		super(message);
	}

	public InvalidMemoryLoad(Throwable cause)
	{
		super(cause);
	}
}
