/*
 * Created on Aug 2, 2007
 */
package test2;

public class Bus16 extends Bus
{
	private int bits16;

	public void put(final int bits16)
	{
		this.bits16 = bits16 & 0x0000FFFF;
	}

	public int get()
	{
		return this.bits16;
	}
}
