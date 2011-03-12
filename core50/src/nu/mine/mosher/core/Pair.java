package nu.mine.mosher.core;

import java.io.Serializable;

public final class Pair<T extends Cloneable, U extends Cloneable> implements Cloneable, Comparable<Pair<T,U>>, Serializable, Immutable
{
	private final ImmutableReference<T> a;
	private final ImmutableReference<U> b;

	public Pair(final T a, final U b) throws CloningException
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

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	@Override
	public String toString()
	{
		return "(" + this.a + "," + this.b + ")";
	}

	@Override
	public boolean equals(final Object other)
	{
		if (this == other)
		{
			return true;
		}
		if (other == null)
		{
			return false;
		}
		if (this.getClass() != other.getClass())
		{
			return false;
		}
		final Pair<?,?> that = (Pair<?,?>)other;
		return this.a.equals(that.a) && this.b.equals(that.b);
	}

	@Override
	public int hashCode()
	{
		return this.a.hashCode() ^ this.b.hashCode();
	}

	@Override
	public int compareTo(final Pair<T,U> that)
	{
		int c = 0;

		if (c == 0)
		{
			c = this.a.compareTo(that.a);
		}
		if (c == 0)
		{
			c = this.b.compareTo(that.b);
		}

		return c;
	}
}
