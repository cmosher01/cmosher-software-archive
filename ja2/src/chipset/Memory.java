package chipset;

import java.io.InputStream;
import java.util.Arrays;
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
