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
	public void testClose1()
	{
		Thing1 t1 = new Thing1();
		assertFalse(t1.closed);
		Closer<Thing1> clos1 = new Closer<Thing1>();
		clos1.close(t1);
		assertTrue(t1.closed);
	}

	public void testClose1()
	{
		Closer<Thing1> clos1 = new Closer<Thing1>();

		{
			Thing1 t1 = new Thing1();
			assertFalse(t1.closed);
			clos1.close(t1);
			assertTrue(t1.closed);
		}

		{
			Thing1 u1 = new Thing1();
			assertFalse(u1.closed);
			clos1.close(u1);
			assertTrue(u1.closed);
		}
	}
}
