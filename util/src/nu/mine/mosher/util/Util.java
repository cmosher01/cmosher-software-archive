package nu.mine.mosher.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

public final class Util
{
	private Util() throws UnsupportedOperationException
	{
        throw new UnsupportedOperationException();
	}



	/**
	 * Documents an Object as being unused.
	 * @param o the object that is (documented as being) not used
	 */
	public static void unused(Object o)
	{
		Object x = o;
		o = x;
	}

    public static void unused(boolean primitiveBoolean)
    {
        boolean x = primitiveBoolean;
        primitiveBoolean = x;
    }

    public static void unused(byte primitiveByte)
    {
        byte x = primitiveByte;
        primitiveByte = x;
    }

    public static void unused(short primitiveShort)
    {
        short x = primitiveShort;
        primitiveShort = x;
    }

    public static void unused(int primitiveInt)
    {
        int x = primitiveInt;
        primitiveInt = x;
    }

    public static void unused(long primitiveLong)
    {
        long x = primitiveLong;
        primitiveLong = x;
    }

    public static void unused(char primitiveChar)
    {
        char x = primitiveChar;
        primitiveChar = x;
    }

    public static void unused(float primitiveFloat)
    {
        float x = primitiveFloat;
        primitiveFloat = x;
    }

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





//  TODO Fix these for Java 1.5:
    public static ArrayList<T> list(Iterable<T> i)
    {
        ArrayList<T> r = new ArrayList<T>();
        addAll(i,r);
        return r;
    }
    public static void addAll(Iterator<?> i, Collection<?> collection)
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
