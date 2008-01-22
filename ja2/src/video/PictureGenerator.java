/*
 * Created on Jan 21, 2008
 */
package video;

import java.awt.image.BufferedImage;
import chipset.TimingGenerator;

public class PictureGenerator
{
	private final AnalogTV tv;
	private final VideoMode mode;
	private final BufferedImage image;

	private int latchGraphics;
	private boolean d7;
	private int latchText;
	private int hpos;
	private int line;
	private int t;

	public PictureGenerator(final AnalogTV tv, final VideoMode mode, final BufferedImage image)
	{
		this.tv = tv;
		this.mode = mode;
		this.image = image;
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
		this.d7 = ((this.latchGraphics >> 7) & 1) != 0;
	}

	public void loadText(final int value)
	{
		this.latchText = value & 0xFF;
	}

	public void resetFrame()
	{
		this.tv.write_sync_signal();
		this.line = 0;
	}

	public void tick(int y)
	{
		int cycles = TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE;
		if (this.hpos == TimingGenerator.HORIZ_CYCLES-1)
		{
			cycles += TimingGenerator.EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE;
			// TODO: fix hi-res half-pixel shift logic
//			if (this.mode.isHiRes() && this.d7) // hi-res half-pixel shift
//			{
//				--cycles; // cut off shifted pixel at right edge of screen
//			}
		}
		if (line > VideoAddressing.VISIBLE_ROWS_PER_FIELD || hpos < Video.VISIBLE_X_OFFSET)
		{
			this.tv.skip(cycles);
		}
		else
		{
			int base = 0;
			if (this.mode.isHiRes() && this.d7) // hi-res half-pixel shift
			{
				this.tv.write_signal(AppleNTSC.BLANK_LEVEL);
				++base;
			}
			for (int cycle = base; cycle < cycles; ++cycle)
			{
				if (this.mode.isDisplayingText(this.t))
				{
					final boolean bit = getTextBit();
					this.tv.write_signal(bit ? AppleNTSC.WHITE_LEVEL : AppleNTSC.BLANK_LEVEL);
					if ((cycle & 1) != 0) // @ 7MHz
					{
						shiftText();
					}
				}
				else if (this.mode.isHiRes())
				{
					final boolean bit = getHiResBit();
					this.tv.write_signal(bit ? AppleNTSC.WHITE_LEVEL : AppleNTSC.BLANK_LEVEL);
					if ((cycle & 1) != 0) // @ 7MHz
					{
						shiftHiRes();
					}
				}
				else
				{
					final boolean bit = getLoResBit((this.t & 1) == (this.line & 1),(y & 4) != 0);
					this.tv.write_signal(bit ? AppleNTSC.WHITE_LEVEL : AppleNTSC.BLANK_LEVEL);
					shiftLoRes();
				}
			}
		}

		++this.hpos;
		if (this.hpos >= TimingGenerator.HORIZ_CYCLES)
		{
			this.hpos = 0;
			++this.line;
		}

		++this.t;
		if (this.t >= 17030)
		{
			this.tv.test_draw(this.image);
			this.t = 0;
		}
	}
}
