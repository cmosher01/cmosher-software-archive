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
}
