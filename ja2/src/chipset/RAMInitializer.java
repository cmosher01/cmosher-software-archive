/*
 * Created on Feb 4, 2008
 */
package chipset;

import java.util.Random;

public class RAMInitializer
{
	private final Memory ram;

	public RAMInitializer(final Memory ram)
	{
		this.ram = ram;
	}

	public void init()
	{
		this.ram.clear();

		int b = 0;
		// TODO make the types of RAM chips configurable
		putBytesUntilFull(b++,1);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,1);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,1);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,2);
		putBytesUntilFull(b++,1);
	}

	private static final class Done extends RuntimeException { Done() {/*just an exception class*/} }
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
			// done filling this bit in RAM
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
		if (this.rand.nextInt(29) < 1)
			on = !on;
		final int mask = 1 << bit;
		for (int i = 0; i < c; ++i)
		{
			if (this.nextinit >= this.ram.size())
			{
				throw new Done();
			}
			byte b = this.ram.read(this.nextinit);
			if (on)
			{
				b = (byte)(b | mask);
			}
			else
			{
				b = (byte)(b & ~mask);
			}
			this.ram.write(this.nextinit,b);
			++this.nextinit;
		}
	}
}
