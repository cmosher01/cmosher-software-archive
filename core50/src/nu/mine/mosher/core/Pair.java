package nu.mine.mosher.core;

public final class Pair<T,U> implements Cloneable, Comparable<T>, Serializable,	Immutable
{
	private final ImmutableReference<T> a;
	private final ImmutableReference<U> b;

	public Pair(T a, U b) throws CloneNotSupportedException
	{
		this.a = new ImmutableReference<T>(a);
		this.b = new ImmutableReference<U>(b);
	}

	public T a() throws CloneNotSupportedException
	{
		return this.a.object();
	}

	public U b() throws CloneNotSupportedException
	{
		return this.b.object();
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public String toString()
	{
		return "(" + a + "," + b + ")";
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof Pair))
		{
			return false;
		}
		Pair that = (Pair)o;
		return this.a.equals(that.a) && this.b.equals(that.b);
	}

	public int hashCode()
	{
		return a.hashCode() ^ b.hashCode();
	}

	public int compareTo(T o)
	{
		Pair that = (Pair)o;
		int c;
		c = this.a.compareTo(that.a);
		if (c == 0)
		{
			c = this.b.compareTo(that.b);
		}
		return c;
	}
}
