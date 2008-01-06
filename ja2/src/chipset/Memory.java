package chipset;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;




/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private final byte[] ram;

	/**
	 * @param keyboard
	 */
	public Memory(final int bytes)
	{
		this.ram = new byte[bytes];
		clear();
	}

	public int size()
	{
		return this.ram.length;
	}

	public void clear()
	{
		Arrays.fill(this.ram,(byte)0);
	}

	public byte read(final int address)
	{
		return this.ram[address];
	}

	public void write(final int address, final byte data)
	{
		this.ram[address] = data;
	}

	public void load(int base, final InputStream in) throws InvalidMemoryLoad
	{
		try
		{
			for (int byt = in.read(); byt != -1 && base < this.ram.length; byt = in.read())
			{
				this.ram[base++] = (byte)byt;
			}
		}
		catch (final Throwable e)
		{
			throw new InvalidMemoryLoad(e);
		}
	}
}
