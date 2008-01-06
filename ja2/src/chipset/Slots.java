/*
 * Created on Nov 30, 2007
 */
package chipset;

import config.Config;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import disk.DiskController;
import disk.InvalidDiskImage;

public class Slots implements Iterable<Card>
{
	private final List<Card> cards = new ArrayList<Card>(Config.SLOTS);


	public Slots()
	{
		this.cards.addAll(Collections.<Card>nCopies(Config.SLOTS,new EmptySlot()));
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

	public void loadDisk(int slot, int drive, File fnib) throws IOException, InvalidDiskImage
	{
		final Card card = this.cards.get(slot);
		if (!(card instanceof DiskController))
		{
			throw new IllegalArgumentException("Card in slot "+slot+" is not a disk controller card.");
		}
		final DiskController controller = (DiskController)card;
		controller.loadDisk(drive,fnib);
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
