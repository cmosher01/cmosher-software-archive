package nu.mine.mosher.grodb.orig;

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
//		Enum.valueOf(Class<T>.getClass(),other);
		this.other = other;
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
