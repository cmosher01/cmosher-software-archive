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
	private byte[] ram = new byte[0x10000];
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
		if (address < 0 || this.ram.length <= address)
		{
			throw new IllegalStateException();
		}
		if (address == 0xC000)
		{
			return this.keyboard.get();
		}
		if (address == 0xC010)
		{
			this.keyboard.clear();
			return this.keyboard.get();
		}
		if (address == 0xC020 || address == 0xC060)
		{
			if (this.tape == null) return 0;
			return this.tape.io(address,(byte)0);
		}
		if (0xC050 <= address && address < 0xC058)
		{
			return this.video.io(address,(byte)0);
		}
		if (0xC0E0 <= address && address < 0xC0F0)
		{
			if (this.disk == null) return 0;
			return this.disk.io(address,(byte)0);
		}

		return this.ram[address];
	}

	public void write(final int address, final byte data)
	{
		if (address < 0 || this.ram.length <= address)
		{
			throw new IllegalStateException();
		}

		if (address == 0xC010)
		{
			this.keyboard.clear();
			return;
		}
		if (address == 0xC020 || address == 0xC060)
		{
			this.tape.io(address,data);
		}
		if (0xC0E0 <= address && address < 0xC0F0)
		{
			this.disk.io(address,data);
		}

		if (0xC000 <= address) // ROM
		{
			return;
		}

		this.ram[address] = data;
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
