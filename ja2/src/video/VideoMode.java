/*
 * Created on Jan 8, 2008
 */
package video;

public class VideoMode
{
	private boolean swText = true;
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
}
