/*
 * Created on Apr 29, 2005
 */
package nu.mine.mosher.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class CloserTest
{
	private static final class Thing1
	{
		public boolean closed;
		@SuppressWarnings("unused")
		public void close() { this.closed = true; }
	}

	private static final class Thing2
	{
		public boolean closed;
		@SuppressWarnings("unused")
		public void close() { this.closed = true; }
	}



	@Test
	public void testClose1()
	{
		@SuppressWarnings("synthetic-access")
		Thing1 t1 = new Thing1();
		assertFalse(t1.closed);
		final Throwable hadError = Closer.close(t1);
		assertTrue(t1.closed);

		assertTrue(hadError == null);
	}

	@Test
	public void testClose2()
	{
		{
			@SuppressWarnings("synthetic-access")
			Thing1 t1 = new Thing1();
			assertFalse(t1.closed);
			final Throwable hadError = Closer.close(t1);
			assertTrue(t1.closed);
			assertTrue(hadError == null);
		}

		{
			@SuppressWarnings("synthetic-access")
			Thing1 u1 = new Thing1();
			assertFalse(u1.closed);
			final Throwable hadError = Closer.close(u1);
			assertTrue(u1.closed);
			assertTrue(hadError == null);
		}
	}

	@Test
	public void testClose3()
	{
		{
			@SuppressWarnings("synthetic-access")
			Thing1 t1 = new Thing1();
			assertFalse(t1.closed);
			final Throwable hadError = Closer.close(t1);
			assertTrue(t1.closed);
			assertTrue(hadError == null);
		}

		{
			@SuppressWarnings("synthetic-access")
			Thing2 u1 = new Thing2();
			assertFalse(u1.closed);
			final Throwable hadError = Closer.close(u1);
			assertTrue(u1.closed);
			assertTrue(hadError == null);
		}
	}
}
