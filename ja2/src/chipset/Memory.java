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
	private static final byte CLEAR_VALUE = 0;

	private final byte[] bytes;

	/**
	 * @param keyboard
	 */
	public Memory(final int bytes)
	{
		this.bytes = new byte[bytes];
	}

	public int size()
	{
		return this.bytes.length;
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

	public void clear()
	{
		Arrays.fill(this.bytes,CLEAR_VALUE);
	}
}
