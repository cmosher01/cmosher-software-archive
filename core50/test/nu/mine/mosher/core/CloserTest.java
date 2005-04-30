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
		public void close() { closed = true; }
	}

	private static final class Thing2
	{
		public boolean closed;
		public void close() { closed = true; }
	}




	public void testClose1()
	{
		Thing1 t1 = new Thing1();
		assertFalse(t1.closed);
		Closer.close(t1);
		assertTrue(t1.closed);

		assertFalse(Closer.hasErrors());
	}

	public void testClose2()
	{
		Closer clos1 = new Closer();

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

		assertFalse(clos1.hasErrors());
	}

	public void testClose3()
	{
		Closer clos1 = new Closer();
		Closer clos2 = new Closer();

		{
			Thing1 t1 = new Thing1();
			assertFalse(t1.closed);
			clos1.close(t1);
			assertTrue(t1.closed);
		}

		{
			Thing2 u1 = new Thing2();
			assertFalse(u1.closed);
			clos2.close(u1);
			assertTrue(u1.closed);
		}

		assertFalse(clos1.hasErrors());
		assertFalse(clos2.hasErrors());
	}
}
