import java.io.BufferedInputStream;
import java.io.IOException;
import junit.framework.TestCase;

/*
 * Created on Oct 15, 2007
 */
public class NibblizerTest extends TestCase
{
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void test_encode_5and3_alternate() throws IOException
	{
		final int[] data = new int[256];
		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("dos31t0s0.bin"));
		for (int i = 0; i < data.length; ++i)
		{
			data[i] = in.read();
		}
		in.close();

		final int[] enc = Nibblizer.encode_5and3_alternate(data);

		final int[] exp = new int[411];
		final BufferedInputStream innib = new BufferedInputStream(this.getClass().getResourceAsStream("13sect_t0s0.nib"));
		for (int i = 0; i < exp.length; ++i)
		{
			exp[i] = innib.read();
		}
		innib.close();

		assertEquals(exp.length,enc.length);
		for (int i = 0; i < exp.length; ++i)
		{
			assertEquals(exp[i],enc[i]);
		}
	}

	public void test_encode_5and3() throws IOException
	{
		final int[] data = new int[256];
		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("dos31t0s1.bin"));
		for (int i = 0; i < data.length; ++i)
		{
			data[i] = in.read();
		}
		in.close();

		final int[] enc = Nibblizer.encode_5and3(data);

		final int[] exp = new int[411];
		final BufferedInputStream innib = new BufferedInputStream(this.getClass().getResourceAsStream("13sect_t0s1.nib"));
		for (int i = 0; i < exp.length; ++i)
		{
			exp[i] = innib.read();
		}
		innib.close();

		assertEquals(exp.length,enc.length);
		for (int i = 0; i < exp.length; ++i)
		{
			assertEquals(exp[i],enc[i]);
		}
	}
}
