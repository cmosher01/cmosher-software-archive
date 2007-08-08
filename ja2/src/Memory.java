/*
 * Created on Aug 1, 2007
 */
public class Memory
{
	private int[] ram = new int[0x10000];

	public int read(final int address)
	{
//		System.out.println("r $"+Integer.toHexString(address));
		return this.ram[address % 0x10000];
	}

	public void write(final int address, final int data)
	{
//		System.out.println("w $"+Integer.toHexString(address));
		this.ram[address % 0x10000] = data;
	}
}
