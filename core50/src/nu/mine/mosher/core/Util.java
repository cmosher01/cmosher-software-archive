package nu.mine.mosher.core;

public class Util
{
	private Util()
	{
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
}
