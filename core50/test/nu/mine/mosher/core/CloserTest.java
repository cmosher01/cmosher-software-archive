/*
 * Created on Apr 29, 2005
 */
package nu.mine.mosher.core;

import junit.framework.TestCase;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class CloserTest extends TestCase
{
	public static void main(String[] args)
	{
	}

	private static final class Thing1
	{
		public boolean closed;
		public void close()
		{
			closed = true;
		}
	}
	public void testClose()
	{
		Thing1 t1 = new Thing1();
		assertFalse(t1.closed);
		Closer<Thing1> clos1 = new Closer<Thing1>();
		clos1.close(t1);
		assertTrue(t1.closed);
	}
}
