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

	public byte io(final int islot, final int iswch, final byte b, boolean writing)
	{
		return this.slots.get(islot).io(iswch,b,writing);
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

	private final byte[] rb = new byte[1];
	public byte readSeventhRom(int addr)
	{
		this.rb[0] = -1;

		for (final Card card : this.slots)
		{
			card.readSeventhRom(addr,this.rb);
		}

		return this.rb[0];
	}

	public byte ioBankRom(final int addr, byte data, boolean write)
	{
		this.rb[0] = data;

		for (final Card card : this.slots)
		{
			card.ioBankRom(addr,this.rb,write);
		}

		return this.rb[0];
	}

	public boolean inhibitMotherboardRom()
	{
		for (final Card card: this.slots)
		{
			if (card.inhibitMotherboardRom())
			{
				return true;
			}
		}
		return false;
	}
}
