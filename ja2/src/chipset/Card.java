/*
 * Created on Nov 28, 2007
 */
package chipset;

import java.io.InputStream;

public abstract class Card
{
	protected final Memory rom = new Memory(0x100);
	private boolean activeSeventhRom;
	protected final Memory seventhRom = new Memory(0x800);



	// override
	public void reset()
	{
	}



	// override
	public byte io(int address, byte data)
	{
		return data;
	}



	public byte readRom(int address)
	{
		this.activeSeventhRom = true;
		return this.rom.read(address);
	}

	public void readSeventhRom(int address, byte[] rb)
	{
		if (address == 0x7FF)
		{
			this.activeSeventhRom = false;
		}
		else if (this.activeSeventhRom)
		{
			rb[0] = seventhRom.read(address);
		}
	}

	public void loadRom(final InputStream file) throws InvalidMemoryLoad
	{
		this.rom.load(0,file);
	}

	public void loadSeventhRom(final InputStream file) throws InvalidMemoryLoad
	{
		this.seventhRom.load(0,file);
	}
}
