import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/*
 * Created on Oct 22, 2007
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class sameTest
{
  @Test
	public void testSame() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		Assert.assertTrue(same.sameContents(in1,in2));
	}

  @Test
	public void testOneLonger() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4}));
		Assert.assertFalse(same.sameContents(in1,in2));
	}

  @Test
	public void testTwoLonger() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		Assert.assertFalse(same.sameContents(in1,in2));
	}

  @Test
	public void testDifferent() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,7,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		Assert.assertFalse(same.sameContents(in1,in2));
	}

  @Test
	public void testSameEmpty() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		Assert.assertTrue(same.sameContents(in1,in2));
	}

  @Test
	public void testOneNonempty() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		Assert.assertFalse(same.sameContents(in1,in2));
	}

  @Test
	public void testTwoNonempty() throws IOException
	{
		final BufferedInputStream in1 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {}));
		final BufferedInputStream in2 = new BufferedInputStream(new ByteArrayInputStream(new byte[] {1,2,3,4,5}));
		Assert.assertFalse(same.sameContents(in1,in2));
	}
}
