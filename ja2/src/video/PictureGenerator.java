/*
 * Created on Jan 21, 2008
 */
package video;

public class PictureGenerator
{
	private int latchGraphics;
	private int latchText;

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
		this.latchText = this.latchText >> 1;
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
	}

	public void loadText(final int value)
	{
		this.latchText = value & 0xFF;
	}
}
