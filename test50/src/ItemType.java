

/**
 * @author Chris Mosher
 * Created: Feb 7, 2004
 */
public class ItemType<T>
{
	private T /*ItemTypeEnum*/ known;
	private String other;

	public ItemType(T /*ItemTypeEnum*/ known)
	{
		this.known = known;
	}
	public ItemType(String other)
	{
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
	public boolean isa(T /*ItemTypeEnum*/ x)
	{
		return (known != null && known.equals(x));
	}
}
