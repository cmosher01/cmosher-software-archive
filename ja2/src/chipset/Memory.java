package chipset;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import util.Util;




/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private final byte[] bytes;

	/**
	 * @param keyboard
	 */
	public Memory(final int bytes)
	{
		this.bytes = new byte[bytes];
		fillWithInitialValues();
	}

	public int size()
	{
		return this.bytes.length;
	}

	public void fillWithInitialValues()
	{
		Arrays.fill(this.bytes,(byte)0);
		int b = 0;
		putBytesUntilFull(b++,1);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,1);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,1);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,1);
	}

	private static class Done extends RuntimeException {}
	private int nextinit;

	private void putBytesUntilFull(int bit, int pat)
	{
		this.nextinit = 0;
		try
		{
			while (true)
			{
				if (pat==1)
					ramPattern1(bit);
				else
					ramPattern2(bit);
			}
		}
		catch (final Done ignore)
		{
		}
	}

	private void ramPattern1(final int bit) throws Done
	{
		for (int k = 0; k < 2; ++k)
		{
			for (int j = 0; j < 8; ++j)
			{
				for (int i = 0; i < 0x10; ++i)
				{
					putn(4,false,bit);
					putn(2,true,bit);
					putn(2,false,bit);
				}
				for (int i = 0; i < 0x40; ++i)
				{
					putn(2,true,bit);
					putn(2,false,bit);
				}
				for (int i = 0; i < 0x08; ++i)
				{
					putn(2,true,bit);
					putn(1,false,bit);
					putn(3,true,bit);
					putn(2,false,bit);
					putn(2,true,bit);
					putn(2,false,bit);
					putn(2,true,bit);
					putn(2,false,bit);
				}
			}
			for (int i = 0; i < 0x400; ++i)
			{
				putn(2,true,bit);
				putn(2,false,bit);
			}
		}
	}

	private void ramPattern2(final int bit) throws Done
	{
		for (int i = 0; i < 0x40; ++i)
		{
			putn(0x80,true,bit);
			putn(0x80,false,bit);
		}
	}

	private Random rand = new Random();
	private void putn(final int c, boolean on, final int bit) throws Done
	{
		if (rand.nextInt(29) < 1)
			on = !on;
		final int mask = 1 << bit;
		for (int i = 0; i < c; ++i)
		{
			if (this.nextinit >= this.bytes.length)
			{
				throw new Done();
			}
			if (on)
			{
				this.bytes[this.nextinit] = (byte)(this.bytes[this.nextinit] | mask);
			}
			else
			{
				this.bytes[this.nextinit] = (byte)(this.bytes[this.nextinit] & ~mask);
			}
			++this.nextinit;
		}
	}

	public byte read(final int address)
	{
		return this.bytes[address];
	}

	public void write(final int address, final byte data)
	{
		this.bytes[address] = data;
	}

	public void load(int base, final InputStream in) throws InvalidMemoryLoad
	{
		try
		{
			for (int byt = in.read(); byt != Util.EOF && base < this.bytes.length; byt = in.read())
			{
				this.bytes[base++] = (byte)byt;
			}
		}
		catch (final Throwable e)
		{
			throw new InvalidMemoryLoad(e);
		}
	}
}
