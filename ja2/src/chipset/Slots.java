/*
 * Created on Nov 30, 2007
 */
package chipset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Slots
{
	private final List<Card> slots;

	public Slots(final Collection<Card> cards)
	{
		this.slots = new ArrayList<Card>(cards);
	}

	public byte io(final int islot, final int iswch, final byte b)
	{
		return this.slots.get(islot).io(iswch,b);
	}

	public void reset()
	{
		for (final Card card: this.slots)
		{
			card.reset();
		}
	}

	public byte readRom(final int islot, final int addr)
	{
		return this.slots.get(islot).readRom(addr);
	}

	public byte readSeventhRom(int addr)
	{
		final byte[] rb = new byte[1];
		rb[0] = -1;

		for (final Card card : this.slots)
		{
			card.readSeventhRom(addr,rb);
		}

		return rb[0];
	}
}
