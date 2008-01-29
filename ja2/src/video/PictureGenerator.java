/*
 * Created on Jan 21, 2008
 */
package video;

import chipset.TimingGenerator;

public class PictureGenerator
{
	private final VideoDisplayDevice tv;
	private final VideoMode mode;

	private int latchGraphics;
	private boolean d7;
	private int latchText;
	private int hpos;
	private int line;

	public PictureGenerator(final VideoDisplayDevice tv, final VideoMode mode)
	{
		this.tv = tv;
		this.mode = mode;
	}

	private void shiftLoRes()
	{
		// ABCDEFGH --> DABCHEFG


		int rot_bits = this.latchGraphics & 0x11;
		// 000D000H
		rot_bits <<= 3;
		// D000H000

		int sft_bits = this.latchGraphics & 0xEE;
		// ABC0EFG0
		sft_bits >>= 1;
		// 0ABC0EFG

		this.latchGraphics = (rot_bits | sft_bits) & 0xFF;
		// DABCHEFG
	}

	private void shiftHiRes()
	{
		// ABCDEFGH --> DABCDEFG


		int rot_bits = this.latchGraphics & 0x10;
		// 000D0000
		rot_bits <<= 3;
		// D0000000

		this.latchGraphics >>= 1;
		// 0ABCDEFG

		this.latchGraphics = (rot_bits | this.latchGraphics) & 0xFF;
		// DABCDEFG
	}

	private void shiftText()
	{
		this.latchText >>= 1;
	}

	private boolean getTextBit()
	{
		return (this.latchText & 1) != 0;
	}

	private boolean getHiResBit()
	{
		return (this.latchGraphics & 1) != 0;
	}

	private boolean getLoResBit(final boolean odd, final boolean vc)
	{
		final int nibble = (this.latchGraphics >> (vc ? 4 : 0)) & 0x0F;

		return ((nibble >> (odd ? 2 : 0)) & 1) != 0;
	}

	public void loadGraphics(final int value)
	{
		this.latchGraphics = value & 0xFF;
		this.d7 = (this.latchGraphics & 0x80) != 0;
	}

	public void loadText(final int value)
	{
		this.latchText = value & 0xFF;
	}

	public void resetFrame()
	{
		this.line = 0;
	}

	private boolean lasthires;
	public void tick(final int t)
	{
		int cycles = TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE;
		if (this.hpos == TimingGenerator.HORIZ_CYCLES-1)
		{
			cycles += TimingGenerator.EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE;
		}

//		 hi-res half-pixel shift:
		final boolean shift = this.mode.isHiRes() && !this.mode.isDisplayingText(t) && this.d7 && this.line < VideoAddressing.VISIBLE_ROWS_PER_FIELD && !(this.hpos < Video.VISIBLE_X_OFFSET);
		final boolean showLastHiRes = shift && this.lasthires;

		int xtra = 0;
		if (shift)
		{
			--cycles;
			++xtra;
		}
		final int firstBlankedCycle = TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE-xtra;

		for (int cycle = 0; cycle < cycles; ++cycle)
		{
			this.lasthires = getHiResBit(); // save it for the next plotted byte, just in case we need it
			final boolean bit = shiftLatch(t,cycle);
			writeVideoSignal(shift,showLastHiRes,firstBlankedCycle,cycle,bit);
		}

		++this.hpos;
		if (this.hpos >= TimingGenerator.HORIZ_CYCLES)
		{
			this.hpos = 0;
			++this.line;
		}
	}

	private boolean shiftLatch(final int t, final int cycle)
	{
		final boolean bit;
		if (this.mode.isDisplayingText(t))
		{
			bit = getTextBit();
			if ((cycle & 1) != 0) // @ 7MHz
			{
				shiftText();
			}
		}
		else if (this.mode.isHiRes())
		{
			bit = getHiResBit();
			if ((cycle & 1) != 0) // @ 7MHz
			{
				shiftHiRes();
			}
		}
		else // LO-RES
		{
			final int y = t / VideoAddressing.BYTES_PER_ROW;
			bit = getLoResBit((t & 1) == (this.line & 1),(y & 4) != 0);
			shiftLoRes();
		}
		return bit;
	}

	private void writeVideoSignal(final boolean shift, final boolean showLastHiRes, final int firstBlankedCycle, final int cycle, final boolean bit)
	{
		if (shift && cycle==0)
		{
			this.tv.putSignal(showLastHiRes ? AppleNTSC.WHITE_LEVEL : AppleNTSC.BLANK_LEVEL);
		}
		final int hcycle = this.hpos*TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE+cycle;
		if (this.line < VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			if (this.hpos < Video.VISIBLE_X_OFFSET) // HBL
			{
				if (AppleNTSC.CB_START <= hcycle && hcycle < AppleNTSC.CB_END)
				{
					final int cb = (((hcycle-AppleNTSC.CB_START)%4/2*2-1)*AppleNTSC.CB_LEVEL/2);
					this.tv.putSignal(cb);
				}
				else if (AppleNTSC.SYNC_START <= hcycle && hcycle < AppleNTSC.BP_START)
				{
					this.tv.putSignal(AppleNTSC.SYNC_LEVEL);
				}
				else
				{
					this.tv.putSignal(AppleNTSC.BLANK_LEVEL);
				}
			}
			else
			{
				final boolean visible = cycle < firstBlankedCycle;
				this.tv.putSignal(bit&visible ? AppleNTSC.WHITE_LEVEL : AppleNTSC.BLANK_LEVEL);
			}
		}
		else // VBL
		{
			if (224 <= this.line && this.line < 240) // VSYNC
			{
				this.tv.putSignal(AppleNTSC.SYNC_LEVEL);
			}
			else
			{
				final boolean hsync = AppleNTSC.SYNC_START <= hcycle && hcycle < AppleNTSC.BP_START;
				this.tv.putSignal(hsync ? AppleNTSC.SYNC_LEVEL : AppleNTSC.BLANK_LEVEL);
			}
		}
	}
}
