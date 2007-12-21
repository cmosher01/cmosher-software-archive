/*
 * Created on Nov 28, 2007
 */
package chipset;

public class EmptySlot extends Card
{
	@Override
	public byte io(int address, byte data)
	{
		return data;
	}
}
