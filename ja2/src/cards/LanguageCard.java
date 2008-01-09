/*
 * Created on Dec 23, 2007
 */
package cards;

import chipset.Card;
import chipset.Memory;

public class LanguageCard extends Card
{
	private boolean inhibit;
	private final Memory[] ramBank = new Memory[2];
	private final Memory ramTop = new Memory(0x10000-0xE000);
	private int bank;
	private boolean readEnable;
	private boolean writeEnable = true;
	private int writeCount;

	public LanguageCard()
	{
		this.ramBank[this.bank++] = new Memory(0xE000-0xD000);
		this.ramBank[this.bank] = new Memory(0xE000-0xD000);
	}
	@Override
	public void reset()
	{
		// does nothing
	}

	@Override
	public byte io(final int address, final byte data, final boolean writing)
	{
		if ((address & 1) != 0 && !writing)
		{
			++this.writeCount;
		}
		else
		{
			this.writeCount = 0;
		}
		if (this.writeCount > 1)
		{
			this.writeEnable = true;
		}
		if ((address & 1) == 0)
		{
			this.writeEnable = false;
		}

		final int r = address & 3;
		this.readEnable = (r==0 || r==3);

		this.bank = ((address & 8) >> 3 != 0) ? 0 : 1;

		return data;
	}

	@Override
	public boolean inhibitMotherboardRom()
	{
		return this.inhibit;
	}

	@Override
	public void ioBankRom(final int addr, final byte[] rb, final boolean writing)
	{
		this.inhibit = false;
		if (this.readEnable && !writing)
		{
			if (0 <= addr && addr < 0x1000)
			{
				rb[0] = this.ramBank[this.bank].read(addr);
			}
			else
			{
				rb[0] = this.ramTop.read(addr-0x1000);
			}
			this.inhibit = true;
		}
		else if (this.writeEnable && writing)
		{
			if (0 <= addr && addr < 0x1000)
			{
				this.ramBank[this.bank].write(addr,rb[0]);
			}
			else
			{
				this.ramTop.write(addr-0x1000,rb[0]);
			}
		}
	}

	/**
	 * @return
	 */
	@Override
	public String getTypeName()
	{
		return "language card";
	}
}
