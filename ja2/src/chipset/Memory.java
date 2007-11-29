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
	private final byte[] ram = new byte[CPU6502.MEMORY_LIM];

	/**
	 * @param keyboard
	 */
	public Memory()
	{
		clear();
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
		if ((address >> 14) == 3) // ROM
		{
			return;
		}

		this.ram[address] = data;
	}

	public void load(int base, final InputStream in) throws InvalidMemoryLoad
	{
		if (base < 0 || this.ram.length <= base)
		{
			throw new InvalidMemoryLoad("Invalid base address: "+Integer.toHexString(base));
		}
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

	final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public void dump() throws IOException
	{
		final String name = "dump"+this.fmt.format(new Date())+".bin";
		final OutputStream fil = new BufferedOutputStream(new FileOutputStream(new File(name)));
		for (int i = 0; i < this.ram.length; ++i)
		{
			fil.write(this.ram[i]);
		}
		fil.close();
	}
}
