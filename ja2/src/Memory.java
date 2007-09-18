import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private byte[] ram = new byte[0x10000];
	private final Keyboard keyboard;
	private final DiskInterface disk;

	/**
	 * @param keyboard
	 */
	public Memory(final Keyboard keyboard, final DiskInterface disk)
	{
		this.keyboard = keyboard;
		this.disk = disk;
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
		if (0xC0E0 <= address && address < 0xC0F0)
		{
			return this.disk.io(address,(byte)0);
		}

//		System.out.println("r $"+Integer.toHexString(address));
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
		if (0xC0E0 <= address && address < 0xC0F0)
		{
			this.disk.io(address,data);
		}

		if (0xC000 <= address) // ROM
		{
			return;
		}

//		System.out.println("w $"+Integer.toHexString(address));
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
