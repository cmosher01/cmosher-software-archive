/*
 * Created on Aug 2, 2007
 */
package test2;

public class Bus8 extends Bus
{
	private int bits8;

	public void put(final int bits8)
	{
		this.bits8 = bits8 & 0x000000FF;
	}

	public int get()
	{
		return this.bits8;
	}
}
