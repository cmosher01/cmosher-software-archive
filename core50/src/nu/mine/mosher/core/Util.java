package nu.mine.mosher.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Util
{
	private Util()
	{
		assert false : "can't instantiate";
	}

	/**
	 * Documents an Object as being unused.
	 * 
	 * @param o
	 *        the object that is (documented as being) not used
	 */
	public static void unused(Object o)
	{
		final Object x = o;
		o = x;
	}

	/**
	 * Documents an integral type as being unused.
	 * @param i
	 */
	public static void unused(long i)
	{
		final long x = i;
		i = x;
	}

	/**
	 * Documents a real type as being unused.
	 * @param i
	 */
	public static void unused(double i)
	{
		final double x = i;
		i = x;
	}



	/**
	 * Converts and <ocde>Iterable</code> into a <code>List</code>.
	 * @param <T> class of elements in the <code>List</code>
	 * @param i <ocde>Iterable</code> to read all elements from
	 * @return the new <code>List</code> of all elements from <code>i</code>
	 */
	public static<T> ArrayList<T> list(final Iterable<T> i)
    {
		final List<T> r = new ArrayList<T>();
        for (final T t : i)
        {
            r.add(t);
        }
        return (ArrayList<T>)r;
    }

	/**
	 * Converts and <ocde>Iterable</code> into a <code>Set</code>.
	 * @param <T> class of elements in the <code>List</code>
	 * @param i <ocde>Iterable</code> to read all elements from
	 * @return the new <code>Set</code> of all elements from <code>i</code>
	 */
	public static<T> HashSet<T> set(final Iterable<T> i)
	{
		final Set<T> s = new HashSet<T>();
		for (final T t : i)
		{
			s.add(t);
		}
		return (HashSet<T>)s;
	}
}
