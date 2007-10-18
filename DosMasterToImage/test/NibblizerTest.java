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

	public void test_encode_6and2() throws IOException
	{
		final int[] data = new int[256];
		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("dos33t0s0.bin"));
		for (int i = 0; i < data.length; ++i)
		{
			data[i] = in.read();
		}
		in.close();

		final int[] enc = Nibblizer6and2.encode(data);

		final int[] exp = new int[343];
		final BufferedInputStream innib = new BufferedInputStream(this.getClass().getResourceAsStream("16sect_t0s0.nib"));
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

	public void test_encode_5and3_alternate() throws IOException
	{
		final int[] data = new int[256];
		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("dos31t0s0.bin"));
		for (int i = 0; i < data.length; ++i)
		{
			data[i] = in.read();
		}
		in.close();

		final int[] enc = Nibblizer5and3.encode_alt(data);

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

		final int[] enc = Nibblizer5and3.encode(data);

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

	public void test_encode_4and4()
	{
		assertEquals(0xAAAA,Nibblizer4and4.encode(0x00));
		assertEquals(0xABAA,Nibblizer4and4.encode(0x01));
		assertEquals(0xAAAB,Nibblizer4and4.encode(0x02));
		assertEquals(0xABAB,Nibblizer4and4.encode(0x03));
		assertEquals(0xAEAA,Nibblizer4and4.encode(0x04));
		assertEquals(0xAFAA,Nibblizer4and4.encode(0x05));
		assertEquals(0xAEAB,Nibblizer4and4.encode(0x06));
		assertEquals(0xAFAB,Nibblizer4and4.encode(0x07));
		assertEquals(0xAAAE,Nibblizer4and4.encode(0x08));
		assertEquals(0xABAE,Nibblizer4and4.encode(0x09));
		assertEquals(0xAAAF,Nibblizer4and4.encode(0x0A));
		assertEquals(0xABAF,Nibblizer4and4.encode(0x0B));
		assertEquals(0xAEAE,Nibblizer4and4.encode(0x0C));
		assertEquals(0xAFAE,Nibblizer4and4.encode(0x0D));
		assertEquals(0xAEAF,Nibblizer4and4.encode(0x0E));
		assertEquals(0xAFAF,Nibblizer4and4.encode(0x0F));
		assertEquals(0xFEFF,Nibblizer4and4.encode(0xFE));
	}
}
