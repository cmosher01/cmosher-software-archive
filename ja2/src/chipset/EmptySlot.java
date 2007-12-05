/*
 * Created on Nov 28, 2007
 */
package chipset;

public class EmptySlot implements Card
{
	public byte io(@SuppressWarnings("unused") final int address, final byte data)
	{
		// this really does nothing useful
		return data;
	}

	public void reset()
	{
		// do nothing
	}
}
