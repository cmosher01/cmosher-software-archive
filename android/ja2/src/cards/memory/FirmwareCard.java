/*
 * Created on Dec 23, 2007
 */
package cards.memory;

import java.io.InputStream;
import cards.Card;
import chipset.InvalidMemoryLoad;
import chipset.Memory;

public class FirmwareCard extends Card
{
	private boolean inhibitBankRom;
	private boolean inhibitF8Rom;
	private boolean inhibit;
	protected final Memory bankRom = new Memory(0x10000-0xD000);


	@Override
	public void reset()
	{
		this.inhibitBankRom = false;
		this.inhibitF8Rom = false;
	}



	@Override
	public byte io(final int address, final byte data, @SuppressWarnings("unused") final boolean writing)
	{
		this.inhibitBankRom = (address & 1) == 0;
		this.inhibitF8Rom = (address & 2) != 0;
		return data;
	}

	@Override
	public void loadBankRom(final int base, final InputStream file) throws InvalidMemoryLoad
	{
		this.bankRom.load(base,file);
	}



	@Override
	public boolean inhibitMotherboardRom()
	{
		return this.inhibit;
	}


	@Override
	public void ioBankRom(final int addr, final byte[] rb, @SuppressWarnings("unused") final boolean write)
	{
		this.inhibit = false;
		if (0 <= addr && addr < 0x2800)
		{
			if (this.inhibitBankRom)
			{
				rb[0] = this.bankRom.read(addr);
				this.inhibit = true;
			}
		}
		else if (0x2800 <= addr && addr < 0x3000)
		{
			if (this.inhibitF8Rom)
			{
				rb[0] = this.bankRom.read(addr);
				this.inhibit = true;
			}
		}
	}



	/**
	 * @return
	 */
	@Override
	public String getTypeName()
	{
		return "firmware card";
	}
}
