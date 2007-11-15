package chipset;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import video.Video;
import keyboard.Keyboard;
import disk.DiskInterface;
import disk.TapeInterface;

/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private byte[] ram = new byte[CPU6502.MEMORY_LIM];
	private final Keyboard keyboard;
	private final Video video;
	private final DiskInterface disk;
	private final TapeInterface tape;

	/**
	 * @param keyboard
	 */
	public Memory(final Keyboard keyboard, final Video video, final DiskInterface disk, final TapeInterface tape)
	{
		this.keyboard = keyboard;
		this.video = video;
		this.disk = disk;
		this.tape = tape;
		clear();
	}

	public void clear()
	{
		Arrays.fill(this.ram,(byte)0);
	}

	public byte read(final int address)
	{
//		if (address < 0 || CPU6502.MEMORY_LIM <= address)
//		{
//			throw new IllegalStateException();
//		}
		if (0xC000 <= address && address < 0xC100)
		{
			return readSwitch(address);
		}

		return this.ram[address];
	}

	private byte readSwitch(int address)
	{
		byte r = -1;

		address &= 0x00FF;

		if (address < 0x80)
		{
			final int seg = address >> 4;
			final int sw = address & 0x0F;
			if (seg == 0x0)
			{
				r = this.keyboard.get();
			}
			else if (seg == 0x1)
			{
				this.keyboard.clear();
				r = this.keyboard.get();
			}
			else if (seg == 0x2 || seg == 0x6)
			{
				if (this.tape != null)
				{
					r = this.tape.io(address,(byte)0);
				}
			}
			else if (seg == 0x3)
			{
				// TODO: toggle speaker
			}
			else if (seg == 0x4)
			{
				// TODO: game I/O
			}
			else if (seg == 0x5)
			{
				if (sw < 0x8)
					r = this.video.io(address,(byte)0);
//				else
					// TODO
			}
			if (seg == 0x7)
			{
				// TODO: paddles
			}
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int slot = address >> 4;
	
			if (slot == 0x6)
			{
				if (this.disk != null)
				{
					r = this.disk.io(address,(byte)0);
				}
			}
		}

		return r;
	}

	public void write(final int address, final byte data)
	{
//		if (address < 0 || CPU6502.MEMORY_LIM <= address)
//		{
//			throw new IllegalStateException();
//		}
		if (0xC000 <= address && address < 0xC100)
		{
			writeSwitch(address,data);
		}

		if ((address >> 14) == 3) // ROM
		{
			return;
		}

		this.ram[address] = data;
	}

	private void writeSwitch(int address, byte data)
	{
		address &= 0x00FF;

		if (address < 0x80)
		{
			final int seg = address >> 4;
			final int sw = address & 0x0F;

			if (seg == 0x0)
			{
				// nothing?
			}
			else if (seg == 0x1)
			{
				this.keyboard.clear();
			}
			else if (seg == 0x2 || seg == 0x6)
			{
				if (this.tape != null)
				{
					this.tape.io(address,data);
				}
			}
			else if (seg == 0x5)
			{
				if (sw < 0x8)
					this.video.io(address,data);
//				else
					// TODO
			}
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int slot = address >> 4;
	
			if (slot == 0x6)
			{
				if (this.disk != null)
				{
					this.disk.io(address,data);
				}
			}
		}
	}

	public void load(int base, final InputStream in) throws IOException
	{
		if (base < 0 || this.ram.length <= base)
		{
			throw new IllegalStateException();
		}
		for (int byt = in.read(); byt != -1 && base < this.ram.length; byt = in.read())
		{
			this.ram[base++] = (byte)byt;
		}
	}

	public void dump() throws IOException
	{
		final OutputStream fil = new BufferedOutputStream(new FileOutputStream(new File("dump.bin")));
		for (int i = 0; i < this.ram.length; ++i)
		{
			fil.write(this.ram[i]);
		}
		fil.close();
	}
}
