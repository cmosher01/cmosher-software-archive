package nu.mine.mosher.grodb;

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class ItemType<T extends Enum<T>>
{
	private T known;
	private String other;

	public ItemType(T known)
	{
		this.known = known;
	}
	public ItemType(String other)
	{
		try
		{
			Class<T> c;
			c = Class<T>.getClass();
			T x = Enum.valueOf(c,other);
			this.known = x;
		}
		catch (IllegalArgumentException e)
		{
			this.other = other;
		}
	}
	public String toString()
	{
		String s;
		if (known != null)
		{
			s = known.toString();
		}
		else
		{
			s = other;
		}
		return s;
	}
	public boolean isa(T x)
	{
		return (known != null && known.equals(x));
	}
}
