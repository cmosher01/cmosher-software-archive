import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import junit.framework.TestCase;

/*
 * Created on Oct 22, 2007
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class sameTest extends TestCase
{
	/**
	 * Test method for {@link same#same(java.io.BufferedInputStream, java.io.BufferedInputStream)}.
	 */
	public void testSame() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		assertTrue(same.sameContents(in1,in2));
	}

	public void testOneLonger() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4}));
		assertFalse(same.sameContents(in1,in2));
	}

	public void testTwoLonger() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		assertFalse(same.sameContents(in1,in2));
	}

	public void testDifferent() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,7,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		assertFalse(same.sameContents(in1,in2));
	}

	public void testSameEmpty() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		assertTrue(same.sameContents(in1,in2));
	}

	public void testOneNonempty() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		assertFalse(same.sameContents(in1,in2));
	}

	public void testTwoNonempty() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		assertFalse(same.sameContents(in1,in2));
	}
}
