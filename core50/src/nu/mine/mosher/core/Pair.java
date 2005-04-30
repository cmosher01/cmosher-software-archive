package nu.mine.mosher.core;

import java.io.Serializable;

public final class Pair<T extends Cloneable, U extends Cloneable> implements Cloneable, Comparable<Pair<T,U>>, Serializable, Immutable
{
	private final ImmutableReference<T> a;
	private final ImmutableReference<U> b;

	public Pair(T a, U b) throws CloningException
	{
		this.a = new ImmutableReference<T>(a);
		this.b = new ImmutableReference<U>(b);
	}

	public T a() throws CloningException
	{
		return this.a.object();
	}

	public U b() throws CloningException
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

	public int compareTo(Pair<T,U> that)
	{
		int c = this.a.compareTo(that.a);
		if (c == 0)
		{
			c = this.b.compareTo(that.b);
		}
		return c;
	}
}
