package nu.mine.mosher.core;

import java.util.ArrayList;
import java.util.HashSet;

public final class Util
{
	private Util()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Documents an Object as being unused.
	 * 
	 * @param o
	 *        the object that is (documented as being) not used
	 */
	public static void unused(Object o)
	{
		Object x = o;
		o = x;
	}

	public static void unused(long i)
	{
		long x = i;
		i = x;
	}

	public static void unused(double i)
	{
		double x = i;
		i = x;
	}



	public static<T> ArrayList<T> list(Iterable<T> i)
    {
        ArrayList<T> r = new ArrayList<T>();
        for (T x : i)
        {
            r.add(x);
        }
        return r;
    }

	public static<T> HashSet set(Iterable<T> i)
	{
		HashSet<T> s = new HashSet<T>();
		for (T x : i)
		{
			s.add(x);
		}
		return s;
	}
}
