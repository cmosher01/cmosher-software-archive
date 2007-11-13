/*
 * Created on Sep 28, 2007
 */
package disk;

public class TapeInterface
{
	private final TapeDriveMemory tape;
	private int t;
	private int pt;
	private boolean on;

	public TapeInterface(final TapeDriveMemory tape)
	{
		this.tape = tape;
	}


	public byte io(final int addr, final byte data)
	{
		byte ret = -1;

		// addr: c020=write c060=read
		// 11000010 = write
		// 11000110 = read
		// 00000100 (mask)
		this.on = !this.on;
		int d = this.t - this.pt;
		this.pt = this.t;
		final boolean read = (addr & 0x0040) != 0;
//		System.out.print(read ? "R " : "W ");
//		System.out.print(this.on ? "+ " : ". ");
//		System.out.println(Integer.toString(d));
//		if (read)
//		{
//			ret = this.tape.get();
//		}
//		else
//		{
//			this.tape.set(data);
//		}

		return ret;
	}


	public void stopped()
	{
	}


	public void tick()
	{
		++this.t;
	}
}
