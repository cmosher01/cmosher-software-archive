package chipset;
import java.io.IOException;
import java.io.InputStream;
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
		if (address < 0 || CPU6502.MEMORY_LIM <= address)
		{
			throw new IllegalStateException();
		}
		if (0xC000 <= address && address < 0xC100)
		{
			return readSwitch(address);
		}
		return this.ram[address];
	}

	private byte readSwitch(int address)
	{
		address &= 0x00FF;

		if (address < 0x80)
		{
			if (address == 0x00)
			{
				return this.keyboard.get();
			}
			if (address == 0x10)
			{
				this.keyboard.clear();
				return this.keyboard.get();
			}
			if (address == 0x20 || address == 0x60)
			{
				if (this.tape == null)
				{
					return -1;
				}
				return this.tape.io(address,(byte)0);
			}
			if (0x50 <= address && address < 0x58)
			{
				return this.video.io(address,(byte)0);
			}
			return -1;
		}

		// slot I/O switches
		address &= 0x7F;
		final int slot = address >> 4;

		if (slot == 6)
		{
			if (this.disk == null)
			{
				return -1;
			}
			return this.disk.io(address,(byte)0);
		}
		return -1;
	}

	public void write(final int address, final byte data)
	{
		if (address < 0 || CPU6502.MEMORY_LIM <= address)
		{
			throw new IllegalStateException();
		}
		if (0xC000 <= address && address < 0xC100)
		{
			writeSwitch(address,data);
			return;
		}
		if (0xC100 <= address) // ROM
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
			if (address == 0x10)
			{
				this.keyboard.clear();
				return;
			}
			if (address == 0x20 || address == 0x60)
			{
				this.tape.io(address,data);
			}
			if (0x50 <= address && address < 0x58)
			{
				this.video.io(address,data);
			}
		}

		// slot I/O switches
		address &= 0x7F;
		final int slot = address >> 4;

		if (slot == 6)
		{
			this.disk.io(address,data);
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
}
