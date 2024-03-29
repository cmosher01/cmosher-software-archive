/*
 * Created on Jan 21, 2008
 */
package video;

import chipset.TimingGenerator;
import display.AppleNTSC;

public class PictureGenerator
{
	private final VideoDisplayDevice display;
	private final VideoMode mode;

	private int latchGraphics;
	private boolean d7;
	private int latchText;
	private int hpos;
	private int line;

	public static final int VISIBLE_X_OFFSET = VideoAddressing.BYTES_PER_ROW-VideoAddressing.VISIBLE_BYTES_PER_ROW;

	public PictureGenerator(final VideoMode mode, final VideoDisplayDevice display)
	{
		this.mode = mode;
		this.display = display;
	}

	public void powerOn()
	{
		this.hpos = 0;
		this.line = 0;
    	this.display.restartSignal();
	}

	private void shiftLoRes()
	{
		/*
		 * For byte ABCDEFGH in register, perform
		 * the following 4-bit end-around shifts:
		 * 
		 * +---<----+   +---<----+
		 * |        |   |        |
		 * +->ABCD->+   +->EFGH->+
		 * 
		 * Therefore:
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
		 * For byte ABCDEFGH in register, perform
		 * the following shift:
		 * 
		 * +---<----+
		 * |        |
		 * +->ABCD->+--->EFGH->
		 * 
		 * Therefore:
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

//	private void shiftText()
//	{
//		this.latchText >>>= 1;
//	}

//	private boolean getTextBit()
//	{
//		return (this.latchText & 1) != 0;
//	}

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

		int cycles = TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE;
		if (this.hpos == TimingGenerator.HORIZ_CYCLES-1)
		{
			cycles += TimingGenerator.EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE;
		}

//		 hi-res half-pixel shift:
		final boolean shift = this.mode.isHiRes() && !this.mode.isDisplayingText(t) && this.d7 && this.line < VideoAddressing.VISIBLE_ROWS_PER_FIELD && !(this.hpos < VISIBLE_X_OFFSET); // TODO && rev > 0
		final boolean showLastHiRes = shift && this.lasthires;

		int xtra = 0;
		if (shift)
		{
			--cycles;
			++xtra;
		}
		final int firstBlankedCycle = TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE-xtra;

		final boolean lineVis = this.line < VideoAddressing.VISIBLE_ROWS_PER_FIELD;
		final boolean hVis = this.hpos >= VISIBLE_X_OFFSET;
		for (int cycle = 0; cycle < cycles-1; ++cycle)
		{
			/*
			 * Note: the body of this loop is the most executed code
			 * in the whole program. It should be able to execute at over
			 * 14 million times per second to achieve authentic Apple ][ speed.
			 * (Also note: right now, it doesn't achieve this.)
			 */
			final boolean bit = shiftLatch(t,cycle);
			writeVideoSignal(shift,showLastHiRes,firstBlankedCycle,cycle,bit,lineVis,hVis);
		}
		// optimization: pull the last iteration out of the loop, so we don't getHiResBit every time
		{
			this.lasthires = getHiResBit(); // save it for the next plotted byte, just in case we need it
			final int cycle = cycles-1;
			final boolean bit = shiftLatch(t,cycle);
			writeVideoSignal(shift,showLastHiRes,firstBlankedCycle,cycle,bit,lineVis,hVis);
		}

		++this.hpos;
		if (this.hpos >= TimingGenerator.HORIZ_CYCLES)
		{
			this.hpos = 0;
			++this.line;
		}
	}

//	private boolean shiftLatch(final int t, final int cycle)
//	{
//		final boolean bit;
//		if (this.mode.isDisplayingText(t))
//		{
//			bit = getTextBit();
//			if ((cycle & 1) != 0) // @ 7MHz
//			{
//				shiftText();
//			}
//		}
//		else if (this.mode.isHiRes())
//		{
//			bit = getHiResBit();
//			if ((cycle & 1) != 0) // @ 7MHz
//			{
//				shiftHiRes();
//			}
//		}
//		else // LO-RES
//		{
//			final int y = t / VideoAddressing.BYTES_PER_ROW;
//			bit = getLoResBit((t & 1) == (this.line & 1),(y & 4) != 0);
//			shiftLoRes();
//		}
//		return bit;
//	}

	private boolean shiftLatch(final int t, final int cycle)
	{
		final boolean bit;
		if (this.mode.isDisplayingText(t))
		{
			bit = (this.latchText & 1) != 0;
			if ((cycle & 1) != 0) // @ 7MHz
			{
				this.latchText >>>= 1;
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
	private void writeVideoSignal(final boolean shift, final boolean showLastHiRes, final int firstBlankedCycle, final int cycle, final boolean bit, final boolean lineVis, final boolean hVis)
	{
		if (cycle==0 && shift)
		{
			this.display.putSignal(showLastHiRes ? AppleNTSC.WHITE_LEVEL : AppleNTSC.BLANK_LEVEL);
		}

		final int hcycle = this.hpos*TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE+cycle;
		final int sig;
		if (lineVis)
		{
			if (hVis)
			{
				if (bit && cycle < firstBlankedCycle)
				{
					sig = AppleNTSC.WHITE_LEVEL;
				}
				else
				{
					sig = AppleNTSC.BLANK_LEVEL;
				}
			}
			else
			{
				sig = hbl(hcycle);
			}
		}
		else
		{
			sig = vbl(hcycle);
		}
		this.display.putSignal(sig);
	}

	private int vbl(final int hcycle)
	{
		final int sig;
		if (224 <= this.line && this.line < 240) // VSYNC
		{
			sig = AppleNTSC.SYNC_LEVEL;
		}
		else
		{
			if (AppleNTSC.SYNC_START <= hcycle && hcycle < AppleNTSC.BP_START)
			{
				sig = AppleNTSC.SYNC_LEVEL;
			}
			else
			{
				sig = AppleNTSC.BLANK_LEVEL;
			}
		}
		return sig;
	}

	private int hbl(final int hcycle)
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
		return cb;
	}
}
