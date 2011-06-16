import java.io.BufferedInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;



/*
 * Created on Oct 15, 2007
 */
public class NibblizerTest
{
  @Test
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

		Assert.assertEquals(exp.length,enc.length);
		for (int i = 0; i < exp.length; ++i)
		{
		  Assert.assertEquals(exp[i],enc[i]);
		}
	}

  @Test
	public void test_decode_6and2() throws IOException, CorruptDataException
	{
		final int[] enc = new int[343];
		final BufferedInputStream innib = new BufferedInputStream(this.getClass().getResourceAsStream("16sect_t0s0.nib"));
		for (int i = 0; i < enc.length; ++i)
		{
			enc[i] = innib.read();
		}
		innib.close();

		final int[] data = Nibblizer6and2.decode(enc);

		final int[] exp = new int[256];
		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("dos33t0s0.bin"));
		for (int i = 0; i < exp.length; ++i)
		{
			exp[i] = in.read();
		}
		in.close();



		Assert.assertEquals(exp.length,data.length);
		for (int i = 0; i < data.length; ++i)
		{
		  Assert.assertEquals(exp[i],data[i]);
		}
	}

  @Test
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

		Assert.assertEquals(exp.length,enc.length);
		for (int i = 0; i < exp.length; ++i)
		{
		  Assert.assertEquals(exp[i],enc[i]);
		}
	}

  @Test
	public void test_decode_5and3_alternate() throws IOException
	{
		final int[] enc = new int[411];
		final BufferedInputStream innib = new BufferedInputStream(this.getClass().getResourceAsStream("13sect_t0s0.nib"));
		for (int i = 0; i < enc.length; ++i)
		{
			enc[i] = innib.read();
		}
		innib.close();

		final int[] data = Nibblizer5and3.decode_alt(enc);

		final int[] exp = new int[256];
		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("dos31t0s0.bin"));
		for (int i = 0; i < exp.length; ++i)
		{
			exp[i] = in.read();
		}
		in.close();


		Assert.assertEquals(exp.length,data.length);
		for (int i = 0; i < data.length; ++i)
		{
		  Assert.assertEquals("at index "+i,exp[i],data[i]);
		}
	}

  @Test
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

		Assert.assertEquals(exp.length,enc.length);
		for (int i = 0; i < exp.length; ++i)
		{
		  Assert.assertEquals(exp[i],enc[i]);
		}
	}

  @Test
	public void test_decode_5and3() throws IOException, CorruptDataException
	{
		final int[] enc = new int[411];
		final BufferedInputStream innib = new BufferedInputStream(this.getClass().getResourceAsStream("13sect_t0s1.nib"));
		for (int i = 0; i < enc.length; ++i)
		{
			enc[i] = innib.read();
		}
		innib.close();

		final int[] data = Nibblizer5and3.decode(enc);

		final int[] exp = new int[256];
		final BufferedInputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("dos31t0s1.bin"));
		for (int i = 0; i < exp.length; ++i)
		{
			exp[i] = in.read();
		}
		in.close();



		Assert.assertEquals(exp.length,data.length);
		for (int i = 0; i < exp.length; ++i)
		{
		  Assert.assertEquals(exp[i],data[i]);
		}
	}

  @Test
	public void test_encode_4and4()
	{
    Assert.assertEquals(0xAAAA,Nibblizer4and4.encode(0x00));
    Assert.assertEquals(0xABAA,Nibblizer4and4.encode(0x01));
    Assert.assertEquals(0xAAAB,Nibblizer4and4.encode(0x02));
    Assert.assertEquals(0xABAB,Nibblizer4and4.encode(0x03));
    Assert.assertEquals(0xAEAA,Nibblizer4and4.encode(0x04));
    Assert.assertEquals(0xAFAA,Nibblizer4and4.encode(0x05));
    Assert.assertEquals(0xAEAB,Nibblizer4and4.encode(0x06));
    Assert.assertEquals(0xAFAB,Nibblizer4and4.encode(0x07));
    Assert.assertEquals(0xAAAE,Nibblizer4and4.encode(0x08));
    Assert.assertEquals(0xABAE,Nibblizer4and4.encode(0x09));
    Assert.assertEquals(0xAAAF,Nibblizer4and4.encode(0x0A));
    Assert.assertEquals(0xABAF,Nibblizer4and4.encode(0x0B));
    Assert.assertEquals(0xAEAE,Nibblizer4and4.encode(0x0C));
    Assert.assertEquals(0xAFAE,Nibblizer4and4.encode(0x0D));
    Assert.assertEquals(0xAEAF,Nibblizer4and4.encode(0x0E));
    Assert.assertEquals(0xAFAF,Nibblizer4and4.encode(0x0F));
    Assert.assertEquals(0xFEFF,Nibblizer4and4.encode(0xFE));
	}

  @Test
	public void test_decode_4and4()
	{
    Assert.assertEquals(0x00,Nibblizer4and4.decode(0xAAAA));
    Assert.assertEquals(0x01,Nibblizer4and4.decode(0xABAA));
    Assert.assertEquals(0x02,Nibblizer4and4.decode(0xAAAB));
    Assert.assertEquals(0x03,Nibblizer4and4.decode(0xABAB));
    Assert.assertEquals(0x04,Nibblizer4and4.decode(0xAEAA));
    Assert.assertEquals(0x05,Nibblizer4and4.decode(0xAFAA));
    Assert.assertEquals(0x06,Nibblizer4and4.decode(0xAEAB));
    Assert.assertEquals(0x07,Nibblizer4and4.decode(0xAFAB));
    Assert.assertEquals(0x08,Nibblizer4and4.decode(0xAAAE));
    Assert.assertEquals(0x09,Nibblizer4and4.decode(0xABAE));
    Assert.assertEquals(0x0A,Nibblizer4and4.decode(0xAAAF));
    Assert.assertEquals(0x0B,Nibblizer4and4.decode(0xABAF));
    Assert.assertEquals(0x0C,Nibblizer4and4.decode(0xAEAE));
    Assert.assertEquals(0x0D,Nibblizer4and4.decode(0xAFAE));
    Assert.assertEquals(0x0E,Nibblizer4and4.decode(0xAEAF));
    Assert.assertEquals(0x0F,Nibblizer4and4.decode(0xAFAF));
    Assert.assertEquals(0xFE,Nibblizer4and4.decode(0xFEFF));
	}
}
