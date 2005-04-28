package nu.mine.mosher.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Various general-purpose utility functions.
 *
 * @author Chris Mosher
 */
public final class Util
{
	private Util()
	{
		assert false : "can't instantiate";
	}

	/**
	 * Documents a variable as being unused. Calling
	 * this method can be useful to prevent certain
	 * compiler warnings regarding unused variables.
	 * @param <T> class of object
	 * @param x object
	 */
	public static<T> void unused(T x)
	{
		final T unused = x; x = unused;
	}

    /**
     * Documents a <code>boolean</code> variable as being unused.
     * @param primitiveBoolean
     */
    public static void unused(boolean primitiveBoolean)
    {
        boolean x = primitiveBoolean;
        primitiveBoolean = x;
    }

    /**
     * Documents a <code>byte</code> variable as being unused.
     * @param primitiveByte
     */
    public static void unused(byte primitiveByte)
    {
        byte x = primitiveByte;
        primitiveByte = x;
    }

    /**
     * Documents a <code>short</code> variable as being unused.
     * @param primitiveShort
     */
    public static void unused(short primitiveShort)
    {
        short x = primitiveShort;
        primitiveShort = x;
    }

    /**
     * Documents a <code>int</code> variable as being unused.
     * @param primitiveInt
     */
    public static void unused(int primitiveInt)
    {
        int x = primitiveInt;
        primitiveInt = x;
    }

    /**
     * Documents a <code>long</code> variable as being unused.
     * @param primitiveLong
     */
    public static void unused(long primitiveLong)
    {
        long x = primitiveLong;
        primitiveLong = x;
    }

    /**
     * Documents a <code>char</code> variable as being unused.
     * @param primitiveChar
     */
    public static void unused(char primitiveChar)
    {
        char x = primitiveChar;
        primitiveChar = x;
    }

    /**
     * Documents a <code>float</code> variable as being unused.
     * @param primitiveFloat
     */
    public static void unused(float primitiveFloat)
    {
        float x = primitiveFloat;
        primitiveFloat = x;
    }

    /**
     * Documents a <code>double</code> variable as being unused.
     * @param primitiveDouble
     */
    public static void unused(double primitiveDouble)
    {
        double x = primitiveDouble;
        primitiveDouble = x;
    }


    /**
     * Logs an "Arguments ignored." message if the
     * given array is empty.
     * @param rArgument
     */
    public static void ignoreMainArguments(String[] rArgument)
    {
        if (rArgument.length > 0)
        {
            Logger.global.warning("Arguments ignored.");
        }
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



    /**
     * Creates a <code>List</code> containing all 
     * items (of type <code>T</code>) from the given <code>Iterable</code>
     * object.
     * @param <T>
     * @param i
     * @return a new <code>ArrayList</code>
     */
    public static<T> ArrayList<T> list(Iterable<T> i)
    {
        ArrayList<T> r = new ArrayList<T>();
        addAll(i.iterator(),r);
        return r;
    }

    /**
     * Adds all the items from the given <code>Iterator</code> to the
     * given <code>Collection</code>.
     * @param <T> type of item in each container
     * @param i the <code>Iterator</code> to get items from
     * @param collection the <code>Collection</code> to add the items to
     */
    public static<T> void addAll(Iterator<T> i, Collection<T> collection)
    {
        while (i.hasNext())
        {
            collection.add(i.next());
        }
    }

//    /**
//     * Iterates through the given Iterator and adds
//     * its elements to the given collection.
//     * @param i the Iterator to get the elements of
//     * @param collection the Collection to add the elements to
//     */
//    public static void addAll(Iterator i, Collection collection)
//    {
//        while (i.hasNext())
//        {
//            collection.add(i.next());
//        }
//    }
//
//    /**
//     * Iterates through the given Iterator and builds
//     * an ArrayList out of its elements.
//     * @param i the Iterator to get the elements of
//     * @return an ArrayList of the elements from i
//     */
//    public static ArrayList list(Iterator i)
//    {
//        ArrayList r = new ArrayList();
//        addAll(i,r);
//        return r;
//    }

    /**
     * Iterates through the given Iterator and builds
     * a HashSet out of its elements.
     * @param i the Iterator to get the elements of
     * @return a HashSet of the elements from i
     */
    public static HashSet set(Iterator i)
    {
        HashSet r = new HashSet();
        addAll(i,r);
        return r;
    }






	public static int compare(byte x0, byte x1)
	{
		return x0 < x1 ? -1 : x1 < x0 ? +1 : 0;
	}
	public static int compare(short x0, short x1)
	{
		return x0 < x1 ? -1 : x1 < x0 ? +1 : 0;
	}
	public static int compare(int x0, int x1)
	{
		return x0 < x1 ? -1 : x1 < x0 ? +1 : 0;
	}
	public static int compare(long x0, long x1)
	{
		return x0 < x1 ? -1 : x1 < x0 ? +1 : 0;
	}
	public static int compare(char x0, char x1)
	{
		return x0 < x1 ? -1 : x1 < x0 ? +1 : 0;
	}


	public Comparable min(Comparable x0, Comparable x1)
	{
		int cmp = x0.compareTo(x1);
		if (cmp <= 0)
		{
			return x0;
		}
		else
		{
			return x1;
		}
	}
	public Comparable max(Comparable x0, Comparable x1)
	{
		int cmp = x0.compareTo(x1);
		if (cmp >= 0)
		{
			return x0;
		}
		else
		{
			return x1;
		}
	}
}
