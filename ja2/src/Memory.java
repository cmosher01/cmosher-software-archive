/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private int[] ram = new int[0x10000];

	public int read(final int address)
	{
		return this.ram[address];
	}

	public void write(final int address, final int data)
	{
		this.ram[address] = data;
	}
}
