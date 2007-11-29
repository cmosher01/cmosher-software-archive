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
import stdio.StandardIn;
import stdio.StandardOut;
import video.Video;
import keyboard.Keyboard;
import keyboard.Paddles;




/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private byte[] ram = new byte[CPU6502.MEMORY_LIM];

	private final Keyboard keyboard;
	private final Video video;
	private final Paddles paddles;

	private final Card[] slot = new Card[8];

	/**
	 * @param keyboard
	 */
	public Memory(final Keyboard keyboard, final Video video, final Card disk, final Paddles paddles)
	{
		this.keyboard = keyboard;
		this.video = video;
		this.paddles = paddles;

		for (int s = 0; s < this.slot.length; ++s)
		{
			this.slot[s] = new EmptySlot();
		}

		this.slot[1] = new StandardOut();
		this.slot[2] = new StandardIn();

		if (disk != null)
		{
			this.slot[6] = disk;
		}

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
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			if (islot == 0x0)
			{
				r = this.keyboard.get();
			}
			else if (islot == 0x1)
			{
				this.keyboard.clear();
				r = this.keyboard.get();
			}
			else if (islot == 0x2)
			{
				// ignore cassette output
			}
			else if (islot == 0x3)
			{
				// TODO: toggle speaker
			}
			else if (islot == 0x4)
			{
				// ignore utility strobe
			}
			else if (islot == 0x5)
			{
				if (iswch < 0x8)
				{
					r = this.video.io(address,(byte)0);
				}
				else
				{
					// ignore annunciator outputs
				}
			}
			else if (islot == 0x6)
			{
				int sw2 = iswch & 0x7;
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
			else if (islot == 0x7)
			{
				this.paddles.startPaddleTimers();
			}
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			r = this.slot[islot].io(iswch,(byte)0);
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
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);

			if (islot == 0x1)
			{
				this.keyboard.clear();
			}
			else if (islot == 0x5)
			{
				if (iswch < 0x8)
					this.video.io(address,data);
			}
			// ignore all other switch writes
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			this.slot[islot].io(iswch,data);
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
