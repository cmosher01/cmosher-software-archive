package chipset;

import paddle.PaddleBtnInterface;
import paddle.PaddlesInterface;
import speaker.SpeakerClicker;
import keyboard.KeyboardInterface;
import video.VideoMode;




/*
 * Created on Aug 1, 2007
 */
public class AddressBus implements chipset.cpu.AddressBus
{
	private final Memory ram;
	private final Memory rom;
	private final KeyboardInterface keyboard;
	private final VideoMode video;
	private final PaddlesInterface paddles;
	private final SpeakerClicker speaker;
	private final Slots slots;
	private final PaddleBtnInterface paddleButtons;

	private byte data; // this emulates the (floating) data bus



	public AddressBus(final Memory ram, final Memory rom, final KeyboardInterface keyboard, final VideoMode video, final PaddlesInterface paddles, final PaddleBtnInterface paddleButtons, final SpeakerClicker speaker, final Slots slots)
	{
		this.ram = ram;
		this.rom = rom;
		this.keyboard = keyboard;
		this.video = video;
		this.paddles = paddles;
		this.slots = slots;
		this.paddleButtons = paddleButtons;
		this.speaker = speaker;
	}

	public byte read(final int address)
	{
		if ((address >> 14 == 3)) // >= $C000
		{
			if ((address >> 12) == 0xC)
			{
				// 11007sssxxxxxxxx
				final boolean seventh = (address & 0x0800) != 0;
				final int slot = (address >> 8) & 7;
				if (seventh)
				{
					this.data = this.slots.readSeventhRom(address & 0x07FF);
				}
				else if (slot == 0)
				{
					this.data = readSwitch(address & 0x00FF);
				}
				else
				{
					this.data = this.slots.readRom(slot,address & 0x00FF);
				}
			}
			else
			{
				this.data = this.slots.ioBankRom(address - 0xD000,this.data,false);
				if (!this.slots.inhibitMotherboardRom())
				{
					this.data = this.rom.read(address - 0xD000);
				}
			}
		}
		else // < $C000
		{
			this.data = this.ram.read(address);
		}

		return this.data;
	}

	public void write(final int address, final byte d)
	{
		this.data = d;

		if ((address >> 14 == 3)) // >= $C000
		{
			if ((address >> 12) == 0xC)
			{
				// 11007sssxxxxxxxx
				final boolean seventh = (address & 0x0800) != 0;
				final int slot = (address >> 8) & 7;
				if (!seventh && slot == 0)
				{
					writeSwitch(address & 0x00FF);
				}
			}
			else
			{
				this.slots.ioBankRom(address - 0xD000,this.data,true);
			}
		}
		else // < $C000
		{
			this.ram.write(address,this.data);
		}
	}

	public void reset()
	{
		this.slots.reset();
	}







	private byte readSwitch(int address)
	{
		if (address < 0x80)
		{
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			if (islot == 0x0)
			{
				this.data = this.keyboard.get();
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
				this.speaker.click();
			}
			else if (islot == 0x4)
			{
				// ignore utility strobe
			}
			else if (islot == 0x5)
			{
				if (iswch < 0x8)
				{
					this.data = this.video.io(address,this.data);
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
					setD7(this.paddleButtons.isDown(sw2));
				}
				else
				{
					sw2 &= 3;
					setD7(!this.paddles.isTimedOut(sw2));
				}
			}
			else if (islot == 0x7)
			{
				this.paddles.startTimers();
				setD7(true);
			}
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			this.data = this.slots.io(islot,iswch,this.data,false);
		}

		return this.data;
	}

	private void setD7(final boolean set)
	{
		if (set)
		{
			this.data = (byte)(this.data | 0x80);
		}
		else
		{
			this.data = (byte)(this.data & 0x7F);
		}
	}

	private void writeSwitch(int address)
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
					this.video.io(address,this.data);
			}
			// ignore all other switch writes
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			this.slots.io(islot,iswch,this.data,true);
		}
	}
}
