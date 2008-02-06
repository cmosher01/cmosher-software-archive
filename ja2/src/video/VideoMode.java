/*
 * Created on Jan 8, 2008
 */
package video;

public class VideoMode
{
	private static final int MIXED_TEXT_LINES = 4;
	private static final int ROWS_PER_TEXT_LINE = 8;
	private static final int MIXED_TEXT_CYCLE = (VideoAddressing.VISIBLE_ROWS_PER_FIELD-(MIXED_TEXT_LINES*ROWS_PER_TEXT_LINE))*VideoAddressing.BYTES_PER_ROW;



	private boolean swText;
	private boolean swMixed;
	private int swPage2;
	private boolean swHiRes;



	public byte io(final int addr, final byte b)
	{
		final int sw = (addr & 0x000E) >> 1;
		final boolean on = (addr & 0x0001) != 0;
		switch (sw)
		{
			case 0:
				this.swText = on; break;
			case 1:
				this.swMixed = on; break;
			case 2:
				this.swPage2 = on ? 1 : 0; break;
			case 3:
				this.swHiRes = on; break;
		}
		return b;
	}



	public boolean isText()
	{
		return this.swText;
	}

	public boolean isHiRes()
	{
		return this.swHiRes;
	}

	public boolean isMixed()
	{
		return this.swMixed;
	}

	public int getPage()
	{
		return this.swPage2;
	}

	public boolean isDisplayingText(final int atTickInField)
	{
		return this.swText || (this.swMixed && atTickInField >= MIXED_TEXT_CYCLE);
	}



	public void powerOn()
	{
		this.swText = false;
		this.swMixed = false;
		this.swPage2 = 0;
		this.swHiRes = false;;
	}
}
