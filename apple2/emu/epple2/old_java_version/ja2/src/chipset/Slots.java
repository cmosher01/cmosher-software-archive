/*
 * Created on Nov 30, 2007
 */
package chipset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import cards.Card;
import cards.EmptySlot;
import cards.disk.DiskController;

public class Slots implements Iterable<Card>
{
	public static final int SLOTS = 8;

	private final List<Card> cards = new ArrayList<Card>(SLOTS);


	public Slots()
	{
		this.cards.addAll(Collections.<Card>nCopies(SLOTS,new EmptySlot()));
	}

	public byte io(final int islot, final int iswch, final byte b, boolean writing)
	{
		return this.cards.get(islot).io(iswch,b,writing);
	}

	public void reset()
	{
		for (final Card card: this.cards)
		{
			card.reset();
		}
	}

	public byte readRom(final int islot, final int addr)
	{
		return this.cards.get(islot).readRom(addr);
	}

	private final byte[] rb = new byte[1];
	public byte readSeventhRom(int addr)
	{
		this.rb[0] = -1;

		for (final Card card : this.cards)
		{
			card.readSeventhRom(addr,this.rb);
		}

		return this.rb[0];
	}

	public byte ioBankRom(final int addr, byte data, boolean write)
	{
		this.rb[0] = data;

		for (final Card card : this.cards)
		{
			card.ioBankRom(addr,this.rb,write);
		}

		return this.rb[0];
	}

	public boolean inhibitMotherboardRom()
	{
		for (final Card card: this.cards)
		{
			if (card.inhibitMotherboardRom())
			{
				return true;
			}
		}
		return false;
	}

	public void set(final int slot, final Card card)
	{
		this.cards.set(slot,card);
	}

	public Card get(final int slot)
	{
		return this.cards.get(slot);
	}

	public Iterator<Card> iterator()
	{
		return this.cards.iterator();
	}

	public boolean isAnyDiskDriveMotorOn()
	{
		for (final Card card: this.cards)
		{
			if (card instanceof DiskController)
			{
				final DiskController disk = (DiskController)card;
				if (disk.isMotorOn())
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAnyDiskDirty()
	{
		for (final Card card: this.cards)
		{
			if (card instanceof DiskController)
			{
				final DiskController disk = (DiskController)card;
				if (disk.isDirty())
				{
					return true;
				}
			}
		}
		return false;
	}
}
