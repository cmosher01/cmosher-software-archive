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
import video.Video;
import keyboard.Keyboard;
import keyboard.Paddles;
import disk.DiskInterface;



/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private byte[] ram = new byte[CPU6502.MEMORY_LIM];
	private final Keyboard keyboard;
	private final Video video;
	private final DiskInterface disk;
	private final Paddles paddles;
	private int slot2latch;
	private boolean inHasCRs;

	/**
	 * @param keyboard
	 */
	public Memory(final Keyboard keyboard, final Video video, final DiskInterface disk, final Paddles paddles)
	{
		this.keyboard = keyboard;
		this.video = video;
		this.disk = disk;
		this.paddles = paddles;
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
			else if (seg == 0x2)
			{
				// ignore cassette output
			}
			else if (seg == 0x3)
			{
				// TODO: toggle speaker
			}
			else if (seg == 0x4)
			{
				// ignore utility strobe
			}
			else if (seg == 0x5)
			{
				if (sw < 0x8)
				{
					r = this.video.io(address,(byte)0);
				}
				else
				{
					// ignore annunciator outputs
				}
			}
			else if (seg == 0x6)
			{
				int sw2 = sw & 0x7;
				if (sw2 == 0)
				{
					// ignore cassette input
				}
				else if (sw2 < 4)
				{
					r = this.keyboard.isPaddleButtonDown(sw2) ? (byte)0x80 : 0;
				}
				else
				{
					sw2 &= 3;
					r = this.paddles.paddleTimedOut(sw2) ? 0 : (byte)0x80;
				}
			}
			else if (seg == 0x7)
			{
				this.paddles.startPaddleTimers();
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
			else if (slot == 0x2)
			{
				// IN#2 reads from standard in
				final int sw = address & 0x0F;
				if (sw == 0)
				{
					if (this.slot2latch >= 0x80)
					{
						r = (byte)this.slot2latch;
					}
					else
					{
						try
						{
							int c = System.in.read();
							if (c == '\r')
							{
								this.inHasCRs = true;
							}
							while (c == '\n')
							{
								if (this.inHasCRs)
								{
									c = System.in.read();
								}
								else
								{
									c = '\r';
								}
							}
							r = (byte)(c | 0x80);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
				else if (sw == 1)
				{
					this.slot2latch &= 0x7F;
					r = (byte)this.slot2latch;
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

			if (seg == 0x1)
			{
				this.keyboard.clear();
			}
			else if (seg == 0x5)
			{
				if (sw < 0x8)
					this.video.io(address,data);
			}
			// ignore all other switch writes
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
			else if (slot == 0x1)
			{
				// PR#1 writes to standard out
				final char c = (char)(data&0x7F);
				if (c == '\r')
				{
					System.out.println();
				}
				else
				{
					System.out.print(c);
				}
				System.out.flush();
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
