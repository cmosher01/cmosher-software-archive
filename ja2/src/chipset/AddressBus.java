package chipset;

import keyboard.KeyboardInterface;
import keyboard.PaddleButtons;
import keyboard.Paddles;
import video.Video;




/*
 * Created on Aug 1, 2007
 */
public class AddressBus implements chipset.cpu.AddressBus
{
	private final Memory memory;
	private final KeyboardInterface keyboard;
	private final Video video;
	private final Paddles paddles;
	private final Slots slots;
	private final PaddleButtons paddleButtons;





	public AddressBus(final Memory memory, final KeyboardInterface keyboard, final Video video, final Paddles paddles, final Slots slots, final PaddleButtons paddleButtons)
	{
		this.memory = memory;
		this.keyboard = keyboard;
		this.video = video;
		this.paddles = paddles;
		this.slots = slots;
		this.paddleButtons = paddleButtons;
	}

	public byte read(final int address)
	{
		final byte r;

		if ((address >> 8) == 0xC0)
		{
			r = readSwitch(address & 0x00FF);
		}
		else
		{
			r = this.memory.read(address);
		}

		return r;
	}

	public void write(final int address, final byte data)
	{
		if ((address >> 8) == 0xC0)
		{
			writeSwitch(address & 0x00FF,data);
		}
		else
		{
			this.memory.write(address,data);
		}
	}







	private byte readSwitch(int address)
	{
		byte r = -1;

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
					r = this.paddleButtons.isDown(sw2) ? (byte)0x80 : 0;
				}
				else
				{
					sw2 &= 3;
					r = this.paddles.isTimedOut(sw2) ? 0 : (byte)0x80;
				}
			}
			else if (islot == 0x7)
			{
				this.paddles.startTimers();
				r = (byte)0x80;
			}
		}
		else
		{
			// slot I/O switches
			address &= 0x7F;
			final int islot = (address & 0xF0) >> 4;
			final int iswch = (address & 0x0F);
			r = this.slots.io(islot,iswch,(byte)0);
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
			this.slots.io(islot,iswch,data);
		}
	}
}
