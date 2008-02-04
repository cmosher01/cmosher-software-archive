/*
 * Created on Jan 21, 2008
 */
package video;

import chipset.TimingGenerator;
import chipset.TimingGeneratorAbstract;

public class PictureGenerator
{
	private final VideoDisplayDevice tv;
	private final VideoMode mode;

	private int latchGraphics;
	private boolean d7;
	private int latchText;
	private int hpos;
	private int line;

	public static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	public PictureGenerator(final VideoDisplayDevice tv, final VideoMode mode)
	{
		this.tv = tv;
		this.mode = mode;
	}

	private void shiftLoRes()
	{
		/*
		 * +--------+ +--------+
		 * |        | |        |
		 * +->ABCD->+ +->EFGH->+
		 * 
		 * ABCDEFGH --> DABCHEFG
		 */

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
		/*
		 * +--------+
		 * |        |
		 * +->ABCD->+--->EFGH
		 * 
		 * ABCDEFGH --> DABCDEFG
		 */

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
		this.latchText >>>= 1;
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

	private void loadGraphics(final int value)
	{
		this.latchGraphics = value & 0xFF;
		this.d7 = (this.latchGraphics & 0x80) != 0;
	}

	private void loadText(final int value)
	{
		this.latchText = value & 0xFF;
	}


	private boolean lasthires;
	public void tick(final int t, final int rowToPlot)
	{
		if (this.mode.isDisplayingText(t))
			loadText(rowToPlot);
		else
			loadGraphics(rowToPlot);

		if (t==0)
		{
			this.line = 0;
		}

		int cycles = TimingGeneratorAbstract.CRYSTAL_CYCLES_PER_CPU_CYCLE;
		if (this.hpos == TimingGeneratorAbstract.HORIZ_CYCLES-1)
		{
			cycles += TimingGeneratorAbstract.EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE;
		}

//		 hi-res half-pixel shift:
		final boolean shift = this.mode.isHiRes() && !this.mode.isDisplayingText(t) && this.d7 && this.line < VideoAddressing.VISIBLE_ROWS_PER_FIELD && !(this.hpos < VISIBLE_X_OFFSET);
		final boolean showLastHiRes = shift && this.lasthires;

		int xtra = 0;
		if (shift)
		{
			--cycles;
			++xtra;
		}
		final int firstBlankedCycle = TimingGeneratorAbstract.CRYSTAL_CYCLES_PER_CPU_CYCLE-xtra;

		for (int cycle = 0; cycle < cycles-1; ++cycle)
		{
			final boolean bit = shiftLatch(t,cycle);
			writeVideoSignal(shift,showLastHiRes,firstBlankedCycle,cycle,bit);
		}
		// optimization: pull the last iteration of the loop out, so we don't getHiResBit every time
		{
			this.lasthires = getHiResBit(); // save it for the next plotted byte, just in case we need it
			final int cycle = cycles-1;
			final boolean bit = shiftLatch(t,cycle);
			writeVideoSignal(shift,showLastHiRes,firstBlankedCycle,cycle,bit);
		}

		++this.hpos;
		if (this.hpos >= TimingGeneratorAbstract.HORIZ_CYCLES)
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

	private static final int[] lutCB = { AppleNTSC.BLANK_LEVEL,-AppleNTSC.CB_LEVEL,AppleNTSC.BLANK_LEVEL,+AppleNTSC.CB_LEVEL };
	private void writeVideoSignal(final boolean shift, final boolean showLastHiRes, final int firstBlankedCycle, final int cycle, final boolean bit)
	{
		if (shift && cycle==0)
		{
			this.tv.putSignal(showLastHiRes ? AppleNTSC.WHITE_LEVEL : AppleNTSC.BLANK_LEVEL);
		}
		final int hcycle = this.hpos*TimingGeneratorAbstract.CRYSTAL_CYCLES_PER_CPU_CYCLE+cycle;
		if (this.line < VideoAddressing.VISIBLE_ROWS_PER_FIELD)
		{
			if (this.hpos < VISIBLE_X_OFFSET) // HBL
			{
				final int cb;
				if (AppleNTSC.CB_START <= hcycle && hcycle < AppleNTSC.CB_END)
				{
					if (this.mode.isText()) // TODO && rev > 0
					{
						cb = AppleNTSC.BLANK_LEVEL;
					}
					else
					{
						cb = lutCB[(hcycle-AppleNTSC.CB_START)%4];
					}
				}
				else if (AppleNTSC.SYNC_START <= hcycle && hcycle < AppleNTSC.BP_START)
				{
					cb = AppleNTSC.SYNC_LEVEL;
				}
				else
				{
					cb = AppleNTSC.BLANK_LEVEL;
				}
				this.tv.putSignal(cb);
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
