/*
 * Created on Sep 28, 2007
 */
package disk;

public class TapeDriveMemory
{
	private final byte buf[] = new byte[0x10000]; // a tape as big as RAM for now
	private int ir;
	private int iw;

	public byte get()
	{
		return this.buf[nextRead()];
	}

	public void set(byte data)
	{
		this.buf[nextWrite()] = data;
	}

	public void rewind()
	{
		this.ir = 0;
	}

	private int nextRead()
	{
		int i = this.ir;
		++this.ir;
		if (this.ir >= this.buf.length)
		{
			this.ir = 0;
		}
		return i;
	}

	private int nextWrite()
	{
		int i = this.iw;
		++this.iw;
		if (this.iw >= this.buf.length)
		{
			this.iw = 0;
		}
		return i;
	}
}
