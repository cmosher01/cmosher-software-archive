package chipset;

import paddle.PaddleBtnInterface;
import paddle.PaddlesInterface;
import keyboard.KeyboardInterface;
import video.Video;




/*
 * Created on Aug 1, 2007
 */
public class AddressBus implements chipset.cpu.AddressBus
{
	private final Memory ram;
	private final Memory rom;
	private final KeyboardInterface keyboard;
	private final Video video;
	private final PaddlesInterface paddles;
	private final Slots slots;
	private final PaddleBtnInterface paddleButtons;





	public AddressBus(final Memory ram, final Memory rom, final KeyboardInterface keyboard, final Video video, final PaddlesInterface paddles, final PaddleBtnInterface paddleButtons, final Slots slots)
	{
		this.ram = ram;
		this.rom = rom;
		this.keyboard = keyboard;
		this.video = video;
		this.paddles = paddles;
		this.slots = slots;
		this.paddleButtons = paddleButtons;
	}

	public byte read(final int address)
	{
		byte r = this.video.getDataBus();

		if ((address >> 14 == 3)) // >= $C000
		{
			if ((address >> 12) == 0xC)
			{
				// 11007sssxxxxxxxx
				final boolean seventh = (address & 0xF800) == 0xC800;
				final int slot = (address >> 8) & 7;
				if (seventh)
				{
					r = this.slots.readSeventhRom(address & 0x07FF);
				}
				else if (slot == 0)
				{
					r = readSwitch(address & 0x00FF,r);
				}
				else
				{
					r = this.slots.readRom(slot,address & 0x00FF);
				}
			}
			else
			{
				r = this.slots.ioBankRom(address - 0xD000,r,false);
				if (!this.slots.inhibitMotherboardRom())
				{
					r = this.rom.read(address - 0xD000);
				}
			}
		}
		else // < $C000
		{
			r = this.ram.read(address);
		}

		return r;
	}

	public void write(final int address, final byte data)
	{
		if ((address >> 14 == 3)) // >= $C000
		{
			if ((address >> 12) == 0xC)
			{
				// 11007sssxxxxxxxx
				final boolean seventh = (address & 0xF800) == 0xC800;
				final int slot = (address >> 8) & 7;
				if (!seventh && slot == 0)
				{
					writeSwitch(address & 0x00FF, data);
				}
			}
			else
			{
				this.slots.ioBankRom(address - 0xD000,data,true);
			}
		}
		else // < $C000
		{
			this.ram.write(address,data);
		}
	}

	public void reset()
	{
		this.slots.reset();
	}







	private byte readSwitch(int address, byte r)
	{
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
					r = this.video.io(address,r);
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
					if (this.paddleButtons.isDown(sw2))
					{
						r = (byte)(r | 0x80);
					}
					else
					{
						r = (byte)(r & 0x7F);
					}
				}
				else
				{
					sw2 &= 3;
					if (this.paddles.isTimedOut(sw2))
					{
						r = (byte)(r & 0x7F);
					}
					else
					{
						r = (byte)(r | 0x80);
					}
				}
			}
			else if (islot == 0x7)
			{
				this.paddles.startTimers();
				r = (byte)(r | 0x80);
			}
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			r = this.slots.io(islot,iswch,r,false);
		}

		return r;
	}

	private void writeSwitch(int address, byte data)
	{
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
			this.slots.io(islot,iswch,data,true);
		}
	}
}
