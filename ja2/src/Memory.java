import java.io.IOException;
import java.io.InputStream;

/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private int[] ram = new int[0x10000];
	private final Keyboard keyboard;
	private final DiskInterface disk;

	/**
	 * @param keyboard
	 */
	public Memory(final Keyboard keyboard, final DiskInterface disk)
	{
		this.keyboard = keyboard;
		this.disk = disk;
	}

	public int read(final int address)
	{
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
			//System.out.println("reading "+Integer.toHexString(address));
			return this.disk.io(address,(byte)0);
		}

//		System.out.println("r $"+Integer.toHexString(address));
		return this.ram[address % 0x10000];
	}

	public void write(final int address, int data)
	{
		data &= 0x000000FF;
		if (address == 0xC010)
		{
			this.keyboard.clear();
			return;
		}
		if (0xC0E0 <= address && address < 0xC0F0)
		{
			//System.out.println("writing "+Integer.toHexString(address));
			this.disk.io(address,(byte)data);
		}

//		System.out.println("w $"+Integer.toHexString(address));
		this.ram[address % 0x10000] = data;
	}

	public void load(int base, final InputStream in) throws IOException
	{
		for (int byt = in.read(); byt != -1; byt = in.read())
		{
			this.ram[base++ % 0x10000] = byt;
		}
	}
}
